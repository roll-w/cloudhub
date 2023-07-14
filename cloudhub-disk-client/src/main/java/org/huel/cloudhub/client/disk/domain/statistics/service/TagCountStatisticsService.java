package org.huel.cloudhub.client.disk.domain.statistics.service;

import org.huel.cloudhub.client.disk.domain.statistics.StatisticJobTask;
import org.huel.cloudhub.client.disk.domain.statistics.TagGroupValueCount;
import org.huel.cloudhub.client.disk.domain.statistics.TagValueCount;
import org.huel.cloudhub.client.disk.domain.statistics.common.StatisticsKeys;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEvent;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEventRegistry;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEventType;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEventTypes;
import org.huel.cloudhub.client.disk.domain.tag.ContentTagProvider;
import org.huel.cloudhub.client.disk.domain.tag.TaggedValue;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAttributesService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageTagValueIterator;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;
import org.huel.cloudhub.client.disk.jobs.JobEvent;
import org.huel.cloudhub.client.disk.jobs.JobRegistry;
import org.huel.cloudhub.client.disk.jobs.trigger.ConditionTrigger;
import org.huel.cloudhub.client.disk.jobs.trigger.ConditionTriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
@Service
public class TagCountStatisticsService implements StatisticJobTask, StatisticsPersistable {
    private static final Logger logger = LoggerFactory.getLogger(TagCountStatisticsService.class);

    private final AtomicLong statisticsVersion = new AtomicLong(0L);
    // tagGroupId -> {tagId -> count}
    private final Map<Long, List<TagCount>> tagCounts = new HashMap<>();
    private final ContentTagProvider contentTagProvider;
    private final StorageAttributesService storageAttributesService;

    public TagCountStatisticsService(JobRegistry jobRegistry,
                                     StorageProcessingEventRegistry storageProcessingEventRegistry,
                                     ContentTagProvider contentTagProvider,
                                     StorageAttributesService storageAttributesService) {
        this.contentTagProvider = contentTagProvider;
        this.storageAttributesService = storageAttributesService;
        jobRegistry.register(this, new ConditionTrigger<>(
                storageProcessingEventRegistry,
                StorageProcessingEventTypes.of(
                        StorageProcessingEventType.CREATE,
                        StorageProcessingEventType.DELETE,
                        StorageProcessingEventType.UPDATE
                )
        ));
    }

    private final Deque<StorageProcessingEvent>
            waitQueue = new LinkedList<>();
    private final AtomicBoolean rescanWaiting
            = new AtomicBoolean(false);

    @Override
    public void execute(JobEvent jobEvent) {
        StorageProcessingEvent storageProcessingEvent =
                checkEvent(jobEvent);
        if (storageProcessingEvent == null) {
            return;
        }
        if (rescanWaiting.get()) {
            waitQueue.add(storageProcessingEvent);
            return;
        }
        logger.debug("Got storage processing event: status: {}, storage: {}, size: {}, taggedValues: {}",
                storageProcessingEvent.getStorageProcessEventType(),
                storageProcessingEvent.getStorage(),
                storageProcessingEvent.getSize(),
                storageProcessingEvent.getTaggedValues()
        );
        processStorageProcessingEvent(storageProcessingEvent);
    }

    private void processStorageProcessingEvent(StorageProcessingEvent storageProcessingEvent) {
        List<TaggedValue> taggedValues =
                storageProcessingEvent.getTaggedValues();
        if (taggedValues == null || taggedValues.isEmpty()) {
            return;
        }

        switch (storageProcessingEvent.getStorageProcessEventType()) {
            case CREATE -> taggedValues.forEach(taggedValue -> addTaggedValue(taggedValue, tagCounts));
            case DELETE -> taggedValues.forEach(taggedValue -> removeTaggedValue(taggedValue, tagCounts));
            default -> {
            }
        }
        updateStatisticsVersion();
    }

    private void addTaggedValue(TaggedValue taggedValue,
                                Map<Long, List<TagCount>> tagCountMap) {
        long tagGroupId = taggedValue.groupId();
        long tagId = taggedValue.tagId();
        List<TagCount> tagCountList =
                tagCountMap.getOrDefault(tagGroupId, new ArrayList<>());
        if (!tagCountMap.containsKey(tagGroupId)) {
            tagCountMap.put(tagGroupId, tagCountList);
        }

        boolean found = false;
        for (TagCount tagCount : tagCountList) {
            if (tagCount.getTagId() == tagId) {
                tagCount.increment();
                found = true;
                break;
            }
        }
        if (!found) {
            tagCountList.add(new TagCount(tagId, 1));
        }

    }

    private void removeTaggedValue(TaggedValue taggedValue,
                                   Map<Long, List<TagCount>> tagCountMap) {
        long tagGroupId = taggedValue.groupId();
        long tagId = taggedValue.tagId();
        List<TagCount> tagCountList = tagCountMap.get(tagGroupId);
        if (tagCountList == null) {
            return;
        }
        for (TagCount tagCount : tagCountList) {
            if (tagCount.getTagId() == tagId) {
                tagCount.decrement();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private StorageProcessingEvent checkEvent(JobEvent jobEvent) {
        if (!(jobEvent instanceof ConditionTriggerEvent<?, ?> conditionTriggerEvent)) {
            return null;
        }
        ConditionTriggerEvent<StorageProcessingEvent, StorageProcessingEventTypes> event =
                (ConditionTriggerEvent<StorageProcessingEvent, StorageProcessingEventTypes>)
                        conditionTriggerEvent;
        return event.getEvent();
    }

    @NonNull
    @Override
    public String getStatisticsKey() {
        return StatisticsKeys.TAG_COUNT;
    }

    @Override
    public Map<String, String> getStatistics() {
        Map<String, String> statistics = new HashMap<>();
        Map<Long, List<ImmutableTagCount>>
                snapshot = takeSnapshot();
        for (Map.Entry<Long, List<ImmutableTagCount>> entry : snapshot.entrySet()) {
            long tagGroupId = entry.getKey();
            List<ImmutableTagCount> tagCounts = entry.getValue();
            statistics.put(
                    String.valueOf(tagGroupId),
                    serializeTagCount(tagCounts)
            );
        }
        return statistics;
    }

    private String serializeTagCount(List<ImmutableTagCount> tagCounts) {
        StringBuilder sb = new StringBuilder();
        for (ImmutableTagCount tagCount : tagCounts) {
            sb.append(tagCount.tagId())
                    .append(":")
                    .append(tagCount.count())
                    .append(",");
        }
        return sb.toString();
    }

    private List<ImmutableTagCount> deserializeTagCount(
            String tagCountStr) {
        List<ImmutableTagCount> tagCounts = new ArrayList<>();
        String[] tagCountArr = tagCountStr.split(",");
        for (String tagCount : tagCountArr) {
            String[] tagCountPair = tagCount.split(":");
            if (tagCountPair.length != 2) {
                continue;
            }
            long tagId = Long.parseLong(tagCountPair[0]);
            int count = Integer.parseInt(tagCountPair[1]);
            tagCounts.add(new ImmutableTagCount(tagId, count));
        }
        return tagCounts;
    }

    public Map<Long, List<ImmutableTagCount>> takeSnapshot() {
        Map<Long, List<ImmutableTagCount>> snapshot = new HashMap<>();
        for (Map.Entry<Long, List<TagCount>> entry : tagCounts.entrySet()) {
            List<TagCount> tagCountList = entry.getValue();
            List<ImmutableTagCount> immutableTagCounts = new ArrayList<>(tagCountList.size());
            for (TagCount tagCount : tagCountList) {
                immutableTagCounts.add(ImmutableTagCount.from(tagCount));
            }
            snapshot.put(entry.getKey(), immutableTagCounts);
        }
        return snapshot;
    }

    @Override
    public long getStatisticsVersion() {
        return statisticsVersion.get();
    }

    private void updateStatisticsVersion() {
        statisticsVersion.incrementAndGet();
    }

    @Override
    public void rescanStatistics() {
        if (!rescanWaiting.compareAndSet(false, true)) {
            logger.debug("Rescan tag count already processing.");
            return;
        }
        logger.debug("Rescan tag count start.");
        Map<Long, List<TagCount>> curTagCounts = new HashMap<>();
        StorageTagValueIterator storageTagValueIterator =
                storageAttributesService.getStorageTagValueIterator();
        while (storageTagValueIterator.hasNext()) {
            List<StorageTagValue> storageTagValues =
                    storageTagValueIterator.next();
            storageTagValues.forEach(taggedValue ->
                    addTaggedValue(taggedValue, curTagCounts)
            );
        }

        replaceTagCounts(curTagCounts);
        updateStatisticsVersion();
        restartProcessing();
    }

    private void replaceTagCounts(
            Map<Long, List<TagCount>> newCounts) {
        this.tagCounts.clear();
        this.tagCounts.putAll(newCounts);
    }

    private void restartProcessing() {
        rescanWaiting.set(false);
        // process wait queue
        while (!waitQueue.isEmpty()) {
            StorageProcessingEvent storageProcessingEvent = waitQueue.poll();
            if (storageProcessingEvent == null) {
                break;
            }
            processStorageProcessingEvent(storageProcessingEvent);
        }
    }

    @NonNull
    @Override
    public Map<String, Object> getStatistics(
            Map<String, String> rawStatistics) {
        Map<Long, List<ImmutableTagCount>> tagCounts = new HashMap<>();
        Map<String, TagGroupValueCount> statistics = getTaggedValuesFromRaw(
                rawStatistics, tagCounts);
        return new HashMap<>(statistics);
    }

    @NonNull
    private static Map<String, TagGroupValueCount> pairToValueCount(
            Map<Long, List<ImmutableTagCount>> tagCounts,
            List<TagGroupInfo> groupInfos,
            List<ContentTagInfo> tagInfos) {
        Map<String, TagGroupValueCount> statistics = new HashMap<>();
        for (Map.Entry<Long, List<ImmutableTagCount>> count :
                tagCounts.entrySet()) {
            long tagGroupId = count.getKey();
            TagGroupInfo tagGroupInfo = groupInfos.stream()
                    .filter(info -> info.id() == tagGroupId)
                    .findFirst()
                    .orElse(null);
            List<ImmutableTagCount> tagCountList = count.getValue();
            if (tagGroupInfo == null) {
                continue;
            }
            List<TagValueCount> tagValueCounts = getTagValueCounts(tagInfos, tagCountList);
            TagGroupValueCount tagGroupValueCount = new TagGroupValueCount(
                    tagGroupInfo.id(), tagGroupInfo.name(), tagValueCounts);
            statistics.put(String.valueOf(tagGroupId), tagGroupValueCount);
        }
        return statistics;
    }

    @NonNull
    private static List<TagValueCount> getTagValueCounts(List<ContentTagInfo> tagInfos,
                                                         List<ImmutableTagCount> tagCountList) {
        List<TagValueCount> tagValueCounts = new ArrayList<>();
        tagCountList.forEach(tagCount -> {
            ContentTagInfo tagInfo = tagInfos.stream()
                    .filter(info -> info.id() == tagCount.tagId())
                    .findFirst()
                    .orElse(null);
            if (tagInfo == null) {
                return;
            }
            TagValueCount tagValueCount = new TagValueCount(
                    tagInfo.id(), tagInfo.name(), tagCount.count());
            tagValueCounts.add(tagValueCount);
        });
        return tagValueCounts;
    }

    @NonNull
    private Map<String, TagGroupValueCount> getTaggedValuesFromRaw(Map<String, String> rawStatistics, Map<Long, List<ImmutableTagCount>> tagCounts) {
        for (Map.Entry<String, String> entry : rawStatistics.entrySet()) {
            long tagGroupId = Long.parseLong(entry.getKey());
            String tagCountStr = entry.getValue();
            List<ImmutableTagCount> immutableTagCounts =
                    deserializeTagCount(tagCountStr);
            tagCounts.put(tagGroupId, immutableTagCounts);
        }
        List<Long> tagGroupIds = new ArrayList<>(tagCounts.keySet());
        List<Long> tagIds = tagCounts.values().stream()
                .flatMap(Collection::stream)
                .map(ImmutableTagCount::tagId)
                .distinct()
                .toList();
        List<TagGroupInfo> tagGroupInfos =
                contentTagProvider.getTagGroupInfos(tagGroupIds);
        List<ContentTagInfo> tagInfos =
                contentTagProvider.getTags(tagIds);
        return pairToValueCount(tagCounts, tagGroupInfos, tagInfos);
    }


    private static final class TagCount {
        private final long tagId;
        private final AtomicInteger count;

        private TagCount(long tagId, int count) {
            this.tagId = tagId;
            this.count = new AtomicInteger(count);
        }

        public long getTagId() {
            return tagId;
        }

        public int getCount() {
            return count.get();
        }

        public void setCount(int count) {
            this.count.set(count);
        }

        public void increment() {
            this.count.incrementAndGet();
        }

        public void decrement() {
            this.count.decrementAndGet();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TagCount) obj;
            return this.tagId == that.tagId &&
                    this.count == that.count;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tagId, count);
        }

        @Override
        public String toString() {
            return "TagCount[" +
                    "tagId=" + tagId + ", " +
                    "count=" + count + ']';
        }
    }

    private record ImmutableTagCount(
            long tagId,
            int count) {
        TagCount toTagCount() {
            return new TagCount(tagId, count);
        }


        static ImmutableTagCount from(TagCount tagCount) {
            return new ImmutableTagCount(
                    tagCount.getTagId(),
                    tagCount.getCount()
            );
        }
    }

}

package org.huel.cloudhub.client.disk.controller.tag;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.controller.OneParameterRequest;
import org.huel.cloudhub.client.disk.controller.tag.vo.*;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorProvider;
import org.huel.cloudhub.client.disk.domain.tag.*;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.client.disk.system.pages.PageableInterceptor;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class ContentTagController {
    private final ContentTagService contentTagService;
    private final ContentTagProvider contentTagProvider;
    private final PageableInterceptor pageableInterceptor;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;

    public ContentTagController(ContentTagService contentTagService,
                                ContentTagProvider contentTagProvider,
                                PageableInterceptor pageableInterceptor, SystemResourceOperatorProvider systemResourceOperatorProvider) {
        this.contentTagService = contentTagService;
        this.contentTagProvider = contentTagProvider;
        this.pageableInterceptor = pageableInterceptor;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
    }

    @GetMapping("/tags")
    public HttpResponseEntity<List<ContentTagInfo>> getTags(
            Pageable pageable) {
        List<ContentTagInfo> contentTagInfos =
                contentTagService.getTags(pageable);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        contentTagInfos,
                        pageable,
                        ContentTag.class
                )
        );
    }

    @GetMapping("/tags/{tagId}")
    public HttpResponseEntity<ContentTagInfo> getTag(
            @PathVariable("tagId") Long id) {
        return HttpResponseEntity.success(
                contentTagProvider.getTagById(id)
        );
    }

    @GetMapping("/tags/groups")
    public HttpResponseEntity<List<TagGroupVo>> getTagGroups(
            Pageable pageable) {
        List<TagGroupDto> tagGroupDtos =
                contentTagService.getTagGroups(pageable);

        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        tagGroupDtos.stream()
                                .map(TagGroupVo::from)
                                .toList(),
                        pageable,
                        ContentTag.class
                )
        );
    }

    @GetMapping("/tags/groups/{groupId}")
    public HttpResponseEntity<TagGroupDto> getTagGroup(
            @PathVariable("groupId") Long groupId) {

        return HttpResponseEntity.success(
                contentTagProvider.getTagGroupById(groupId)
        );
    }

    @PostMapping("/tags/groups")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG_GROUP)
    public HttpResponseEntity<Void> createTagGroup(
            @RequestBody TagGroupCreateRequest request) {
        contentTagService.createContentTagGroup(
                request.name(),
                request.description(),
                request.keywordSearchScope()
        );
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/groups/{groupId}")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG_GROUP)
    public HttpResponseEntity<Void> updateTagGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody TagGroupUpdateRequest request) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .rename(request.name())
                .setDescription(request.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/groups/{groupId}/tags/{tagId}")
    public HttpResponseEntity<Void> addTagToGroup(
            @PathVariable("groupId") Long groupId,
            @PathVariable("tagId") Long tagId) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .addTag(tagId)
                .update();
        return HttpResponseEntity.success();
    }

    @PostMapping("/tags/groups/{groupId}/tags")
    public HttpResponseEntity<Void> addTagToGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody OneParameterRequest<String> request) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        ContentTagInfo contentTagInfo =
                contentTagProvider.getByName(request.value());
        tagGroupOperator.disableAutoUpdate()
                .addTag(contentTagInfo.id())
                .update();
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/groups/{groupId}/tags/{tagId}")
    public HttpResponseEntity<Void> removeTagFromGroup(
            @PathVariable("groupId") Long groupId,
            @PathVariable("tagId") Long tagId) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .removeTag(tagId)
                .update();
        return HttpResponseEntity.success();
    }


    @PostMapping("/tags/groups/{groupId}/infile")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG)
    public HttpResponseEntity<Void> importTags(
            @PathVariable("groupId") Long groupId,
            @RequestPart(name = "file") MultipartFile file) throws IOException {
        contentTagService.importFromKeywordsFile(file.getInputStream(), groupId);

        return HttpResponseEntity.success();
    }

    @GetMapping("/tags/groups/{groupId}/infile")
    public void exportTags(HttpServletResponse servletResponse) {
        // exports
    }

    @PostMapping("/tags")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG)
    public HttpResponseEntity<Void> createTag(
            @RequestBody TagCreateRequest request) {
        contentTagService.createContentTag(
                request.name(),
                request.description(),
                request.keywords()
        );
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/{tagId}")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> updateTag(
            @PathVariable("tagId") Long tagId,
            @RequestBody TagUpdateRequest request) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.disableAutoUpdate()
                .rename(request.name())
                .setDescription(request.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/{tagId}/keywords")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> setKeyword(
            @PathVariable("tagId") Long tagId,
            @RequestBody TagKeyword tagKeyword) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .addKeyword(tagKeyword);
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/{tagId}/keywords")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> deleteKeyword(
            @PathVariable("tagId") Long tagId,
            @RequestParam("name") String keywordName) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .removeKeyword(TagKeyword.of(keywordName));
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/{tagId}")
    @BuiltinOperate(BuiltinOperationType.DELETE_TAG)
    public HttpResponseEntity<Void> deleteTag(
            @PathVariable("tagId") Long tagId) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .delete();
        return HttpResponseEntity.success();
    }
}

package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.meta.SerializeBlockFileMeta;

import java.util.*;

/**
 * Readonly container, every modification will
 * create a new version of container.
 *
 * @author RollW
 */
public class Container {
    public static final int CALC_BYCONT = -1;

    private int usedBlock;
    private final ContainerLocation location;
    private final ContainerFileNameMeta meta;
    private final ContainerIdentity identity;
    private final List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();

    private boolean valid;

    public Container(@NonNull ContainerLocation location,
                     int usedBlock,
                     @NonNull ContainerFileNameMeta meta,
                     @NonNull ContainerIdentity identity,
                     @NonNull Collection<BlockMetaInfo> blockMetaInfos,
                     boolean valid) {
        this.usedBlock = usedBlock;
        this.location = location;
        this.meta = meta;
        this.identity = identity;
        this.valid = valid;
        this.blockMetaInfos.addAll(blockMetaInfos);
        if (usedBlock <= 0) {
           addOnUsed(this.blockMetaInfos);
        }
    }

    public void addBlockMetaInfos(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null) {
            return;
        }
        this.blockMetaInfos.addAll(blockMetaInfos);
        addOnUsed(blockMetaInfos);
    }

    public void addBlockMetaInfos(BlockMetaInfo... blockMetaInfos) {
        addBlockMetaInfos(List.of(blockMetaInfos));
    }

    public void setBlockMetaInfos(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null) {
            return;
        }
        this.blockMetaInfos.clear();
        this.blockMetaInfos.addAll(blockMetaInfos);
        addOnUsed(blockMetaInfos);
    }

    public void setBlockMetaInfos(BlockMetaInfo... blockMetaInfos) {
        setBlockMetaInfos(List.of(blockMetaInfos));
    }

    public List<BlockMetaInfo> getBlockMetaInfos() {
        return Collections.unmodifiableList(blockMetaInfos);
    }

    public List<SerializeBlockFileMeta> getSerializeMetaInfos() {
        List<SerializeBlockFileMeta> serializeBlockFileMetas =
                new ArrayList<>();
        getBlockMetaInfos().forEach(blockMetaInfo ->
                serializeBlockFileMetas.add(blockMetaInfo.serialize()));
        return serializeBlockFileMetas;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid() {
        this.valid = true;
    }

    public ContainerLocation getLocation() {
        return location;
    }

    public String getResourceLocator() {
        return location.getResourceLocator();
    }

    public ContainerIdentity getIdentity() {
        return identity;
    }

    public ContainerFileNameMeta getSimpleMeta() {
        return meta;
    }

    private void addOnUsed(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null || blockMetaInfos.isEmpty()) {
            return;
        }
        final int blocks = blockMetaInfos.stream()
                .mapToInt(BlockMetaInfo::occupiedBlocks).sum();
        usedBlock += blocks;
    }

    public int getUsedBlock() {
        return usedBlock;
    }

    public boolean isReachLimit() {
        return usedBlock == identity.blockLimit();
    }

    public long calcLimitBytes() {
        return identity.blockSizeBytes() * identity.blockLimit();
    }

    public boolean hasFileId(String fileId) {
        for (BlockMetaInfo blockMetaInfo : this.blockMetaInfos) {
            if (blockMetaInfo.getFileId().equals(fileId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return usedBlock == container.usedBlock && valid == container.valid && Objects.equals(location, container.location) && Objects.equals(meta, container.meta) && Objects.equals(identity, container.identity) && Objects.equals(blockMetaInfos, container.blockMetaInfos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usedBlock, location, meta, identity, blockMetaInfos, valid);
    }

    @Override
    public String toString() {
        return "Container{" +
                "usedBlock=" + usedBlock +
                ", location=" + location +
                ", meta=" + meta +
                ", identity=" + identity +
                ", blockMetaInfos=" + blockMetaInfos +
                ", valid=" + valid +
                '}';
    }
}

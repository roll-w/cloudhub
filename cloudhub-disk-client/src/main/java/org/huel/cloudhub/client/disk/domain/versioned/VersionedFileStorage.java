package org.huel.cloudhub.client.disk.domain.versioned;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "versioned_file_storage", indices = {
        @Index(value = {"file_id", "version"}, unique = true)
})
public class VersionedFileStorage {
    private static final long INVALID_VERSION = 0;

    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "file_id")
    private final long fileId;

    @DataColumn(name = "version")
    private final long version;

    @DataColumn(name = "operator")
    private final long operator;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    public VersionedFileStorage(Long id, long fileId, long version,
                                long operator, long createTime) {
        this.id = id;
        this.fileId = fileId;
        this.version = version;
        this.operator = operator;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public long getFileId() {
        return fileId;
    }

    public long getVersion() {
        return version;
    }

    public long getOperator() {
        return operator;
    }

    public long getCreateTime() {
        return createTime;
    }
}

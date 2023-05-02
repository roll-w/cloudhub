package org.huel.cloudhub.client.disk.domain.tag;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "content_tag")
public class ContentTag implements SystemResource {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "description")
    private final String description;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public ContentTag(Long id, String name,
                      String description,
                      long createTime,
                      long updateTime,
                      boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public long getResourceId() {
        return getId();
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.TAG;
    }
}

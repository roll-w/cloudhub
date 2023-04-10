package org.huel.cloudhub.objectstorage.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * 文件引用。当引用数为0时，及时删除释放资源。
 *
 * @author RollW
 */
@DataTable(name = "file_reference_table")
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120")
public class FileReference {
    @DataColumn(name = "file_id")
    @PrimaryKey
    private String fileId;

    @DataColumn(name = "reference_num")
    private long referenceNum;

    public FileReference(String fileId, long referenceNum) {
        this.fileId = fileId;
        this.referenceNum = referenceNum;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getReferenceNum() {
        return referenceNum;
    }

    public void decrease() {
        referenceNum--;
    }

    public void increase() {
        referenceNum++;
    }
}

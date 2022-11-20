package org.huel.cloudhub.client.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;

/**
 * 文件引用。当引用数为0时，及时删除释放资源。
 *
 * @author RollW
 */
@DataTable(tableName = "file_reference_table")
public class FileReference {
    // TODO: file reference
    @DataColumn(name = "file_id")
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

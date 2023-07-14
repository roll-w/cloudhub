package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.tag.TaggedValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StorageMetadataDao
        extends AutoPrimaryBaseDao<StorageMetadata>, DaoConnectionGetter {

    @Query("SELECT * FROM storage_metadata WHERE storage_id = {storageId}")
    List<StorageMetadata> getByStorageId(long storageId);

    @Query("SELECT * FROM storage_metadata WHERE storage_id = {storageId} AND tag_group_id = {tagGroupId}")
    StorageMetadata getByStorageIdAndTagGroupId(long storageId, long tagGroupId);

    @Query("SELECT * FROM storage_metadata WHERE tag_id = {tagId}")
    List<StorageMetadata> getByTagId(long tagId);

    @Query("SELECT * FROM storage_metadata WHERE storage_id = {storageId} AND name = {name}")
    StorageMetadata getByStorageIdAndName(long storageId, String name);

    default List<StorageMetadata> getByTagValues(List<? extends TaggedValue> taggedValues) {
        if (taggedValues.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("SELECT " +
                "id, storage_id, tag_group_id, tag_id, deleted, create_time, update_time " +
                "FROM storage_metadata WHERE ");
        for (int i = 0; i < taggedValues.size(); i++) {
            sql.append("tag_group_id = ")
                    .append("?")
                    .append(" AND tag_id = ")
                    .append("?");
            if (i != taggedValues.size() - 1) {
                sql.append(" OR ");
            }
        }
        ManagedConnection connection = getConnection();
        PreparedStatement statement = connection.acquire(sql.toString());
        int index = 1;
        for (TaggedValue taggedValue : taggedValues) {
            try {
                statement.setLong(index++, taggedValue.groupId());
                statement.setLong(index++, taggedValue.tagId());
            } catch (Exception e) {
                throw new LightRuntimeException(e);
            }
        }
        List<StorageMetadata> storageMetadata = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                storageMetadata.add(new StorageMetadata(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getLong(4),
                        resultSet.getBoolean(5),
                        resultSet.getLong(6),
                        resultSet.getLong(7)
                ));
            }
        } catch (Exception e) {
            throw new LightRuntimeException(e);
        }
        connection.close();
        return storageMetadata;
    }

    @Override
    @Query("SELECT * FROM storage_metadata WHERE deleted = 0")
    List<StorageMetadata> getActives();

    @Override
    @Query("SELECT * FROM storage_metadata WHERE deleted = 1")
    List<StorageMetadata> getInactives();

    @Override
    @Query("SELECT * FROM storage_metadata WHERE id = {id}")
    StorageMetadata getById(long id);

    @Override
    @Query("SELECT * FROM storage_metadata WHERE id IN ({ids})")
    List<StorageMetadata> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM storage_metadata WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM storage_metadata WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM storage_metadata")
    List<StorageMetadata> get();

    @Override
    @Query("SELECT COUNT(*) FROM storage_metadata")
    int count();

    @Override
    @Query("SELECT * FROM storage_metadata LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<StorageMetadata> get(Offset offset);

    @Override
    default String getTableName() {
        return "storage_metadata";
    }
}

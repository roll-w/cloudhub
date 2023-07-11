package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.tag.NameValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.DaoConnectionGetter;
import space.lingu.light.LightRuntimeException;
import space.lingu.light.ManagedConnection;
import space.lingu.light.Query;

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

    default List<StorageMetadata> getByTagValues(List<NameValue> nameValues) {
        if (nameValues.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("SELECT " +
                "id, storage_id, tag_group_id, tag_id, deleted, create_time, update_time " +
                "FROM storage_metadata WHERE ");
        for (int i = 0; i < nameValues.size(); i++) {
            sql.append("name = ")
                    .append("?")
                    .append(" AND value = ")
                    .append("?");
            if (i != nameValues.size() - 1) {
                sql.append(" OR ");
            }
        }
        ManagedConnection connection = getConnection();
        PreparedStatement statement = connection.acquire(sql.toString());
        int index = 1;
        for (NameValue nameValue : nameValues) {
            try {
                statement.setString(index++, nameValue.name());
                statement.setString(index++, nameValue.value());
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
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        resultSet.getBoolean(7),
                        0, 0
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

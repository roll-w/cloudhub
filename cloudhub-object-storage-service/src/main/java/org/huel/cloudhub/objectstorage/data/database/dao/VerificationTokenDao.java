package org.huel.cloudhub.objectstorage.data.database.dao;

import org.huel.cloudhub.objectstorage.data.entity.token.RegisterVerificationToken;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class VerificationTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RegisterVerificationToken... tokens);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RegisterVerificationToken token);// with user id

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<RegisterVerificationToken> tokens);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(RegisterVerificationToken... tokens);

    @Transaction
    @Delete("UPDATE verification_token_table SET verification_used = {used} WHERE verification_token = {token}")
    public abstract void updateUsedByToken(String token, boolean used);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<RegisterVerificationToken> tokens);

    @Delete
    public abstract void delete(RegisterVerificationToken... tokens);

    @Transaction
    @Delete("DELETE FROM verification_token_table WHERE verification_token = {token.token()}")
    public abstract void delete(RegisterVerificationToken token);

    @Delete
    public abstract void delete(List<RegisterVerificationToken> users);

    @Query("SELECT * FROM verification_token_table WHERE verification_token = {token}")
    public abstract RegisterVerificationToken findByToken(String token);

    @Query("SELECT * FROM verification_token_table WHERE verification_user_id = {userId} LIMIT 1")
    public abstract RegisterVerificationToken findByUserId(long userId);
}

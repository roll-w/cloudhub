package org.huel.cloudhub.web.data.entity;

import org.huel.cloudhub.web.data.dto.VerifiableToken;
import space.lingu.light.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Verification Token
 *
 * @author RollW
 */
@DataTable(tableName = "verification_token_table", indices =
@Index(value = "verification_user_id"), configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public record RegisterVerificationToken(
        @DataColumn(name = "verification_token")
        @PrimaryKey
        String token,

        @DataColumn(name = "verification_user_id")
        Long userId,

        @DataColumn(name = "verification_expiry_time")
        Long expiryDate,

        @DataColumn(name = "verification_used")
        boolean used) implements VerifiableToken {

    private static final int EXPIRATION = 60 * 24;// min

    public static long calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime().getTime();
    }

    public static long calculateExpiryDate() {
        return calculateExpiryDate(EXPIRATION);
    }

    public Date toDate() {
        return new Date(expiryDate);
    }

}

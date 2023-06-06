package org.huel.cloudhub.client.disk.domain.userstats;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.light.*;

import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "user_statistics", indices = {
        @Index(value = {"user_id", "user_type"}, unique = true)
})
public class UserStatistics implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "user_type")
    private final LegalUserType userType;

    @DataColumn(name = "statistics", dataType = SQLDataType.LONGTEXT)
    private final Map<String, String> statistics;

    public UserStatistics(Long id, long userId,
                          LegalUserType userType,
                          Map<String, String> statistics) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.statistics = statistics;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public LegalUserType getUserType() {
        return userType;
    }

    public Map<String, String> getStatistics() {
        return statistics;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Long id;
        private long userId;
        private LegalUserType userType;
        private Map<String, String> statistics;

        private Builder() {
        }

        private Builder(UserStatistics userstatistics) {
            this.id = userstatistics.id;
            this.userId = userstatistics.userId;
            this.userType = userstatistics.userType;
            this.statistics = userstatistics.statistics;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUserType(LegalUserType userType) {
            this.userType = userType;
            return this;
        }

        public Builder setStatistics(Map<String, String> statistics) {
            this.statistics = statistics;
            return this;
        }

        public UserStatistics build() {
            return new UserStatistics(id, userId, userType, statistics);
        }
    }
}

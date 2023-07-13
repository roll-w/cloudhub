package org.huel.cloudhub.client.disk.domain.statistics;

import org.huel.cloudhub.client.disk.database.DataItem;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "dated_statistics", indices = {
     @Index(value = {"key", "date"}, unique = true)
})
public class DatedStatistics implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "key")
    private final String key;

    @DataColumn(name = "value")
    private final Map<String, String> value;

    @DataColumn(name = "date")
    private final LocalDate date;

    public DatedStatistics(Long id, String key,
                           Map<String, String> value, LocalDate date) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.date = date;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Long id;
        private String key;
        private Map<String, String> value;
        private LocalDate date;

        private Builder() {
        }

        private Builder(DatedStatistics datedstatistics) {
            this.id = datedstatistics.id;
            this.key = datedstatistics.key;
            this.value = datedstatistics.value;
            this.date = datedstatistics.date;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setValue(Map<String, String> value) {
            this.value = value;
            return this;
        }

        public Builder setDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public DatedStatistics build() {
            return new DatedStatistics(id, key, value, date);
        }
    }

}

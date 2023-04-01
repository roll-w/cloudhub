package org.huel.cloudhub.client.data.entity;

import space.lingu.NonNull;
import space.lingu.Nullable;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(name = "setting_table")
public record SettingItem(
        @NonNull
        @PrimaryKey
        @DataColumn(name = "key", nullable = false)
        String key,

        @Nullable
        @DataColumn(name = "value")
        String value) {
}

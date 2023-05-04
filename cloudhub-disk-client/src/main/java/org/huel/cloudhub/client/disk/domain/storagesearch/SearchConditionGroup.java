package org.huel.cloudhub.client.disk.domain.storagesearch;

import java.util.List;

/**
 * @author RollW
 */
public record SearchConditionGroup(
        List<SearchCondition> conditions
) {

    public SearchConditionGroup {
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("conditions must not be null or empty");
        }
    }

    public SearchCondition getCondition(String name) {
        return conditions.stream()
                .filter(condition -> condition.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasDuplicateConditionName() {
        return conditions.stream()
                .map(SearchCondition::name)
                .distinct()
                .count() != conditions.size();
    }
}

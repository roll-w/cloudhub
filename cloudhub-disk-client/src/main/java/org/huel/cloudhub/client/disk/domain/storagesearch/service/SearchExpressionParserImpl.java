package org.huel.cloudhub.client.disk.domain.storagesearch.service;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchExpressionParser;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchExpressionException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class SearchExpressionParserImpl implements SearchExpressionParser {
    @Override
    public List<SearchCondition> parse(String expression) throws SearchConditionException {
        if (Strings.isNullOrEmpty(expression)) {
            return List.of();
        }
        final String expr = expression.trim();
        if (expr.isEmpty()) {
            return List.of();
        }
        String[] conditions = expr.split(" ");
        if (conditions.length == 0) {
            return List.of();
        }
        List<SearchCondition> searchConditions = new ArrayList<>();

        for (String condition : conditions) {
            String trimmed = condition.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            SearchCondition searchCondition = parseCondition(trimmed);
            searchConditions.add(searchCondition);
        }

        return searchConditions;
    }

    private SearchCondition parseCondition(String condition) {
        int index = condition.indexOf(":");
        if (index == -1) {
            throw new SearchExpressionException("Invalid condition: " + condition);
        }
        String name = condition.substring(0, index);
        String keyword = condition.substring(index + 1);
        if (name.isEmpty()) {
            throw new SearchExpressionException("Empty name: " + condition);
        }
        if (keyword.isEmpty()) {
            throw new SearchExpressionException("Empty keyword: " + condition);
        }
        return new SearchCondition(name, keyword);
    }

}

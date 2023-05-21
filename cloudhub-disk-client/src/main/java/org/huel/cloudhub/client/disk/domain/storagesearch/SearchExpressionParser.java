package org.huel.cloudhub.client.disk.domain.storagesearch;

import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;

import java.util.List;

/**
 * @author RollW
 */
public interface SearchExpressionParser {
    List<SearchCondition> parse(String expression)
            throws SearchConditionException;
}

package org.huel.cloudhub.client.disk.domain.storagesearch.common;

/**
 * @author RollW
 */
public class SearchConditionException extends SearchExpressionException {
    private final String name;
    private final String keyword;

    public SearchConditionException(String name,
                                    String keyword) {
        this.name = name;
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }
}

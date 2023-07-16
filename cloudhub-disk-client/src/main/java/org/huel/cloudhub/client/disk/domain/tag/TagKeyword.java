package org.huel.cloudhub.client.disk.domain.tag;

/**
 * @author RollW
 */
public record TagKeyword(
        String name,
        int weight
) {
    public TagKeyword {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
    }

    public TagKeyword(String name) {
        this(name, 0);
    }

    public static TagKeyword of(String name) {
        return new TagKeyword(name);
    }

    public static TagKeyword of(String name, int weight) {
        return new TagKeyword(name, weight);
    }
}

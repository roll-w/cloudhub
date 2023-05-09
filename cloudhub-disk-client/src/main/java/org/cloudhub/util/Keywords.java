package org.cloudhub.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A readonly Keywords collection.
 * <p>
 * <h2>Keywords File Format</h2>
 * <p>
 * <pre>
 * # This is a comment
 * [Group] # line comment: Group name
 * word1=1 # With weight provided 1
 * word3= # With no weight provided
 * word2 # With no weight provided
 * </pre>
 * </p>
 * <p>
 * Allow set weight for each word. Defaults to 1.
 * If set to 0 or negative, the word will be ignored.
 * <p>
 * Invalid format:
 * <pre>
 * word1=1=2 # Invalid format
 * word2==
 * word3=12a
 * word4=12.3 # Only supports integer value
 * </pre>
 * Invalid format will throw a {@link KeywordsTokenizeException}.
 *
 * @author RollW
 */
public class Keywords implements Map<String, List<Keywords.Keyword>> {
    private final Map<String, List<Keyword>> keywordMap;

    private final List<KeywordsGroup> groups;

    /**
     * Creates an empty {@code Keywords}.
     */
    public Keywords() {
        this.keywordMap = new HashMap<>();
        this.groups = Collections.emptyList();
    }

    public Keywords(Map<String, List<Keyword>> keywordMap) {
        this.keywordMap = new HashMap<>();
        this.keywordMap.putAll(keywordMap);
        this.groups = this.keywordMap.entrySet().stream()
                .map(entry -> new KeywordsGroup(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Keywords(InputStream inputStream) {
        this.keywordMap = loadKeywords(inputStream);
        this.groups = this.keywordMap.entrySet().stream()
                .map(entry -> new KeywordsGroup(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<Keyword> get(String key) {
        return keywordMap.get(key);
    }

    @Override
    public int size() {
        return keywordMap.size();
    }

    @Override
    public boolean isEmpty() {
        return keywordMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keywordMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return keywordMap.containsValue(value);
    }

    @Override
    public List<Keyword> get(Object key) {
        return keywordMap.get(key);
    }

    @Override
    public List<Keyword> put(String key, List<Keyword> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super List<Keyword>, ? extends List<Keyword>> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> putIfAbsent(String key, List<Keyword> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(String key, List<Keyword> oldValue, List<Keyword> newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> replace(String key, List<Keyword> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> computeIfAbsent(String key,
                                         Function<? super String, ? extends List<Keyword>> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> computeIfPresent(String key,
                                          BiFunction<? super String, ? super List<Keyword>, ? extends List<Keyword>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> compute(String key,
                                 BiFunction<? super String, ? super List<Keyword>, ? extends List<Keyword>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Keyword> merge(String key, List<Keyword> value,
                               BiFunction<? super List<Keyword>, ? super List<Keyword>, ? extends List<Keyword>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<Keyword>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return keywordMap.keySet();
    }

    @Override
    public Collection<List<Keyword>> values() {
        return Collections.unmodifiableCollection(keywordMap.values());
    }

    @Override
    public Set<Entry<String, List<Keyword>>> entrySet() {
        return keywordMap.entrySet()
                .stream()
                .map(KeywordsGroup::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<Keyword> getOrDefault(Object key, List<Keyword> defaultValue) {
        return keywordMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super List<Keyword>> action) {
        keywordMap.forEach(action);
    }

    public List<KeywordsGroup> listGroups() {
        return groups;
    }

    public record Keyword(
            String word,
            int weight
    ) implements Comparable<Keyword> {
        public Keyword(String word) {
            this(word, 1);
        }

        @Override
        public int compareTo(Keyword o) {
            return Integer.compare(weight, o.weight);
        }
    }

    public record KeywordsGroup(
            String name,
            List<Keyword> keywords
    ) implements Entry<String, List<Keyword>> {
        public KeywordsGroup(String name) {
            this(name, new ArrayList<>());
        }

        private KeywordsGroup(Entry<String, List<Keyword>> entry) {
            this(entry.getKey(), entry.getValue());
        }

        @Override
        public String getKey() {
            return name;
        }

        @Override
        public List<Keyword> getValue() {
            return keywords;
        }

        @Override
        public List<Keyword> setValue(List<Keyword> value) {
            throw new UnsupportedOperationException();
        }
    }

    public void tryParseInput(BufferedReader reader) {
        AtomicInteger lineNum = new AtomicInteger(0);
        reader.lines().forEachOrdered(line -> {
            int cur = lineNum.incrementAndGet();
            List<Tokenize> tokenizes = tokenizeLine(line, cur);
            tokenizes.forEach(tokenize -> {
                Parser<?> parser = getParser(tokenize.token);
                System.out.println("parse line: " + cur + " " +
                        tokenize.token + " " + tokenize.value +
                        " res: " + tokenize.parse(parser));

            });
        });
    }

    private enum Token {
        GROUP_NAME,
        WORD,
        COMMENT,
        END,
        INVALID;
    }

    private record Tokenize(
            Token token,
            String value,
            int lineNum,
            int order
    ) {

        static Tokenize end(int lineNum) {
            return new Tokenize(Token.END, null, lineNum, 0);
        }

        <T> T parse(Parser<T> parser) {
            return parser.parse(value, lineNum);
        }
    }

    public interface Parser<T> {
        T parse(String value, int lineNum);

        Parser<String> STRING = (value, lineNum) -> value;
    }

    private List<Tokenize> tokenizeLine(String line, int lineNum) {
        if (line == null || line.isEmpty()) {
            return List.of(Tokenize.end(lineNum));
        }
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return List.of(Tokenize.end(lineNum));
        }

        final int length = trimmedLine.length();
        if (length == 1 && trimmedLine.equals("#")) {
            return List.of(new Tokenize(Token.COMMENT, trimmedLine, lineNum, 0));
        }

        final char firstChar = trimmedLine.charAt(0);

        return switch (firstChar) {
            case '#' -> List.of(new Tokenize(Token.COMMENT, trimmedLine, lineNum, 0));
            case '[' -> {
                if (length == 2) {
                    throw new KeywordsTokenizeException(
                            "Invalid group name on '" + line + "' of line [" + lineNum + "]."
                    );
                }
                yield tryTokenizeForComment(trimmedLine, lineNum, Token.GROUP_NAME);
            }
            default -> tryTokenizeForComment(trimmedLine, lineNum, Token.WORD);
        };
    }

    private List<Tokenize> tryTokenizeForComment(String line, int lineNum,
                                                 Token originalToken) {
        CharacterIterator it = new StringCharacterIterator(line);
        int index = 0;
        int commentIndex = -1;


        while (it.current() != CharacterIterator.DONE) {
            char c = it.current();
            if (c == '#') {
                commentIndex = index;
                break;
            }
            it.next();
            index++;

        }
        if (commentIndex == -1) {
            return List.of(new Tokenize(originalToken, line, lineNum, 0));
        }
        if (commentIndex == 0) {
            return List.of(new Tokenize(Token.COMMENT, line, lineNum, 0));
        }
        return List.of(
                new Tokenize(originalToken, line.substring(0, commentIndex), lineNum, 0),
                new Tokenize(Token.COMMENT, line.substring(commentIndex), lineNum, 1)
        );
    }

    private static Parser<?> getParser(Token token) {
        return switch (token) {
            case GROUP_NAME -> GroupNameParser.INSTANCE;
            case WORD -> WordParser.INSTANCE;
            default -> Parser.STRING;
        };
    }

    static final class GroupNameParser implements Parser<String> {
        @Override
        public String parse(String value, int lineNum) {
            return value.substring(1, value.length() - 1);
        }

        static final GroupNameParser INSTANCE = new GroupNameParser();
    }

    static final class WordParser implements Parser<Keyword> {
        @Override
        public Keyword parse(String value, int lineNum) {
            return iteratorLineIfWord(value, lineNum);
        }

        static final WordParser INSTANCE = new WordParser();
    }

    private static Keyword iteratorLineIfWord(String line, int lineNum) {
        CharacterIterator it = new StringCharacterIterator(line);
        int index = 0;
        boolean hasEquals = false;
        int equalsIndex = -1;

        while (it.current() != CharacterIterator.DONE) {
            char c = it.current();
            if (c == '#') {
                break;
            }
            if (c == '=') {
                if (hasEquals) {
                    throw new KeywordsTokenizeException(
                            "Invalid word on '" + line + "' of line [" + lineNum + "]."
                    );
                }
                hasEquals = true;
                equalsIndex = index;
            }

            if (!Character.isSpaceChar(c) &&
                    hasEquals && c != '=' && !Character.isDigit(c)) {
                throw new KeywordsTokenizeException(
                        "Only integer value can be set as weight in word on '" + line + "' of line [" + lineNum + "]."
                );
            }

            it.next();
            index++;
        }

        if (hasEquals) {
            String word = line.substring(0, equalsIndex);
            String weight = line.substring(equalsIndex + 1).trim();
            if (weight.isEmpty()) {
                return new Keyword(word);
            }
            return new Keyword(word, Integer.parseInt(weight));
        }
        return new Keyword(line);
    }

    /**
     * Keywords tokenize exception, throw when tokenize keywords file failed.
     */
    public static class KeywordsTokenizeException extends RuntimeException {
        public KeywordsTokenizeException(String message) {
            super(message);
        }

        public KeywordsTokenizeException() {
            super();
        }

        public KeywordsTokenizeException(String message, Throwable cause) {
            super(message, cause);
        }

        public KeywordsTokenizeException(Throwable cause) {
            super(cause);
        }
    }

    private Map<String, List<Keyword>> loadKeywords(InputStream inputStream) {
        Map<String, List<Keyword>> keywords = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String currentGroupName = null;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                List<Tokenize> tokenizes = tokenizeLine(line, 0);
                for (Tokenize tokenize : tokenizes) {
                    Token token = tokenize.token;
                    if (token == Token.END) {
                        continue;
                    }
                    if (token == Token.COMMENT) {
                        continue;
                    }
                    if (token == Token.GROUP_NAME) {
                        currentGroupName = tokenize.parse(GroupNameParser.INSTANCE);
                        keywords.put(currentGroupName, new ArrayList<>());
                        continue;
                    }
                    if (token == Token.WORD) {
                        if (currentGroupName == null) {
                            throw new KeywordsTokenizeException(
                                    "Word '" + tokenize.value + "' must be in a group."
                            );
                        }
                        keywords.get(currentGroupName).add(tokenize.parse(WordParser.INSTANCE));
                    }
                }
            }
            return keywords;
        } catch (IOException e) {
            throw new KeywordsTokenizeException(e);
        }
    }

    public void writeTo(OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(outputStream)
        ));
        List<KeywordsGroup> keywordsGroups = listGroups();
        for (KeywordsGroup keywordsGroup : keywordsGroups) {
            writer.println("[" + keywordsGroup.name() + "]");
            for (Keyword keyword : keywordsGroup.keywords()) {
                writer.print(keyword.word());
                if (keyword.weight() != 1) {
                    writer.print("=" + keyword.weight());
                }
                writer.println();
            }
            writer.flush();
        }
        writer.flush();
    }

    public static Keywords loadFile(InputStream inputStream) {
        return new Keywords(inputStream);
    }
}

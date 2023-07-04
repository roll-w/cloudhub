package org.cloudhub.util;

import space.lingu.NonNull;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;

/**
 * A simple time range parser.
 * <p>
 * The time range format is like:
 * <ul>
 *     <li>2023/1/1</li>
 *     <li>1/1</li>
 *     <li>2023/1/1-2023/1/2</li>
 *     <li>1/1-1/2</li>
 * </ul>
 *
 * @author RollW
 */
public class TimeParser {
    private final String expression;

    public TimeParser(String expression) {
        this.expression = expression;
    }


    @NonNull
    public TimeRange parse() throws TimeExpressionException {
        return parseDuration(expression);
    }

    @NonNull
    private TimeRange parseDuration(String time) {
        String[] times = time.split("-");
        if (times.length == 1) {
            return parseSingleDuration(times[0]);
        }
        if (times.length == 2) {
            return parseDoubleDuration(times[0], times[1]);
        }
        throw new TimeExpressionException("Invalid time format: " + time);
    }

    @NonNull
    private TimeRange parseSingleDuration(String time) {
        String timeWithoutSlash = removeEndSlash(time);
        Nodes timeNode = tryParseTime(timeWithoutSlash);

        return new TimeRange(timeNode.begin(), timeNode.end());
    }

    @NonNull
    private TimeRange parseDoubleDuration(String startTime, String endTime) {
        String startTimeWithoutSlash = removeEndSlash(startTime);
        String endTimeWithoutSlash = removeEndSlash(endTime);

        Nodes startTimeNode = tryParseTime(startTimeWithoutSlash);
        Nodes endTimeNode = tryParseTime(endTimeWithoutSlash);

        long startTimestamp = startTimeNode.begin();
        long endTimestamp = endTimeNode.end();

        if (startTimestamp <= 0 || endTimestamp <= 0) {
            throw new TimeExpressionException("Invalid time format: " + startTime + "-" + endTime);
        }
        if (startTimestamp > endTimestamp) {
            throw new TimeExpressionException("Invalid time format: " + startTime + "-" + endTime);
        }

        return new TimeRange(startTimestamp, endTimestamp);
    }


    private static String removeEndSlash(String time) {
        if (time.endsWith("/")) {
            return time.substring(0, time.length() - 1);
        }
        return time.trim();
    }

    private Nodes tryParseTime(String time) {
        String timeWithoutSlash = removeEndSlash(time);
        String[] timeNodes = timeWithoutSlash.split("/");

        if (timeNodes.length == 1) {
            TimeNode node = parseNode(timeNodes[0], NodeTimeUnit.MONTH);
            return Nodes.of(node);
        }
        if (timeNodes.length == 2) {
            TimeNode node1 = parseNode(timeNodes[0], NodeTimeUnit.YEAR);
            NodeTimeUnit inferredUnit = node1.value() <= 12
                    ? NodeTimeUnit.DAY : NodeTimeUnit.MONTH;

            TimeNode node2 = parseNode(
                    timeNodes[1],
                    inferredUnit
            );
            return Nodes.of(node1, node2);
        }
        if (timeNodes.length == 3) {
            TimeNode node1 = parseNode(timeNodes[0], NodeTimeUnit.YEAR);
            TimeNode node2 = parseNode(timeNodes[1], NodeTimeUnit.MONTH);
            TimeNode node3 = parseNode(timeNodes[2], NodeTimeUnit.DAY);
            return Nodes.of(node1, node2, node3);
        }

        throw new TimeExpressionException("Invalid time format: " + time);
    }

    private TimeNode parseNode(String node,
                               NodeTimeUnit inferredUnit) {
        Integer value = tryParseInt(node);
        if (value == null) {
            throw new TimeExpressionException("Invalid time format: " + node);
        }
        if (value <= 0) {
            throw new TimeExpressionException("Invalid time format: " + node);
        }
        if (value <= 12 && isMonthOrYear(inferredUnit)) {
            return new TimeNode(NodeTimeUnit.MONTH, value);
        }
        if (value <= 31) {
            return new TimeNode(NodeTimeUnit.DAY, value);
        }

        if (value > 1000) {
            return new TimeNode(NodeTimeUnit.YEAR, value);
        }

        throw new TimeExpressionException("Invalid time format: " + node);
    }

    private boolean isMonthOrYear(NodeTimeUnit unit) {
        return unit == NodeTimeUnit.MONTH || unit == NodeTimeUnit.YEAR;
    }

    private static Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private enum NodeTimeUnit {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND
    }

    private record TimeNode(
            NodeTimeUnit unit,
            int value
    ) {
    }

    private record Nodes(
            List<TimeNode> nodes
    ) {

        private int getYear() {
            TimeNode yearNode = nodes.stream()
                    .filter(node -> node.unit() == NodeTimeUnit.YEAR)
                    .findFirst()
                    .orElse(null);
            if (yearNode == null) {
                return Calendar.getInstance().get(Calendar.YEAR);
            }
            return yearNode.value();
        }

        private int getMonth(int defaultMonth) {
            TimeNode monthNode = nodes.stream()
                    .filter(node -> node.unit() == NodeTimeUnit.MONTH)
                    .findFirst()
                    .orElse(null);
            if (monthNode == null) {
                return defaultMonth;
            }
            return monthNode.value();
        }

        private int getDay(int defaultDay) {
            TimeNode dayNode = nodes.stream()
                    .filter(node -> node.unit() == NodeTimeUnit.DAY)
                    .findFirst()
                    .orElse(null);
            if (dayNode == null) {
                return defaultDay;
            }
            return dayNode.value();
        }

        public long begin() {
            int year = getYear();
            int month = getMonth(1);
            int day = getDay(1);

            LocalDateTime localDateTime = LocalDateTime.of(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0,
                    0
            );
            return localDateTime
                    .atZone(ZoneOffset.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        public long end() {
            int year = getYear();
            int month = getMonth(12);
            int day = getDay(lastDayOf(year, month));

            LocalDateTime localDateTime = LocalDateTime.of(
                    year,
                    month,
                    day,
                    23,
                    59,
                    59,
                    999
            );
            return localDateTime
                    .atZone(ZoneOffset.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        static Nodes of(TimeNode... nodes) {
            return new Nodes(List.of(nodes));
        }
    }

    private static int lastDayOf(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    @NonNull
    public static TimeRange parseTimeRange(String expression) {
        return new TimeParser(expression).parse();
    }
}

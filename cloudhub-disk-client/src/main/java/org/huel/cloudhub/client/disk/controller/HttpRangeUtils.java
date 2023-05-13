package org.huel.cloudhub.client.disk.controller;

import org.springframework.http.HttpRange;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author RollW
 */
public final class HttpRangeUtils {

    public static List<HttpRange> tryGetsRange(HttpServletRequest request) {
        String range = request.getHeader("Range");
        if (range == null) {
            return List.of();
        }
        return HttpRange.parseRanges(range);
    }

    private HttpRangeUtils() {
    }
}

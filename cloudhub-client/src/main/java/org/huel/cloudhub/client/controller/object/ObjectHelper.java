package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.service.object.ObjectMetadataHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public final class ObjectHelper {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static String readPath(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = antPathMatcher.extractPathWithinPattern(bestMatchingPattern, path);

        if (!arguments.isEmpty()) {
            return "/" + arguments;
        }
        return "";
    }

    public static Map<String, String> buildInitialMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ObjectMetadataHeaders.CONTENT_TYPE, file.getContentType());
        metadata.put(ObjectMetadataHeaders.OBJECT_LENGTH, "" + file.getSize());
        return metadata;
    }


    private ObjectHelper() {
    }
}

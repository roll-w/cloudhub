package org.huel.cloudhub.client.data.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.lingu.light.DataConverter;

import java.util.Collections;
import java.util.Map;

/**
 * @author RollW
 */
public class CloudhubConverter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @DataConverter
    public static String convertsStringMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    @DataConverter
    public static Map<String, String> convertsStringMapFrom(String mapString) {
        if (mapString == null || mapString.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return (Map<String, String>) MAPPER.readValue(mapString, Map.class);
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

}

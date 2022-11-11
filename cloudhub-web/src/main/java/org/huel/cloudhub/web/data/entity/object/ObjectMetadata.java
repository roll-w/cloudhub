package org.huel.cloudhub.web.data.entity.object;

import java.util.Map;

/**
 * @author RollW
 */
public class ObjectMetadata {
    // TODO:
    private Long userId;
    private String objectName;
    /**
     * 对象的元数据，写入到响应的headers里。
     */
    private Map<String, String> metadata;
}

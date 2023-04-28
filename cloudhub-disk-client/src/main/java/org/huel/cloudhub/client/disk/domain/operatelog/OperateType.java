package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public interface OperateType extends SystemResourceKind.Kind {
    long getTypeId();

    Action getAction();

    @Override
    SystemResourceKind getSystemResourceKind();

    String getName();

    /**
     * 操作描述模板。
     * <p>
     * 可用参数：
     * <ul>
     *     <li>{0}：操作前的内容（可能为空）</li>
     *     <li>{1}：操作后的内容</li>
     * </ul>
     */
    String getDescriptionTemplate();

    String getDescription(Object... args);
}

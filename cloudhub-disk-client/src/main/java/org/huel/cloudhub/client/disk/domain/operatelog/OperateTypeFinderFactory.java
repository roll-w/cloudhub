package org.huel.cloudhub.client.disk.domain.operatelog;

import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class OperateTypeFinderFactory {

    public OperateTypeFinder getOperateTypeFinder() {
        return BuiltinOperationType.getFinderInstance();
    }
}

package org.huel.cloudhub.client.disk.system.pages;

import org.huel.cloudhub.client.disk.database.DataItem;

/**
 * @author RollW
 */
public interface PageableDataTransferObject<T extends DataItem> {
    Class<T> convertFrom();
}

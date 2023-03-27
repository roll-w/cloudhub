package org.huel.cloudhub.web.data.page;

/**
 * @author RollW
 */
public interface Pageable {
    int getPage();

    int getSize();

    default Offset toOffset() {
        return PageHelper.offset(this);
    }
}

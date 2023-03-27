package org.huel.cloudhub.web.data.page;

/**
 * @author RollW
 */
public class PageHelper {

    public static Offset offset(int page, int size) {
        return new Offset(size, (page - 1) * size);
    }

    public static Offset offset(Pageable pageRequest) {
        return offset(pageRequest.getPage(), pageRequest.getSize());
    }


    private PageHelper() {
    }
}

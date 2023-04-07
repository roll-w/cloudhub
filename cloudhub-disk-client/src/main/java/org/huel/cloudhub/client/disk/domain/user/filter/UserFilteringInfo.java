package org.huel.cloudhub.client.disk.domain.user.filter;

import space.lingu.NonNull;

/**
 * @author RollW
 */
public record UserFilteringInfo(
        @NonNull
        String value,

        @NonNull
        UserFilteringInfoType filterType
) {

}

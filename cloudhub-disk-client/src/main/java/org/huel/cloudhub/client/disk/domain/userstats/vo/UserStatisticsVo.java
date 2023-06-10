package org.huel.cloudhub.client.disk.domain.userstats.vo;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.dto.UserStatisticsDetail;
import org.huel.cloudhub.web.LongKeyValue;

import java.util.List;

/**
 * @author RollW
 */
public record UserStatisticsVo(
        long id,
        long userId,
        LegalUserType userType,
        List<LongKeyValue> statistics
) {

    public static UserStatisticsVo from(
            UserStatisticsDetail userStatisticsDetail) {
        return new UserStatisticsVo(
                userStatisticsDetail.id(),
                userStatisticsDetail.userId(),
                userStatisticsDetail.userType(),
                LongKeyValue.from(userStatisticsDetail.statistics())
        );
    }
}

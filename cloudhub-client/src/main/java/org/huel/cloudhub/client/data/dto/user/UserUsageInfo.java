package org.huel.cloudhub.client.data.dto.user;

/**
 * 用户用量信息
 *
 * @author RollW
 */
public record UserUsageInfo(
        long userId,
        String groupName,
        int nowUsedSize,
        int nowUsedNum,
        int maxSize,
        int maxNum) {
}

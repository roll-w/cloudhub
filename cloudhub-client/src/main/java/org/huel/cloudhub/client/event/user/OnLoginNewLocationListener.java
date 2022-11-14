package org.huel.cloudhub.client.event.user;

import org.huel.cloudhub.common.MailConstant;
import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.service.user.UserService;
import org.huel.cloudhub.client.util.SimpleMailMessageBuilder;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class OnLoginNewLocationListener implements ApplicationListener<OnLoginNewLocationEvent> {

    public OnLoginNewLocationListener(MessageSource messageSource,
                                      UserService userService,
                                      JavaMailSender mailSender,
                                      MailProperties mailProperties) {
        this.messageSource = messageSource;
        this.userService = userService;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public void onApplicationEvent(@NonNull OnLoginNewLocationEvent event) {
        handleLoginEvent(event);
    }

    @Async
    void handleLoginEvent(OnLoginNewLocationEvent event) {
        if (MailConstant.EMAIL_DISABLED.equals(mailProperties.getUsername())) {
            // if disables mail, directly activate user.
            return;
        }
        UserInfo userInfo = event.getUserInfo();
        if (userInfo.id() > 0) {
            return;
        }
        // TODO: temp disable

        final String subject = "[Cloudhub] Login at a new position";
        String greeting = "Dear " + userInfo.username();
        final String message = greeting +
                ", \nYou are logging in through a new address, " +
                "if this is not you, please change your password promptly. \n\nLocation: 127.0.0.1.";

        SimpleMailMessage mailMessage = new SimpleMailMessageBuilder()
                .setTo(userInfo.email())
                .setSubject(subject)
                .setText(message)
                .setFrom(mailProperties.getUsername())
                .build();
        mailSender.send(mailMessage);
    }

    private final MessageSource messageSource;

    private final UserService userService;

    private final JavaMailSender mailSender;

    private final MailProperties mailProperties;
}

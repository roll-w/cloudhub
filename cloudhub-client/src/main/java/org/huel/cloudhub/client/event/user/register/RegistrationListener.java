package org.huel.cloudhub.client.event.user.register;

import org.huel.cloudhub.common.MailConstant;
import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.service.user.UserService;
import org.huel.cloudhub.client.util.SimpleMailMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

import java.util.UUID;

/**
 * @author RollW
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
        handleRegistration(event);
    }

    @Async
    void handleRegistration(OnRegistrationCompleteEvent event) {
        UserInfo user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        if (MailConstant.EMAIL_DISABLED.equals(mailProperties.getUsername())) {
            // if disables mail, directly activate user.
            userService.verifyToken(token);
            return;
        }

        String emailAddress = user.email();
        String subject = "[Cloudhub] Registration Confirmation";
        String confirmUrl = event.getUrl() + "/api/user/register/confirm/" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessageBuilder()
                .setTo(emailAddress)
                .setSubject(subject)
                .setText(("You are now registering a new account, " +
                        "click %s to confirm activate.").formatted(confirmUrl))
                .setFrom(mailProperties.getUsername())
                .build();
        mailSender.send(mailMessage);
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private JavaMailSender mailSender;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private MailProperties mailProperties;

    @Autowired
    public void setMailProperties(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }
}

package com.example.talentrecruitment.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(
            String recipientEmail,
            String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipientEmail);

        message.setSubject(
                "Password Reset - Talent Recruitment"
        );

        message.setText(
                "Hello,\n\n"
                        + "You requested to reset your password.\n\n"
                        + "Click the link below to reset your password:\n\n"
                        + resetLink
                        + "\n\n"
                        + "This link will expire in 30 minutes.\n\n"
                        + "If you did not request this password reset, "
                        + "please ignore this email.\n\n"
                        + "Regards,\n"
                        + "Talent Recruitment Team"
        );

        mailSender.send(message);
    }
}
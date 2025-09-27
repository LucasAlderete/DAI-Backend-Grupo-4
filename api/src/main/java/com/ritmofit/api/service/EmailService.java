package com.ritmofit.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void enviarCodigoOtp(String email, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Código de verificación - RitmoFit");
        message.setText(String.format(
                "Tu código de verificación es: %s\n\n" +
                "Este código expira en 10 minutos.\n\n" +
                "Si no solicitaste este código, ignora este mensaje.\n\n" +
                "¡Gracias por usar RitmoFit!",
                codigo
        ));

        mailSender.send(message);
        System.out.println("✅ Email enviado exitosamente a: " + email);
    }
}

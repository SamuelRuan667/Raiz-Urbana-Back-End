package br.com.raizurbana.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacao(
            String destinatario,
            String token
    ) {
        String url = frontendUrl.replaceAll("/+$", "")
                + "/redefinir-senha?token=" + token;

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de senha - Raiz Urbana");

        mensagem.setText(
                "Olá!\n\n"
                        + "Recebemos uma solicitação para redefinir sua senha "
                        + "no Raiz Urbana.\n\n"
                        + "Para criar uma nova senha, acesse o link abaixo:\n"
                        + url + "\n\n"
                        + "Este link é válido por 30 minutos e pode ser utilizado "
                        + "apenas uma vez.\n\n"
                        + "Se você não solicitou essa alteração, ignore este e-mail.\n\n"
                        + "Equipe Raiz Urbana."
        );

        mailSender.send(mensagem);
    }
}
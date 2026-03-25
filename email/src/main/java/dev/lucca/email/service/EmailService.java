package dev.lucca.email.service;

import dev.lucca.email.domain.EmailModel;
import dev.lucca.email.dto.EmailDto;
import dev.lucca.email.enums.EmailStatus;
import dev.lucca.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;

    @Value("${app.mail.sender:onboarding@resend.dev}")
    private String senderEmail;

    @Transactional
    public EmailModel sendEmailTransactional(EmailDto emailDto) {
        EmailModel emailModel = new EmailModel();
        BeanUtils.copyProperties(emailDto, emailModel);
        emailModel.setEmailFrom(senderEmail);
        emailModel.setEmailSubject(emailDto.emailSubject());
        emailModel.setSendDateEmail(LocalDateTime.now());
        emailModel.setStatusEmail(EmailStatus.PENDING);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getEmailSubject());
            message.setText(emailModel.getBody());

            javaMailSender.send(message);
            emailModel.setStatusEmail(EmailStatus.SENT);
        } catch (Exception ex) {
            emailModel.setStatusEmail(EmailStatus.FAILED);
            log.error("Erro ao enviar email para {}: {}", emailDto.emailTo(), ex.getMessage(), ex);
        }

        return emailRepository.save(emailModel);
    }

    public List<EmailModel> findAll() {
        return emailRepository.findAll();
    }

    public EmailModel findById(UUID id) {
        return emailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email nao encontrado"));
    }
}


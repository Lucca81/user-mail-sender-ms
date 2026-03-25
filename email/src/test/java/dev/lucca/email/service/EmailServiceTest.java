package dev.lucca.email.service;

import dev.lucca.email.dto.EmailDto;
import dev.lucca.email.enums.EmailStatus;
import dev.lucca.email.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private EmailRepository emailRepository;
    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailRepository = mock(EmailRepository.class);
        javaMailSender = mock(JavaMailSender.class);
        emailService = new EmailService(emailRepository, javaMailSender);
        ReflectionTestUtils.setField(emailService, "senderEmail", "sender@gmail.com");
    }

    @Test
    void shouldSendAndSaveAsSent() {
        EmailDto emailDto = new EmailDto(
                UUID.randomUUID(),
                "destino@gmail.com",
                "Assunto teste",
                "Corpo teste"
        );

        when(emailRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = emailService.sendEmailTransactional(emailDto);

        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(any());
        assertEquals(EmailStatus.SENT, result.getStatusEmail());
        assertEquals("sender@gmail.com", result.getEmailFrom());
        assertEquals("Assunto teste", result.getEmailSubject());
    }

    @Test
    void shouldSaveAsFailedWhenSendThrowsException() {
        EmailDto emailDto = new EmailDto(
                UUID.randomUUID(),
                "destino@gmail.com",
                "Assunto teste",
                "Corpo teste"
        );

        doThrow(new RuntimeException("smtp error")).when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        when(emailRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = emailService.sendEmailTransactional(emailDto);

        verify(emailRepository, times(1)).save(any());
        assertEquals(EmailStatus.FAILED, result.getStatusEmail());
    }
}


package dev.lucca.email.consumer;

import dev.lucca.email.dto.EmailDto;
import dev.lucca.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.queue:email-queue}")
    public void listenEmailQueue(@Payload EmailDto emailDto) {
        emailService.sendEmailTransactional(emailDto);
    }
}

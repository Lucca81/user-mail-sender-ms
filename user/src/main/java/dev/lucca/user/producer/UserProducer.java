package dev.lucca.user.producer;

import dev.lucca.user.domain.UserModel;
import dev.lucca.user.dto.EmailDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${app.rabbitmq.queue:email-queue}")
    private String routingKey;

    public void publishEvent(UserModel userModel) {
        var emailDto = new EmailDto(
                userModel.getUserId(),
                userModel.getEmail(),
                "Cadastro realizado com sucesso",
                "Olá " + userModel.getName() + ", sua conta foi criada com sucesso."
        );

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}

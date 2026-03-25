package dev.lucca.email.domain;

import dev.lucca.email.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_email")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailModel {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "email_id")
    private UUID emailId;
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email_from")
    private String emailFrom;
    @Column(name = "email_to")
    private String emailTo;
    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;
    @Column(name = "send_date_email")
    private LocalDateTime sendDateEmail;

    @Enumerated(EnumType.STRING)
    private EmailStatus statusEmail;

}

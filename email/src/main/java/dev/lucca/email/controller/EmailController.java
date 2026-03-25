package dev.lucca.email.controller;

import dev.lucca.email.doc.EmailControllerDoc;
import dev.lucca.email.domain.EmailModel;
import dev.lucca.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController implements EmailControllerDoc {

    private final EmailService emailService;

    @Override
    @GetMapping("/list")
    public ResponseEntity<List<EmailModel>> listAllEmails() {
        return ResponseEntity.ok(emailService.findAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmailModel> findEmailById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(emailService.findById(id));
    }
}


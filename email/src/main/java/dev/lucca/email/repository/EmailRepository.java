package dev.lucca.email.repository;

import dev.lucca.email.domain.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface EmailRepository extends JpaRepository<EmailModel, UUID> {
}

package dev.lucca.email.doc;

import dev.lucca.email.domain.EmailModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Emails", description = "Endpoints para consulta de emails")
public interface EmailControllerDoc {

    @Operation(summary = "Listar emails", description = "Lista todos os emails processados")
    ResponseEntity<List<EmailModel>> listAllEmails();

    @Operation(summary = "Buscar email por id", description = "Retorna um email pelo identificador")
    ResponseEntity<EmailModel> findEmailById(UUID id);
}


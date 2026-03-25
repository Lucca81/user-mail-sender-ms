package dev.lucca.user.doc;

import dev.lucca.user.domain.UserModel;
import dev.lucca.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de usuarios")
public interface UserControllerDoc {

    @Operation(summary = "Criar usuario", description = "Cria um novo usuario e publica um evento")
    ResponseEntity<UserModel> createUser(UserDto userDto);

    @Operation(summary = "Listar usuarios", description = "Lista todos os usuarios cadastrados")
    ResponseEntity<List<UserModel>> listAllUsers();

    @Operation(summary = "Filtrar usuario por email", description = "Retorna um usuario pelo email informado")
    ResponseEntity<UserModel> findUserByEmail(@PathVariable("email") String email);

    @Operation(summary = "Atualizar usuario", description = "Atualiza os dados de um usuario pelo identificador")
    ResponseEntity<UserModel> updateUser(UUID id, UserDto userDto);

    @Operation(summary = "Deletar usuario", description = "Remove um usuario pelo identificador")
    ResponseEntity<Void> deleteUsersById(UUID id);
}


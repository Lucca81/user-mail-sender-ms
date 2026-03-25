package dev.lucca.user.service;

import dev.lucca.user.domain.UserModel;
import dev.lucca.user.dto.UserDto;
import dev.lucca.user.exception.EmailAlreadyUsedException;
import dev.lucca.user.producer.UserProducer;
import dev.lucca.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;

    @Transactional
    public UserModel saveAndPublish(UserModel userModel) {
        if (userRepository.existsByEmail(userModel.getEmail())) {
            throw new EmailAlreadyUsedException(userModel.getEmail());
        }

        UserModel savedUser = userRepository.save(userModel);
        userProducer.publishEvent(savedUser);
        return savedUser;
    }


    public List<UserModel> findAll() {
        return userRepository.findAll();
    }


    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserModel updateById(UUID id, UserDto userDto) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        if (userRepository.existsByEmailAndUserIdNot(userDto.email(), id)) {
            throw new EmailAlreadyUsedException(userDto.email());
        }

        user.setName(userDto.name());
        user.setEmail(userDto.email());
        return userRepository.save(user);
    }

    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }
}

package dev.lucca.user.controller;

import dev.lucca.user.doc.UserControllerDoc;
import dev.lucca.user.domain.UserModel;
import dev.lucca.user.dto.UserDto;
import dev.lucca.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDoc {

    private final UserService userService;


    @Override
    @PostMapping
    public ResponseEntity<UserModel> createUser(@RequestBody UserDto userDto) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveAndPublish(userModel));

    }


    @Override
    @GetMapping("/list")
    public ResponseEntity<List<UserModel>> listAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    @GetMapping("/email/{email}")
    public ResponseEntity<UserModel> findUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable("id") UUID id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateById(id, userDto));
    }


    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsersById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}

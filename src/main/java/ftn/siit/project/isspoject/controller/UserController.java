package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.UserDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
//        try {
//            userService.registerUser(userDto.getUsername(), userDto.getPassword(), userDto.getEmail());
//            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
//        try {
//            User user = userService.loginUser(userDto.getUsername(), userDto.getPassword());
//            return ResponseEntity.ok("Login successful");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
//        Optional<User> user = userService.getUser(id);
//        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        return null;
    }
}


package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.RegistrationRequestDTO;
import ftn.siit.project.isspoject.dto.UserDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
//        if (userService.existsByEmail(user.getEmail())) {
//            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
//        }
//
//        user.setActive(false);
//        user.setSuspendedSince(null);
//
//        userService.save(user);
//
//        userService.sendActivationEmail(user.getEmail());
        if(registrationRequestDTO == null){
            return new ResponseEntity<>("Error, invalid user", HttpStatus.BAD_REQUEST);
        }
        if(registrationRequestDTO.getRole().equals("ORGANIZER")){
            userService.saveOrganizer();
        }
        return new ResponseEntity<>("User was registered, check out the activation code", HttpStatus.CREATED);
    }


    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateUser(@PathVariable String token) {
        boolean activated = true;
//        activated =userService.activateUserByToken(token);
        if (!activated) {
            return new ResponseEntity<>("User was not activated", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User was activated", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        User user = userService.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            return new ResponseEntity<>("Wrong email or password", HttpStatus.UNAUTHORIZED);
        }
        if (!user.isActive()) {
            return new ResponseEntity<>("Account is not active", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setLastname(user.getLastname());
            dto.setAddress(user.getAddress());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setPhotoUrl(user.getPhotoUrl());
            dto.setActive(user.isActive());
            dto.setSuspendedSince(user.getSuspendedSince());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    // Удаление пользователя
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

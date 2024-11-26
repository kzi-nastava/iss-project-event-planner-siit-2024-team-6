package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.*;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.Provider;
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
@RequestMapping(value = "/api/users/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        if (registrationRequestDTO.getEmail() == null) {
            return new ResponseEntity<>("Error, invalid user", HttpStatus.BAD_REQUEST);
        }
        userService.save(registrationRequestDTO);
        return new ResponseEntity<>("User was registered, check out the activation code", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserDTO dto = null;
        if(user instanceof Organizer){
             dto = new OrganizerDTO();
        }else{
            if(user instanceof Provider){
                dto = new ProviderDTO();
                ((ProviderDTO) dto).setCompanyEmail(((Provider) user).getCompanyEmail());
                ((ProviderDTO) dto).setCompanyName(((Provider) user).getCompanyName());
                ((ProviderDTO) dto).setDescription(((Provider) user).getDescription());
                ((ProviderDTO) dto).setCompanyPhotos(((Provider) user).getCompanyPhotos());
                ((ProviderDTO) dto).setOpeningTime(((Provider) user).getOpeningTime());
                ((ProviderDTO) dto).setClosingTime(((Provider) user).getClosingTime());
            }else {
                 dto = new UserDTO();
            }
        }
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPhotoUrl(user.getPhotoUrl());
        dto.setActive(user.getIsActive());
        dto.setSuspendedSince(user.getSuspendedSince());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Integer id, @RequestBody UserDTO updatedUser) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        user.setName(updatedUser.getName());
        user.setLastname(updatedUser.getLastname());
        user.setAddress(updatedUser.getAddress());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setPhotoUrl(updatedUser.getPhotoUrl());


        if (user instanceof Organizer) {
//            Organizer organizer = (Organizer) user;

        } else if (user instanceof Provider) {
//            Provider provider = (Provider) user;
            if (updatedUser instanceof ProviderDTO) {
                ProviderDTO providerDTO = (ProviderDTO) updatedUser;
                ((Provider) user).setDescription(providerDTO.getDescription());
                ((Provider) user).setCompanyPhotos(providerDTO.getCompanyPhotos());
                ((Provider) user).setOpeningTime(providerDTO.getOpeningTime());
                ((Provider) user).setClosingTime(providerDTO.getClosingTime());
            }
        }

        userService.save(user);
        return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
    }
    @PutMapping("{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Integer id, @RequestBody String newPassword) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (user.getPassword().equals(newPassword)) {
            return new ResponseEntity<>("New password matches the old one", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(newPassword);

        userService.save(user);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }


    @GetMapping("activate/{token}")
    public ResponseEntity<String> activateUser(@PathVariable String token) {
        boolean activated = true;
//        activated =userService.activateUserByToken(token);
        if (!activated) {
            return new ResponseEntity<>("User was not activated", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User was activated", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());

        if (user == null || !user.getEmail().equals(loginRequest.getEmail()) || !user.getPassword().equals(loginRequest.getPassword())) {
            return new ResponseEntity<>("Wrong email or password", HttpStatus.UNAUTHORIZED);
        }
        if (!user.getIsActive()) {
            return new ResponseEntity<>("Account is not active", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
    }


    @GetMapping("all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream().map(user -> {
            if (user instanceof Provider) {
                Provider provider = (Provider) user;
                ProviderDTO providerDTO = new ProviderDTO();
                providerDTO.setEmail(provider.getEmail());
                providerDTO.setName(provider.getName());
                providerDTO.setLastname(provider.getLastname());
                providerDTO.setAddress(provider.getAddress());
                providerDTO.setPhoneNumber(provider.getPhoneNumber());
                providerDTO.setPhotoUrl(provider.getPhotoUrl());
                providerDTO.setActive(provider.getIsActive());
                providerDTO.setSuspendedSince(provider.getSuspendedSince());

                providerDTO.setCompanyEmail(provider.getCompanyEmail());
                providerDTO.setCompanyName(provider.getCompanyName());
                providerDTO.setCompanyAddress(provider.getCompanyAddress());
                providerDTO.setDescription(provider.getDescription());
                providerDTO.setCompanyPhotos(provider.getCompanyPhotos());
                providerDTO.setOpeningTime(provider.getOpeningTime());
                providerDTO.setClosingTime(provider.getClosingTime());
                return providerDTO;
            } else {
                UserDTO dto = new UserDTO();
                dto.setEmail(user.getEmail());
                dto.setName(user.getName());
                dto.setLastname(user.getLastname());
                dto.setAddress(user.getAddress());
                dto.setPhoneNumber(user.getPhoneNumber());
                dto.setPhotoUrl(user.getPhotoUrl());
                dto.setActive(user.getIsActive());
                dto.setSuspendedSince(user.getSuspendedSince());
                return dto;
            }
        }).collect(Collectors.toList());

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (user instanceof Organizer organizer) {
            if (organizer.hasFutureEvents()) {
                return new ResponseEntity<>("Cannot deactivate organizer with future events", HttpStatus.BAD_REQUEST);
            }
        }

        if (user instanceof Provider provider) {
            if (provider.hasActiveServices()) {
                return new ResponseEntity<>("Cannot deactivate provider with active services", HttpStatus.BAD_REQUEST);
            }
        }
        userService.delete(user);
        return new ResponseEntity<>("User deactivated", HttpStatus.OK);
    }
}

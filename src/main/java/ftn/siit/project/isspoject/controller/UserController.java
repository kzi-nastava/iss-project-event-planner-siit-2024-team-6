package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.user.*;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.ReportService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ReportService reportService;

    @PostMapping()
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        if (registrationRequestDTO.getEmail() == null || registrationRequestDTO.getRole() == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        User savedUser = userService.save(registrationRequestDTO);

        // Преобразуем сохранённого пользователя в соответствующий DTO
        UserDTO responseDto;
        if (savedUser instanceof Provider) {
            responseDto = new ProviderDTO((Provider) savedUser);
        } else if (savedUser instanceof Organizer) {
            responseDto = new OrganizerDTO((Organizer) savedUser);
        } else {
            responseDto = new UserDTO(savedUser);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("quick-register")
    public ResponseEntity<String> quicklyRegisterUser(@RequestBody QuickRegistrationDTO requestDTO) {
        if (requestDTO.getEmail() == null) {
            return new ResponseEntity<>("Error, invalid user", HttpStatus.BAD_REQUEST);
        }
        userService.save(requestDTO.toUser());
        return new ResponseEntity<>("User was quickly registered, check out the activation code", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserDTO dto = null;
        if(user instanceof Organizer){
             dto = new OrganizerDTO((Organizer) user);
        }else{
            if(user instanceof Provider){
                dto = new ProviderDTO((Provider) user);
                ((ProviderDTO) dto).setCompanyEmail(((Provider) user).getCompanyEmail());
                ((ProviderDTO) dto).setCompanyName(((Provider) user).getCompanyName());
                ((ProviderDTO) dto).setDescription(((Provider) user).getDescription());
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
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Integer id, @RequestBody UserDTO updatedUser) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
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
                ((Provider) user).setOpeningTime(providerDTO.getOpeningTime());
                ((Provider) user).setClosingTime(providerDTO.getClosingTime());
            }
        }

        userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }
  
    @PutMapping("{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Integer id, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (!user.getPassword().equals(passwordChangeDTO.getOldPassword())) {
            return new ResponseEntity<>("Wrong password", HttpStatus.FORBIDDEN);
        }
        if (!passwordChangeDTO.getNewPasswordFirst().equals(passwordChangeDTO.getOldPassword())) {
            return new ResponseEntity<>("New password matches the old one!", HttpStatus.FORBIDDEN);
        }
        if (!passwordChangeDTO.getNewPasswordFirst().equals(passwordChangeDTO.getNewPasswordSecond())) {
            return new ResponseEntity<>("Bad new-password confirmation", HttpStatus.FORBIDDEN);
        }
        user.setPassword(passwordChangeDTO.getNewPasswordFirst());

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

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());

        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Неверные данные для входа
        }

        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Аккаунт неактивен
        }

        // Успешный вход, преобразуем в DTO
        UserDTO userDTO = toDTO(user);
        return ResponseEntity.ok(userDTO);
    }



    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream().map(user -> {
            if (user instanceof Provider) {
                Provider provider = (Provider) user;
                ProviderDTO providerDTO = new ProviderDTO((Provider) user);
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
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Integer id) {
        User user = userService.findById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Пользователь не найден
        }

        if (user instanceof Organizer organizer && organizer.hasFutureEvents()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Организатор с будущими событиями
        }

        if (user instanceof Provider provider && provider.hasActiveServices()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Провайдер с активными услугами
        }

        userService.delete(user);
        UserDTO userDTO = toDTO(user);

        return ResponseEntity.ok(userDTO); // Возвращаем удалённого пользователя
    }


    @PutMapping("{id}/role")
    public ResponseEntity<String> updateRole(@PathVariable Integer id, @RequestParam String newRole) {
        userService.updateRole(id, newRole);
        return ResponseEntity.ok("User successfully updated to " + newRole);

    }

    @GetMapping("{id}/attends")
    public ResponseEntity<List<EventDTO>> getUserEvents(@PathVariable Integer id) {
        List<Event> events = eventService.getEventsUserAttends(id);
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .toList();

        return ResponseEntity.ok(eventDTOs);
    }
    @PostMapping("{blockerId}/block/{blockedId}")
    public ResponseEntity<Block> blockUser(
            @PathVariable Integer blockerId,
            @PathVariable Integer blockedId
    ) {
        Block block = userService.blockUser(blockerId, blockedId);
        return ResponseEntity.ok(block);
    }

    @PostMapping("report")
    public ResponseEntity<UserReportDTO> reportUser(@RequestBody NewUserReportDTO userReportDTO) {
        if (userReportDTO == null || userReportDTO.getReporterId() == null || userReportDTO.getReportedId() == null) {
            throw new IllegalArgumentException("Not all arguments were given while reporting user");
        }
        Report report = new Report(userReportDTO);
        User reporter = userService.findById(userReportDTO.getReporterId());
        User reported = userService.findById(userReportDTO.getReportedId());
        report.setReporter(reporter);
        report.setReported(reported);
        Report savedReport = reportService.save(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserReportDTO(savedReport));
    }
    private UserDTO toDTO(User user) {
        if (user instanceof Provider) {
            return new ProviderDTO((Provider) user);
        } else if (user instanceof Organizer) {
            return new OrganizerDTO((Organizer) user);
        }
        return new UserDTO(user);
    }

}

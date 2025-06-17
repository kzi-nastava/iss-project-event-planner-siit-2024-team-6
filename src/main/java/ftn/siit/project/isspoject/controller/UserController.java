package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.TokenDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.user.*;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.ReportService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;
    public static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping()
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegistrationRequestDTO registrationRequestDTO) {
        if (registrationRequestDTO.getEmail() == null || registrationRequestDTO.getRole() == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Получаем шифровщик паролей


        // Зашифровываем пароль
        registrationRequestDTO.setPassword(passwordEncoder.encode(registrationRequestDTO.getPassword()));

        // Сохраняем пользователя
        User savedUser = userService.save(registrationRequestDTO);

        // Преобразуем сохранённого пользователя в соответствующий DTO
        UserDTO responseDto;
        if (registrationRequestDTO.getRole().equals("provider")) {
            responseDto = new ProviderDTO((Provider) savedUser);
        } else if (registrationRequestDTO.getRole().equals("organizer")) {
            responseDto = new OrganizerDTO((Organizer) savedUser);
        } else {
            responseDto = new UserDTO(savedUser);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/quick-register")
    public  ResponseEntity<Map<String, String>> quicklyRegisterUser(@RequestBody QuickRegistrationDTO requestDTO) {
        Map<String, String> response = new HashMap<>();

        if (requestDTO.getEmail() == null) {
            response.put("message", "Error, invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        userService.save(requestDTO.toUser());
        response.put("message", "User was quickly registered, check out the activation code");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(HttpServletRequest request) {
        // Извлекаем токен из заголовка с помощью getToken
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String userType = user.getUserType();
        UserDTO userDTO = null;
        if(userType.equals("Provider")){
            userDTO = new ProviderDTO((Provider) user);
        } else if (userType.equals("Organizer")) {
            userDTO = new OrganizerDTO((Organizer) user);
        } else if (userType.equals("Admin")) {
            userDTO = new AdminDTO((Admin) user);
        } else if (userType.equals("User")) {
            userDTO = new UserDTO(user);
        }

//        UserDTO dto = null;
//        if(user instanceof Organizer){
//             dto = new OrganizerDTO((Organizer) user);
//        }else{
//            if(user instanceof Provider){
//                dto = new ProviderDTO((Provider) user);
//                ((ProviderDTO) dto).setCompanyEmail(((Provider) user).getCompanyEmail());
//                ((ProviderDTO) dto).setCompanyName(((Provider) user).getCompanyName());
//                ((ProviderDTO) dto).setDescription(((Provider) user).getDescription());
//                ((ProviderDTO) dto).setOpeningTime(((Provider) user).getOpeningTime());
//                ((ProviderDTO) dto).setClosingTime(((Provider) user).getClosingTime());
//            }else {
//                 dto = new UserDTO(user);
//            }
//        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody Object updatedUser, HttpServletRequest request) {
        // Извлекаем токен из заголовка с помощью getToken
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String userType = user.getUserType();

        if(userType.equals("Provider")){
            ((Provider) user).updateFromObject((Map<String, Object>) updatedUser);
        } else if (userType.equals("Organizer")) {
            ((Organizer) user).updateFromObject((Map<String, Object>) updatedUser);
        } else if (userType.equals("Admin")) {
            ((Admin) user).updateFromObject((Map<String, Object>) updatedUser);
        }


//
//        user.setName(updatedUser.getName());
//        user.setLastname(updatedUser.getLastname());
//        user.setAddress(updatedUser.getAddress());
//        user.setPhoneNumber(updatedUser.getPhoneNumber());
//        user.setPhotoUrl(updatedUser.getPhotoUrl());


//        if (user instanceof Organizer) {
////            Organizer organizer = (Organizer) user;
//
//        } else if (user instanceof Provider) {
////            Provider provider = (Provider) user;
//            if (updatedUser instanceof ProviderDTO) {
//                ProviderDTO providerDTO = (ProviderDTO) updatedUser;
//                ((Provider) user).setDescription(providerDTO.getDescription());
//                ((Provider) user).setOpeningTime(providerDTO.getOpeningTime());
//                ((Provider) user).setClosingTime(providerDTO.getClosingTime());
//            }
//        }
        UserDTO userDTO = new UserDTO(user);
        userService.save(user);
        return ResponseEntity.ok(userDTO);
    }
  
    @PutMapping("/profile/password-change")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Проверка старого пароля
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>("Wrong password", HttpStatus.FORBIDDEN);
        }

        // Проверка на совпадение нового пароля со старым
        if (passwordEncoder.matches(passwordChangeDTO.getNewPasswordFirst(), user.getPassword())) {
            return new ResponseEntity<>("New password matches the old one!", HttpStatus.FORBIDDEN);
        }

        // Проверка подтверждения нового пароля
        if (!passwordChangeDTO.getNewPasswordFirst().equals(passwordChangeDTO.getNewPasswordSecond())) {
            return new ResponseEntity<>("Bad new-password confirmation", HttpStatus.FORBIDDEN);
        }

        // Установка нового пароля (хэширование перед сохранением)
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPasswordFirst()));

        userService.save(user);
        return ResponseEntity.ok("Password changed successfully");
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
    public ResponseEntity loginUser(@RequestBody LoginRequestDTO loginRequest) throws BadRequestException {
        User user = userService.findByEmail(loginRequest.getEmail());

//        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Неверные данные для входа
//        }

        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Аккаунт неактивен
        }

        // checking for suspension
        LocalDateTime suspendedSince = user.getSuspendedSince();
        if (suspendedSince != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration suspensionDuration = Duration.between(suspendedSince, now);

            long totalSuspensionMinutes = 3 * 24 * 60;
            long minutesPassed = suspensionDuration.toMinutes();

            if (minutesPassed < totalSuspensionMinutes) {
                long minutesLeft = totalSuspensionMinutes - minutesPassed;
                String formattedDuration = formatDuration(minutesLeft);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "User is suspended. Please try again in " + formattedDuration + ".");
                response.put("minutesLeft", minutesLeft);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            } else {
                // Suspension expired, clear suspension and continue login
                user.setSuspendedSince(null);
                userService.save(user);
            }
        }
        try {
            TokenDTO token = new TokenDTO();

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginRequest.getEmail());
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            String tokenValue = this.tokenUtils.generateToken((User) userDetails);
            Boolean muted = user.getNotificationsMuted();
            token.setToken(tokenValue);
            token.setMuted(muted);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Wrong password!");
        }
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
    @DeleteMapping("/profile")
    public ResponseEntity<UserDTO> deleteUser(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }


        if (user.getUserType().equals("Organizer") && ((Organizer) user).hasFutureEvents()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Организатор с будущими событиями
        }

        if (user.getUserType().equals("Provider") && ((Provider) user).hasActiveServices()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Провайдер с активными услугами
        }

        userService.delete(user);
        UserDTO userDTO = new UserDTO(user);

        return ResponseEntity.ok(userDTO); // Возвращаем удалённого пользователя
    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<UserDTO> deleteUser(@PathVariable Integer id) {
//        User user = userService.findById(id);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Пользователь не найден
//        }
//
//        if (user instanceof Organizer organizer && organizer.hasFutureEvents()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(null); // Организатор с будущими событиями
//        }
//
//        if (user instanceof Provider provider && provider.hasActiveServices()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(null); // Провайдер с активными услугами
//        }
//
//        userService.delete(user);
//        UserDTO userDTO = toDTO(user);
//
//        return ResponseEntity.ok(userDTO); // Возвращаем удалённого пользователя
//    }


    @PutMapping("/{id}/role")
    public ResponseEntity<String> updateRole(@PathVariable Integer id, @RequestParam String newRole) {
        userService.updateRole(id, newRole);
        return ResponseEntity.ok("User successfully updated to " + newRole);

    }

    @GetMapping("/{id}/attends")
    public ResponseEntity<List<EventDTO>> getUserEvents(@PathVariable Integer id) {
        List<Event> events = eventService.getEventsUserAttends(id);
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .toList();

        return ResponseEntity.ok(eventDTOs);
    }

    @PutMapping("/mute/{userId}")
    public ResponseEntity<Void> toggleMute(@PathVariable Integer userId, @RequestParam boolean mute) {
        User user = userService.findById(userId);
        user.setNotificationsMuted(mute);
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    private UserDTO toDTO(User user) {
        if (user instanceof Provider) {
            return new ProviderDTO((Provider) user);
        } else if (user instanceof Organizer) {
            return new OrganizerDTO((Organizer) user);
        }
        return new UserDTO(user);
    }
    public static String formatDuration(long totalMinutes) {
        long days = TimeUnit.MINUTES.toDays(totalMinutes);
        long hours = TimeUnit.MINUTES.toHours(totalMinutes) - TimeUnit.DAYS.toHours(days);
        long minutes = totalMinutes - TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(days == 1 ? " day " : " days ");
        }
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        if (minutes > 0 || (days == 0 && hours == 0)) {
            sb.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        return sb.toString().trim();
    }
}

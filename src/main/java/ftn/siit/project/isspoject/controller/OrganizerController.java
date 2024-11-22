package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        Organizer organizer = userService.findById(id);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(organizer, HttpStatus.OK);
    }

    // 2. Изменение личных данных (кроме email)
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Long id, @RequestBody Organizer updatedOrganizer) {
        Organizer organizer = userService.findById(id);
        if (organizer == null) {
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        organizer.setName(updatedOrganizer.getName());
        organizer.setLastname(updatedOrganizer.getLastname());
        organizer.setAddress(updatedOrganizer.getAddress());
        organizer.setPhoneNumber(updatedOrganizer.getPhoneNumber());
        userService.save(organizer);

        return new ResponseEntity<>("Профиль обновлен", HttpStatus.OK);
    }

    // 3. Смена пароля
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        Organizer organizer = userService.findById(id);
        if (organizer == null) {
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        if (!organizer.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>("Старый пароль неверный", HttpStatus.BAD_REQUEST);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return new ResponseEntity<>("Пароли не совпадают", HttpStatus.BAD_REQUEST);
        }

        organizer.setPassword(request.getNewPassword());
        userService.save(organizer);

        return new ResponseEntity<>("Пароль успешно изменен", HttpStatus.OK);
    }

    // 4. Деактивация профиля (если нет будущих событий)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateAccount(@PathVariable Long id) {
        Organizer organizer = userService.findById(id);
        if (organizer == null) {
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        if (organizer.hasFutureEvents()) {
            return new ResponseEntity<>("Нельзя деактивировать профиль с будущими событиями", HttpStatus.BAD_REQUEST);
        }

        userService.deactivate(organizer);
        return new ResponseEntity<>("Профиль деактивирован", HttpStatus.OK);
    }
}

package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.user.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    List<User> findAll();

    User save(RegistrationRequestDTO registrationRequestDTO);

    void delete(User user);

    User findById(Integer id);

    User save(User user);
    // Za SRP i OCP

    List<User> findEventAttendees(Integer eventId);
    void updateRole(Integer userId, String newRole);
    User suspendUser(Integer userId);
    String getUserRole(Integer userId);
    List<User> findByRole(String role);

    boolean overlapsWithClosedHours(LocalDateTime start, LocalDateTime end, String openingTime, String closingTime);
}

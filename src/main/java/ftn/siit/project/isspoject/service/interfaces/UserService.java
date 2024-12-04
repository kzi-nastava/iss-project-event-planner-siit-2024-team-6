package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.user.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.entity.User;

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
    Block blockUser(Integer blockerId, Integer blockedId);
    User suspendUser(Integer userId);
}

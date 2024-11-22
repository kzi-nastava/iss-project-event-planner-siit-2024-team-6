package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface UserService {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    List<User> findAll();

    void save(RegistrationRequestDTO registrationRequestDTO);
    // Za SRP i OCP
}

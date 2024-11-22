package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.User;

public interface UserService {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    void saveOrganizer();
    // Za SRP i OCP
}

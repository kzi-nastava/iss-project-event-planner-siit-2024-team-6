package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface UserService {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    void saveOrganizer();

    void saveProvider();

    List<User> findAll();
    // Za SRP i OCP
}

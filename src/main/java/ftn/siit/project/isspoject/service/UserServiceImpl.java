package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl implements UserService{
    // dependency inversion
    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return true;
    }

    @Override
    public User findByEmail(String email) {
        User user = new User();
        user.setEmail("john.doe@gmail.com");
        user.setPassword("123");
        user.setPhotoUrl("https://example.com/photo.jpg");
        user.setActive(true);
        user.setSuspendedSince(LocalDateTime.now());
        user.setName("John");
        user.setLastname("Doe");
        user.setAddress("221B Baker Street, London");
        user.setPhoneNumber("+44 20 7946 0958");
        return user;
    }
}

package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @Override
    public void saveOrganizer() {

    }

    @Override
    public void saveProvider() {

    }

    @Override
    public List<User> findAll() {
        User user1 = new User();
        user1.setEmail("john.doe@gmail.com");
        user1.setPassword("123");
        user1.setPhotoUrl("https://example.com/photo.jpg");
        user1.setActive(true);
        user1.setSuspendedSince(LocalDateTime.now());
        user1.setName("John");
        user1.setLastname("Doe");
        user1.setAddress("221B Baker Street, London");
        user1.setPhoneNumber("+44 20 7946 0958");

        User user2 = new User();
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("456");
        user2.setPhotoUrl("https://example.com/jane.jpg");
        user2.setActive(false);
        user2.setSuspendedSince(null);
        user2.setName("Jane");
        user2.setLastname("Smith");
        user2.setAddress("742 Evergreen Terrace, Springfield");
        user2.setPhoneNumber("+1 555-123-4567");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        return users;
    }
}

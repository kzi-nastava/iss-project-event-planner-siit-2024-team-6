package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.Provider;
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
//    private final UserRepository userRepository;
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public boolean existsByEmail(String email) {
        return true;
    }

    @Override
    public User findByEmail(String email) {
        List<User> users = findAll();
        User user = null;
        for(User u: users){
            if(u.getEmail().equals(email))
                user = u;
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        User user1 = new User();
        user1.setId(0);
        user1.setEmail("john.doe@gmail.com");
        user1.setPassword("123");
        user1.setPhotoUrl("https://example.com/photo.jpg");
        user1.setIsActive(true);
        user1.setSuspendedSince(LocalDateTime.now());
        user1.setName("John");
        user1.setLastname("Doe");
        user1.setAddress("221B Baker Street, London");
        user1.setPhoneNumber("+44 20 7946 0958");

        User user2 = new Provider();
        user2.setId(1);
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("456");
        user2.setPhotoUrl("https://example.com/jane.jpg");
        user2.setIsActive(false);
        user2.setSuspendedSince(null);
        user2.setName("Jane");
        user2.setLastname("Smith");
        user2.setAddress("742 Evergreen Terrace, Springfield");
        user2.setPhoneNumber("+1 555-123-4567");
        ((Provider) user2).setCompanyName("company name");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        return users;
    }

    @Override
    public void save(RegistrationRequestDTO registrationRequestDTO) {

    }

    @Override
    public void delete(User user) {

    }

    @Override
    public User findById(Integer id) {
        User user = null;
        try{
            user = findAll().get(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void save(User user) {
        System.out.println(user);
    }

    @Override
    public List<User> findEventAttendees(Integer eventId) {
        return List.of();
    }
}

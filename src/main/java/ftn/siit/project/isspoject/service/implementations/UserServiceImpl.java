package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.user.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.UserRepository;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(RegistrationRequestDTO registrationRequestDTO) {
        User user = new User();
        user.setName(registrationRequestDTO.getName());
        user.setLastname(registrationRequestDTO.getLastname());
        user.setEmail(registrationRequestDTO.getEmail());
        user.setPassword(registrationRequestDTO.getPassword());
        user.setIsActive(true);
        user.setSuspendedSince(null);
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        if (userRepository.existsById(user.getId())) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User with ID " + user.getId() + " does not exist");
        }
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }


//    @Override
//    public void updateRole(Integer userId, String newRole) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
//
//        if ("Provider".equalsIgnoreCase(newRole)) {
//            if (!(user instanceof Provider)) {
//                Provider provider = new Provider(user);
//                userRepository.save(provider);
//            }
//        } else {
//            throw new IllegalArgumentException("Invalid role: " + newRole + ". Allowed role is 'Provider'.");
//        }
//    }
//



    @Override
    public List<User> findEventAttendees(Integer eventId) {
        return List.of();
    }
    @Override
    public void updateRole(Integer userId, String newRole){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
//
//        if ("Provider".equalsIgnoreCase(newRole)) {
//            if (!(user instanceof Provider)) {
//                Provider provider = new Provider(user);
//                userRepository.save(provider);
//            }
//        } else if ("Organizer".equalsIgnoreCase(newRole)) {
//            if (!(user instanceof Organizer)) {
//                Organizer organizer = new Organizer(user);
//                userRepository.save(organizer);
//            }
//        } else {
//            throw new IllegalArgumentException("Invalid role: " + newRole + ". Allowed roles are 'Provider' or 'Organizer'.");
//        }
    }
    public Block blockUser(Integer blockerId, Integer blockedId) {
//        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
//            throw new IllegalArgumentException("User is already blocked.");
//        }
//
//        Block block = new Block();
//        block.setBlockerId(blockerId);
//        block.setBlockedId(blockedId);
//
//        return blockRepository.save(block);
        return null;
    }
    public User suspendUser(Integer userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        user.setSuspendedSince(LocalDateTime.now());
//
//        return userRepository.save(user);
        return null;
    }
}

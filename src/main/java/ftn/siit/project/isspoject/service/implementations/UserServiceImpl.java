package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.user.RegistrationRequestDTO;
import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.UserRepository;
import ftn.siit.project.isspoject.service.interfaces.BlockService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlockService blockService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BlockService blockService) {
        this.userRepository = userRepository;
        this.blockService = blockService;
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
        User user;

        // Определяем тип пользователя на основе роли
        switch (registrationRequestDTO.getRole().toLowerCase()) {
            case "provider":
                Provider provider = new Provider();
                provider.setCompanyEmail(registrationRequestDTO.getCompanyEmail());
                provider.setCompanyName(registrationRequestDTO.getCompanyName());
                provider.setCompanyAddress(registrationRequestDTO.getCompanyAddress());
                provider.setDescription(registrationRequestDTO.getDescription());
                provider.setCompanyPhotos(List.of(registrationRequestDTO.getCompanyPhoto()));
                provider.setOpeningTime(registrationRequestDTO.getOpeningTime());
                provider.setClosingTime(registrationRequestDTO.getClosingTime());
                user = provider;
                break;

            case "organizer":
                Organizer organizer = new Organizer();
                organizer.setMyEvents(new ArrayList<>()); // Пустой список событий по умолчанию
                user = organizer;
                break;

            default:
                // Если роль не указана или некорректная, создаем обычного пользователя
                user = new User();
        }

        // Общие свойства для всех пользователей
        user.setName(registrationRequestDTO.getName());
        user.setLastname(registrationRequestDTO.getLastname());
        user.setEmail(registrationRequestDTO.getEmail());
        user.setPassword(registrationRequestDTO.getPassword());
        user.setPhotoUrl(registrationRequestDTO.getPhotoUrl());
        user.setAddress(registrationRequestDTO.getAddress());
        user.setPhoneNumber(registrationRequestDTO.getPhoneNumber());
        user.setIsActive(true);
        user.setSuspendedSince(null);

        // Сохраняем пользователя в репозитории
        return userRepository.save(user);
    }


    @Override
    public void delete(User user) {
        if (userRepository.existsById(user.getId())) {
            user.setIsActive(false);
            userRepository.save(user); // Logical deleting
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


    @Override
    public void updateRole(Integer userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        if ("Provider".equalsIgnoreCase(newRole)) {
            if (!(user instanceof Provider)) {
//                Provider provider = new Provider(user);
                userRepository.save(user); // NEED TO FIX
            }
        } else {
            throw new IllegalArgumentException("Invalid role: " + newRole + ". Allowed role is 'Provider'.");
        }
    }




    @Override
    public List<User> findEventAttendees(Integer eventId) {
        return userRepository.findEventAttendees(eventId);
    }

//    @Override
//    public void updateRole(Integer userId, String newRole){
////        User user = userRepository.findById(userId)
////                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
////
////        if ("Provider".equalsIgnoreCase(newRole)) {
////            if (!(user instanceof Provider)) {
////                Provider provider = new Provider(user);
////                userRepository.save(provider);
////            }
////        } else if ("Organizer".equalsIgnoreCase(newRole)) {
////            if (!(user instanceof Organizer)) {
////                Organizer organizer = new Organizer(user);
////                userRepository.save(organizer);
////            }
////        } else {
////            throw new IllegalArgumentException("Invalid role: " + newRole + ". Allowed roles are 'Provider' or 'Organizer'.");
////        }
//    }
@Override
public Block blockUser(Integer blockerId, Integer blockedId) {

        User blocker = userRepository.findById(blockerId).get();
        User blocked = userRepository.findById(blockedId).get();
    // Проверяем, существует ли уже блокировка через BlockService
    if (blockService.existsByBlockerIdAndBlockedId(blocker, blocked)) {
        throw new IllegalArgumentException("User is already blocked.");
    }

    Block block = new Block();
    block.setBlockerId(blocker);
    block.setBlockedId(blocked);

    return blockService.save(block);
}
    @Override
    public User suspendUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        user.setSuspendedSince(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    @Override
    public String getUserRole(Integer userId) {
        return "ROLE_" + userRepository.findUserTypeById(userId).toUpperCase();
    }

    @Override
    public List<User> findByRole(String role) {
        return userRepository.findAllByRoleAndIsActive(role);
    }

    public boolean overlapsWithClosedHours(LocalDateTime start, LocalDateTime end, String openingTime, String closingTime) {
        LocalTime opening = LocalTime.parse(openingTime);
        LocalTime closing = LocalTime.parse(closingTime);

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        boolean startsBeforeOpening = startTime.isBefore(opening);
        boolean endsAfterClosing = endTime.isAfter(closing);

        return startsBeforeOpening || endsAfterClosing;
    }

}

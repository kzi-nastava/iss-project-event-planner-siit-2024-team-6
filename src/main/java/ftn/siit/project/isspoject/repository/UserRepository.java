package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
     @Query("SELECT u FROM User u WHERE u.userType = :role AND u.isActive IS TRUE")
    List<User> findAllByRoleAndIsActive(String role);
    @Query("SELECT u.userType FROM User u WHERE u.id = :userId")
    String findUserTypeById(@Param("userId") Integer userId);
}


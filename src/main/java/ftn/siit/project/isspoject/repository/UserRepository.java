package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
// Automatski omogucuje sav rad sa bazom, procitajte o Spring Data i JpaRepository
}

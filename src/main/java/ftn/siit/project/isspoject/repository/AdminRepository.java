package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Admin;
import ftn.siit.project.isspoject.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByEmailAndIsActiveIsTrue(String email);
}

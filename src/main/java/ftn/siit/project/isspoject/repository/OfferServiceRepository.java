package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.OfferService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferServiceRepository extends JpaRepository<OfferService, Long> {
    OfferService findById(int id);
}

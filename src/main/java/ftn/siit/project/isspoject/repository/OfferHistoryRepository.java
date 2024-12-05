package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.OfferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferHistoryRepository extends JpaRepository<OfferHistory, Integer>{
}

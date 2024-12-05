package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.OfferService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer>{
    List<Offer> findTop5ByOrderByLastChangedAsc();

}

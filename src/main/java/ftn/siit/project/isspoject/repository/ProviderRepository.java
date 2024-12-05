package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    List<Provider> findByCompanyName(String companyName);

    Optional<Provider> findByOffersContaining(Offer offer);
}

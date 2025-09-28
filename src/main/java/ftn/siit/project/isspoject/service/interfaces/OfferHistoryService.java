package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.OfferHistory;

import java.util.Optional;

public interface OfferHistoryService {
    OfferHistory save(OfferHistory offerHistory);
    OfferHistory update(OfferHistory updatedOfferHistory);
    void delete(Integer id);

    Optional<OfferHistory> findById(Integer id);
    OfferHistory add(int id, Offer offer);
}

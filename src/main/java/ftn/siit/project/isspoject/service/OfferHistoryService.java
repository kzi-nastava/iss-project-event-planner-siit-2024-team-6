package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.OfferHistory;

public interface OfferHistoryService {
    OfferHistory save(OfferHistory offerHistory);
    OfferHistory update(OfferHistory offerHistory);
    void delete(Integer id);

    OfferHistory findById(Integer id);
    void add(Offer offer);
}

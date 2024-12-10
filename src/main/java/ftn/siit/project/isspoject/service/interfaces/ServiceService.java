package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.OfferService;

import java.util.List;

public interface ServiceService {
    List<OfferService> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable);
    OfferService findById(Integer id);
    OfferService save(OfferService offerService);
    OfferService update(OfferService offerService);
    void delete(OfferService offerService);
}

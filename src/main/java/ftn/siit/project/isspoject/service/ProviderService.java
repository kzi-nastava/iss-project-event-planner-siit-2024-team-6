package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;

public interface ProviderService {
    Provider findByOffer(Offer offer);
    Provider findById(Integer id);

    Provider update(Provider provider);
}

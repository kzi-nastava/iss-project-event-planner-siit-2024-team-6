package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceImpl implements ProviderService {

    //@Autowired
    //private ProviderRepository providerRepository;

    @Override
    public Provider findByOffer(Offer offer) {
        return null;
    }

    @Override
    public Provider findById(Integer id) {
        return null;
    }

    @Override
    public Provider update(Provider provider) {
        return null;
    }
}

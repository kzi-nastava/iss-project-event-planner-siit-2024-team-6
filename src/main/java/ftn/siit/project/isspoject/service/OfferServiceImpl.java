package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OfferServiceImpl implements OfferService {

    //@Autowired
    //private OfferRepository offerRepository;

    @Override
    public List<Offer> findAll() {
        return List.of();
    }
    @Override
    public Offer findById(Integer offerId) {
        return null;
    }
    @Override
    public List<Offer> findByProvider(Provider provider) {
        return List.of();
    }

    @Override
    public List<Offer> findTopFive() {
        return List.of();
    }


    @Override
    public void save(Offer offer) {

    }

    @Override
    public void update(Offer offer) {

    }

    @Override
    public void delete(Offer offer) {

    }
}

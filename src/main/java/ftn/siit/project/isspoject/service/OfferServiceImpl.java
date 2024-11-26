package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.OfferDTO;
import ftn.siit.project.isspoject.dto.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.EventType;
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
    public List<Offer> allOffersWithCategory(Category category) {
        return List.of();
    }

    @Override
    public List<Offer> getFilteredServices(Provider provider, String name, String category, String eventType, Double price, Boolean isAvailable) {
        return List.of();
    }

    @Override
    public List<PriceListOfferDTO> getPriceList(List<Offer> offers) {
        return List.of();
    }

    @Override
    public Offer updatePrice(PriceListOfferDTO priceListOfferDTO) {
        Offer offer = findById(priceListOfferDTO.getId());
        offer.setPrice(priceListOfferDTO.getPrice());
        offer.setSale(priceListOfferDTO.getSale());
        return update(offer);
    }

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
    public Offer update(Offer offer) {
        return null;
    }

    @Override
    public void delete(Offer offer) {

    }
}

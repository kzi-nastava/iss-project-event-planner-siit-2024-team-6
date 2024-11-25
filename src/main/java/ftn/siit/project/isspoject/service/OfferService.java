package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.Provider;

import java.util.List;

public interface OfferService {
    List<PriceListOfferDTO> getPriceList(List<Offer> offers);
    Offer updatePrice(PriceListOfferDTO priceListOfferDTO);
    List<Offer> findAll();
    Offer findById(Integer id);
    List<Offer> findByProvider(Provider provider);
    List<Offer> findTopFive();

    void save(Offer offer);
    Offer update(Offer offer);
    void delete(Offer offer);
}

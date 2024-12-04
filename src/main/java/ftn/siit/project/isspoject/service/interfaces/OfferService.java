package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferService {
    List<Offer> allOffersWithCategory(Category category);
    List<Offer> getFilteredServices(Provider provider, String name, String category, String eventType, Double price, Boolean isAvailable);
    List<PriceListOfferDTO> getPriceList(List<Offer> offers);
    Offer updatePrice(PriceListOfferDTO priceListOfferDTO);
    List<Offer> findAll();
    Offer findById(Integer id);
    List<Offer> findByProvider(Provider provider);
    List<Offer> findTopFive();

    Offer save(NewOfferDTO dto);
    void save(Offer offer);
    Offer update(Offer offer);
    Offer update(OfferDTO dto);
    void delete(Offer offer);
    List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService);
}

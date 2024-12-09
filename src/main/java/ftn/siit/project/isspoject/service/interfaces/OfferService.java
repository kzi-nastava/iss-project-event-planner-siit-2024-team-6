package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferService {
    List<Offer> allOffersWithCategory(Category category);
    List<PriceListOfferDTO> getPriceList(Provider p);
    Offer updatePrice(int offerId, NewPriceListOfferDTO dto);
    List<Offer> findAll();
    Offer findById(Integer id);
    List<Offer> findTopFive();

    Offer save(NewOfferDTO dto);
    Offer save(Offer offer);
    Offer update(Offer offer);
    Offer update(int id, NewOfferDTO dto);
    void delete(Offer offer);
    List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService);
}

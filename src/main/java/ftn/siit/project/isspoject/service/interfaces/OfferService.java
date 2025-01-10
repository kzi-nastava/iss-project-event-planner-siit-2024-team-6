package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferService {
    List<Offer> allOffersWithCategory(Category category);
    List<PriceListOfferDTO> getPriceList(Provider p);
    Offer updatePrice(int offerId, NewPriceListOfferDTO dto);
    List<Offer> findAll();
    Page<Offer> findAll(Pageable page);
    Offer findById(Integer id);
    List<Offer> findTopFive();

    Offer save(NewOfferDTO dto);
    Offer save(Offer offer);
    Offer update(Offer offer);
    Offer update(int id, NewOfferDTO dto);
    void delete(Offer offer);
    List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService);
    Page<OfferDTO> searchOffers(String name, String description, Double maxPrice, Boolean isOnSale, LocalDateTime startDate, LocalDateTime endDate, List<String> categories, Boolean isService, Boolean isProduct, Pageable pageable);

}

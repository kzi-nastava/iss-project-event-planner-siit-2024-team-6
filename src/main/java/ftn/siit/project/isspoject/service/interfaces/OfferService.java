package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferService {
    List<Offer> allOffersWithCategory(Category category);
    Offer updatePrice(int offerId, NewPriceListOfferDTO dto);
    List<Offer> findAll();
    Page<Offer> findAll(Pageable page);
    Page<Offer> findAccepted(Pageable page);
    Offer findById(Integer id);
    List<Offer> findTopFive();

    Offer save(NewOfferDTO dto);
    Offer save(Offer offer);
    Offer update(Offer offer);
    Offer update(int id, NewOfferDTO dto);
    void delete(Offer offer);
    Page<OfferDTO> searchOffers(String name, String description, Double maxPrice, Boolean isOnSale, LocalDateTime startDate, LocalDateTime endDate, String category, String eventType, Boolean isService, Boolean isProduct, Pageable pageable, String sortDir);

    Page<OfferDTO> searchProviderServices(Integer id, String name, Double maxPrice, Boolean isOnSale, String category, String eventType, Boolean isAvailable, Pageable pageable);
    Page<OfferDTO> searchOffers(NewBudgetDTO dto, Pageable pageable);
}

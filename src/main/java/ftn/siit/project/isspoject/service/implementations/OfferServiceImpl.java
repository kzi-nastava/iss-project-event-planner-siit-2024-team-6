package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.repository.OfferRepository;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Override
    public List<Offer> allOffersWithCategory(Category category) {
        return new ArrayList<>();
    }

    @Override
    public List<Offer> getFilteredServices(Provider provider, String name, String category, String eventType, Double price, Boolean isAvailable) {
//        return offers.stream()
//                .filter(offer -> (provider == null || offer.getProvider().equals(provider)) &&
//                        (name == null || offer.getName().toLowerCase().contains(name.toLowerCase())) &&
//                        (category == null || offer.getCategory().getName().equalsIgnoreCase(category)) &&
//                        (eventType == null || offer.getEventTypes().stream().anyMatch(event -> event.getName().equalsIgnoreCase(eventType))) &&
//                        (price == null || offer.getPrice() <= price) &&
//                        (isAvailable == null || offer.getIsAvailable().equals(isAvailable)))
//                .toList();
        return List.of();
    }

    @Override
    public List<PriceListOfferDTO> getPriceList(List<Offer> offers) {
        return List.of();
    }

    @Override
    public Offer updatePrice(NewPriceListOfferDTO dto) {
        return null;
    }

    @Override
    public List<Offer> findAll() {

        return offerRepository.findAll();
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
        return offerRepository.findTop5ByOrderByLastChangedAsc();
    }

    @Override
    public Offer save(NewOfferDTO dto) {
        return null;
    }


    @Override
    public void save(Offer offer) {

    }

    @Override
    public Offer update(Offer offer) {
        return null;
    }

    @Override
    public Offer update(NewOfferDTO dto) {
        return null;
    }

    @Override
    public void delete(Offer offer) {

    }

    @Override
    public List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService) {
        return List.of();
    }
}

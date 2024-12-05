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

        return List.of(
                new Offer(1, Status.ACCEPTED, "Offer 1", "Description 1", 100.0, 10.0,
                        List.of("photo1.jpg", "photo2.jpg"), true, true, false,
                        LocalDateTime.now(), new Category(), List.of(new EventType())),
                new Offer(2, Status.ACCEPTED, "Offer 2", "Description 2", 200.0, 20.0,
                        List.of("photo3.jpg", "photo4.jpg"), true, false, false,
                        LocalDateTime.now().plusDays(1), new Category(), List.of(new EventType())),
                new Offer(3, Status.ACCEPTED, "Offer 3", "Description 3", 300.0, 30.0,
                        List.of("photo5.jpg", "photo6.jpg"), true, true, false,
                        LocalDateTime.now().plusDays(2), new Category(), List.of(new EventType())),
                new Offer(4, Status.ACCEPTED, "Offer 4", "Description 4", 400.0, 40.0,
                        List.of("photo7.jpg", "photo8.jpg"), false, false, false,
                        LocalDateTime.now().plusDays(3), new Category(), List.of(new EventType())),
                new Offer(5, Status.ACCEPTED, "Offer 5", "Description 5", 500.0, 50.0,
                        List.of("photo9.jpg", "photo10.jpg"), true, true, false,
                        LocalDateTime.now().plusDays(4), new Category(), List.of(new EventType())),
                new Offer(6, Status.PENDING, "Offer 6", "Description 6", 600.0, 60.0,
                        List.of("photo11.jpg", "photo12.jpg"), true, false, false,
                        LocalDateTime.now().plusDays(5), new Category(), List.of(new EventType())),
                new Offer(7, Status.ACCEPTED, "Offer 7", "Description 7", 700.0, 70.0,
                        List.of("photo13.jpg", "photo14.jpg"), true, true, false,
                        LocalDateTime.now().plusDays(6), new Category(), List.of(new EventType()))
        );
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

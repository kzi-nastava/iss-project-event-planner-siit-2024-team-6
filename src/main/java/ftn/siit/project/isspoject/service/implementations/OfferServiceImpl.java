package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Service
public class OfferServiceImpl implements OfferService {
    private final List<Offer> offers = new ArrayList<>();

    public OfferServiceImpl() {
        // Hardcoded categories
        Category photography = new Category(1, "Photography", "Capture moments that last forever");
        Category catering = new Category(2, "Catering", "Food and beverage services");
        Category entertainment = new Category(3, "Entertainment", "Music, shows, and activities");

        // Hardcoded event types
        EventType wedding = new EventType(10, "Wedding", "Special occasions and ceremonies", true);
        EventType corporate = new EventType(12, "Corporate", "Professional corporate events", true);
        EventType birthday = new EventType(15, "Birthday", "Celebration events for birthdays", true);

        // Hardcoded services
        Offer o = new Offer();
        offers.add(o.toOffer("Service",
                1,
                Status.ACCEPTED,
                "Wedding Photography",
                "Professional photography services for weddings.",
                1500.0,
                10.0,
                List.of("photo1.jpg", "photo2.jpg"),
                true,
                true,
                false,
                LocalDateTime.now().minusDays(1),
                photography,
                List.of(wedding, corporate),
                "Includes post-editing and two photographers",
                2,
                8,
                6,
                48,
                24
        ));

        offers.add(o.toOffer("Service",
                2,
                Status.ACCEPTED,
                "Event Catering",
                "Delicious catering services for all events.",
                2500.0,
                15.0,
                List.of("menu1.jpg", "menu2.jpg"),
                true,
                true,
                false,
                LocalDateTime.now().minusDays(2),
                catering,
                List.of(wedding, birthday),
                "Customized menus and staff included",
                4,
                12,
                10,
                72,
                48
        ));

        offers.add(o.toOffer("Service",
                3,
                Status.ACCEPTED,
                "DJ and Music Service",
                "Top-notch music and DJ services for your events.",
                1000.0,
                0.0,
                List.of("dj1.jpg", "dj2.jpg"),
                true,
                true,
                false,
                LocalDateTime.now().minusDays(3),
                entertainment,
                List.of(corporate, birthday),
                "Professional DJ and sound setup included",
                2,
                6,
                4,
                48,
                24
        ));
    }

    //@Autowired
    //private OfferRepository offerRepository;

    @Override
    public List<Offer> allOffersWithCategory(Category category) {
        return offers.stream()
                .filter(offer -> offer.getCategory().getId().equals(category.getId()))
                .toList();
    }

    @Override
    public List<Offer> getFilteredServices(Provider provider, String name, String category, String eventType, Double price, Boolean isAvailable) {
        return offers.stream()
                .filter(offer -> (provider == null || offer.getProvider().equals(provider)) &&
                        (name == null || offer.getName().toLowerCase().contains(name.toLowerCase())) &&
                        (category == null || offer.getCategory().getName().equalsIgnoreCase(category)) &&
                        (eventType == null || offer.getEventTypes().stream().anyMatch(event -> event.getName().equalsIgnoreCase(eventType))) &&
                        (price == null || offer.getPrice() <= price) &&
                        (isAvailable == null || offer.getIsAvailable().equals(isAvailable)))
                .toList();
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

        return List.of(
                new Offer(1, Status.ACCEPTED, "Offer 1", "Description 1", 100.0, 10.0, List.of("photo1.jpg", "photo2.jpg"), true, true, false, LocalDateTime.now(), new Category(), new Provider(), List.of(new EventType())),
                new Offer(2, Status.ACCEPTED, "Offer 2", "Description 2", 200.0, 20.0, List.of("photo3.jpg", "photo4.jpg"), true, false, false, LocalDateTime.now().plusDays(1), new Category(), new Provider(), List.of(new EventType())),
                new Offer(3, Status.ACCEPTED, "Offer 3", "Description 3", 300.0, 30.0, List.of("photo5.jpg", "photo6.jpg"), true, true, false, LocalDateTime.now().plusDays(2), new Category(), new Provider(), List.of(new EventType())),
                new Offer(4, Status.ACCEPTED, "Offer 4", "Description 4", 400.0, 40.0, List.of("photo7.jpg", "photo8.jpg"), false, false, false, LocalDateTime.now().plusDays(3), new Category(), new Provider(), List.of(new EventType())),
                new Offer(5, Status.ACCEPTED, "Offer 5", "Description 5", 500.0, 50.0, List.of("photo9.jpg", "photo10.jpg"), true, true, false, LocalDateTime.now().plusDays(4), new Category(), new Provider(), List.of(new EventType())),
                new Offer(6, Status.PENDING, "Offer 6", "Description 6", 600.0, 60.0, List.of("photo11.jpg", "photo12.jpg"), true, false, false, LocalDateTime.now().plusDays(5), new Category(), new Provider(), List.of(new EventType())),
                new Offer(7, Status.ACCEPTED, "Offer 7", "Description 7", 700.0, 70.0, List.of("photo13.jpg", "photo14.jpg"), true, true, false, LocalDateTime.now().plusDays(6), new Category(), new Provider(), List.of(new EventType()))
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

        return List.of(
                new Offer(1, Status.ACCEPTED, "Offer 1", "Description 1", 100.0, 10.0, List.of("photo1.jpg", "photo2.jpg"), true, true, false, LocalDateTime.now(), new Category(), new Provider(), List.of(new EventType())),
                new Offer(2, Status.ACCEPTED, "Offer 2", "Description 2", 200.0, 20.0, List.of("photo3.jpg", "photo4.jpg"), true, false, false, LocalDateTime.now().plusDays(1), new Category(), new Provider(), List.of(new EventType())),
                new Offer(3, Status.ACCEPTED, "Offer 3", "Description 3", 300.0, 30.0, List.of("photo5.jpg", "photo6.jpg"), true, true, false, LocalDateTime.now().plusDays(2), new Category(), new Provider(), List.of(new EventType())),
                new Offer(4, Status.ACCEPTED, "Offer 4", "Description 4", 400.0, 40.0, List.of("photo7.jpg", "photo8.jpg"), false, false, false, LocalDateTime.now().plusDays(3), new Category(), new Provider(), List.of(new EventType())),
                new Offer(5, Status.ACCEPTED, "Offer 5", "Description 5", 500.0, 50.0, List.of("photo9.jpg", "photo10.jpg"), true, true, false, LocalDateTime.now().plusDays(4), new Category(), new Provider(), List.of(new EventType()))
        );
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

    @Override
    public List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService) {
        return List.of();
    }
}

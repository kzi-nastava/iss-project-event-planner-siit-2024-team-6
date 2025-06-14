package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListItemDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.repository.OfferRepository;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OfferHistoryService offerHistoryService;

    @Override
    public List<Offer> allOffersWithCategory(Category category) {
        return offerRepository.findNonDeletedOffersByCategory(category.getId());
    }

    @Override
    public Offer updatePrice(int offerId, NewPriceListItemDTO dto) {
        Offer o = offerRepository.findById(offerId).orElseThrow(() -> new NotFoundException("Offer not found"));
        o.setSale(dto.getSalePrice());
        if(dto.getSalePrice() == 0.0 || dto.getSalePrice() == null){
            o.setSale(null);
        }else if(dto.getPrice() < dto.getSalePrice()){
            throw new IllegalArgumentException("Price must be greater than or equal to Sale");
        }
        o.setPrice(dto.getPrice());
        updateOfferHistory(o);
        return update(o);
    }

    @Override
    public List<Offer> findAll() {
        return offerRepository.findByIsDeletedFalseOrIsDeletedIsNull();
    }

    @Override
    public Page<Offer> findAll(Pageable page) {
        return offerRepository.findAll(page);
    }
    @Override
    public Page<Offer> findAccepted(Pageable page) {
        return offerRepository.findAllAccepted(page);
    }

    @Override
    public Offer findById(Integer offerId) {
        Offer o = offerRepository.findById(offerId).orElseThrow(() -> new NotFoundException("Offer not found"));
        if (o.getIsDeleted()) {
            throw new NotFoundException("Offer is deleted");
        }
        return o;
    }

    @Override
    public List<Offer> findTopFive() {
        return offerRepository.findTop5ByOrderByLastChangedAsc();
    }

    @Override
    public Offer save(NewOfferDTO dto) {
        Offer offer = new Offer(dto, categoryRepository.findByNameIgnoreCase(dto.getCategory()));
        offer.setLastChanged(LocalDateTime.now());
        updateOfferHistory(offer);
        return offerRepository.save(offer);
    }

    @Override
    public Offer save(Offer offer) {
        offer.setLastChanged(LocalDateTime.now());
        updateOfferHistory(offer);
        return offerRepository.save(offer);
    }

    @Override
    public Offer update(Offer offer) {
        Offer existingOffer = offerRepository.findById(offer.getId()).orElseThrow(() -> new NotFoundException("Offer not found"));
        existingOffer.setName(offer.getName());
        existingOffer.setCategory(offer.getCategory());
        if(offer.getSale() == 0.0 || offer.getSale() == null){
            existingOffer.setSale(null);
        }else{
            existingOffer.setSale(offer.getSale());
        }
        existingOffer.setIsDeleted(offer.getIsDeleted());
        existingOffer.setPrice(offer.getPrice());
        existingOffer.setDescription(offer.getDescription());
        existingOffer.setEventTypes(offer.getEventTypes());
        existingOffer.setIsVisible(offer.getIsVisible());
        existingOffer.setIsAvailable(offer.getIsAvailable());
        existingOffer.setPhotos(offer.getPhotos());
        existingOffer.setLastChanged(LocalDateTime.now());
        updateOfferHistory(existingOffer);
        return offerRepository.save(existingOffer);
    }

    @Override
    public Offer update(int id, NewOfferDTO dto) {
        Offer offer = new Offer(dto, categoryRepository.findByNameIgnoreCase(dto.getCategory()));
        offer.setId(id);
        return update(offer);
    }

    @Override
    public void delete(Offer offer) {
        offer.setIsDeleted(true);
        update(offer);
    }

    @Override
    public Page<OfferDTO> searchProviderServices(Integer id, String name, Double maxPrice, Boolean isOnSale, String category, String eventType, Boolean isAvailable, Pageable pageable) {
        List<Offer> offers = offerRepository.findAll();

        List<Offer> filteredOffers = offers.stream()
                .filter(offer ->
                        (name == null || name.isEmpty() || offer.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(offer -> maxPrice == null ||
                        (offer.getSale() != null && offer.getSale() > 0 ? offer.getSale() <= maxPrice : offer.getPrice() <= maxPrice)) //if it is on sale compare max price to sale price
                .filter(offer -> isOnSale == null || (!isOnSale) || (isOnSale && offer.getSale() != null && offer.getSale() > 0)) //if isOnSale is false then return all
                .filter(offer -> category == null || category.isEmpty() || category.toLowerCase().equals(offer.getCategory().getName().toLowerCase()))
                .filter(offer -> (offer.isService() == true))
                .filter(offer -> ( isAvailable == null || offer.getIsAvailable() == null || offer.getIsAvailable() == isAvailable))
                .filter(offer -> eventType == null || eventType.isEmpty() ||
                        offer.getEventTypes().stream().anyMatch(type -> type.getName().equalsIgnoreCase(eventType))
                )
                .filter(offer -> (offer.getIsDeleted() == null || offer.getIsDeleted() == false))
                .filter(offer -> offer.getProvider().getId() == id)
                .collect(Collectors.toList());

        return paginateOffers(filteredOffers, pageable);
    }

    @Override
    public Page<OfferDTO> searchOffers(NewBudgetDTO dto, Pageable pageable) {

        List<Offer> offers = offerRepository.findAll();

        if (dto.getBudgetItems() == null || dto.getBudgetItems().isEmpty()) {
            List<Offer> visibleOffers = offers.stream()
                    .filter(offer -> (offer.getIsDeleted() == null || !offer.getIsDeleted()))
                    .filter(Offer::getIsVisible)
                    .filter(offer -> (offer.getStatus() == Status.ACCEPTED))
                    .collect(Collectors.toList());

            return paginateOffers(visibleOffers, pageable);
        }
        // Build a map of category to max price
        Map<String, Double> categoryMaxPriceMap = dto.getBudgetItems().stream()
                .collect(Collectors.toMap(
                        NewBudgetItemDTO::getCategory,
                        item -> {
                            Double max = item.getMaxPrice() != 0.0 ? item.getMaxPrice() : 0.0;
                            Double curr = item.getCurrPrice() != 0.0 ? item.getCurrPrice() : 0.0;
                            return max - curr;
                        }
                ));

        List<Offer> filteredOffers = offers.stream()
                .filter(offer -> (offer.getIsDeleted() == null || !offer.getIsDeleted()))
                .filter(Offer::getIsVisible)
                .filter(offer -> {
                    String category = offer.getCategory().getName();
                    Double maxAllowed = categoryMaxPriceMap.get(category);
                    return maxAllowed != null && (
                            (offer.getPrice() != null && offer.getPrice() <= maxAllowed) ||
                                    (offer.getSale() != null && offer.getSale() <= maxAllowed)
                    );
                })
                .collect(Collectors.toList());


        return paginateOffers(filteredOffers, pageable);

    }

    private Page<OfferDTO> paginateOffers(List<Offer> offers, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), offers.size());

        List<OfferDTO> paginated = offers.subList(start, end).stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(paginated, pageable, offers.size());
    }

    @Override
    public Page<OfferDTO> searchOffers(String name, String description, Double maxPrice, Boolean isOnSale, LocalDateTime startDate, LocalDateTime endDate, String category, String eventType, Boolean isService, Boolean isProduct, Pageable pageable,String sortDir) {
        System.out.println("Search Offers called with parameters:");
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Max Price: " + maxPrice);
        System.out.println("Is On Sale: " + isOnSale);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Category: " + category);
        System.out.println("EventType: " + eventType);
        System.out.println("Is Service: " + isService);
        System.out.println("Is Product: " + isProduct);
        System.out.println("Page Number: " + pageable.getPageNumber());
        System.out.println("Page Size: " + pageable.getPageSize());

        List<Offer> offers = offerRepository.findAll();

        List<Offer> filteredOffers = offers.stream()
                .filter(offer -> !offer.getIsDeleted())
                .filter(offer -> offer.getIsVisible())
                .filter(offer -> offer.getStatus() == Status.ACCEPTED)
                .filter(offer ->
                        (name == null || name.isEmpty() || offer.getName().toLowerCase().contains(name.toLowerCase())) ||
                                (description == null || description.isEmpty() || offer.getDescription().toLowerCase().contains(description.toLowerCase())))
                .filter(offer -> maxPrice == null ||
                        (offer.getSale() != null && offer.getSale() > 0 ? offer.getSale() <= maxPrice : offer.getPrice() <= maxPrice)) //if it is on sale compare max price to sale price
                .filter(offer -> isOnSale == null || (!isOnSale) || (isOnSale && offer.getSale() != null && offer.getSale() > 0)) //if isOnSale is false then return all
                .filter(offer -> category == null || category.isEmpty() || category.toLowerCase().equals(offer.getCategory().getName().toLowerCase()))
                .filter(offer -> (isService == null || (isService && offer.isService())) ||
                        (isProduct == null || (isProduct && offer.isProduct())))
                .filter(offer -> eventType == null || eventType.isEmpty() ||
                        offer.getEventTypes().stream().anyMatch(type -> type.getName().equalsIgnoreCase(eventType))
                )
                .sorted((o1, o2) -> {
                    double price1 = (o1.getSale() != null && o1.getSale() > 0) ? o1.getSale() : o1.getPrice();
                    double price2 = (o2.getSale() != null && o2.getSale() > 0) ? o2.getSale() : o2.getPrice();
                    return sortDir != null && sortDir.equalsIgnoreCase("desc")
                            ? Double.compare(price2, price1)
                            : Double.compare(price1, price2);
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredOffers.size());
        List<Offer> paginatedOffers = filteredOffers.subList(start, end);
        List<OfferDTO> paginatedOfferDTOs = filteredOffers.subList(start, end)
                .stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedOfferDTOs, pageable, filteredOffers.size());
    }

    private void updateOfferHistory(Offer offer) {
        Optional<OfferHistory> oh = offerHistoryService.findById(offer.getId());
        if(oh.isEmpty()){
            OfferHistory offerHistory = new OfferHistory();
            offerHistory.setId(offer.getId());
            offerHistory.setOffers(List.of(offer));
            offerHistory.setTimestamps(List.of(LocalDateTime.now()));
            offerHistoryService.save(offerHistory);
        }else{
            OfferHistory offerHistory = oh.get();
            offerHistory.getOffers().add(offer);
            offerHistory.getTimestamps().add(LocalDateTime.now());
            offerHistoryService.save(offerHistory);
        }
    }

}

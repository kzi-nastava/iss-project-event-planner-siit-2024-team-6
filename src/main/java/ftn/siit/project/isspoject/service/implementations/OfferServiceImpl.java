package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.repository.OfferRepository;
import ftn.siit.project.isspoject.repository.ServiceRepository;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@org.springframework.stereotype.Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Offer> allOffersWithCategory(Category category) {
        return offerRepository.findNonDeletedOffersByCategory(category.getId());
    }

    @Override
    public List<PriceListOfferDTO> getPriceList(Provider p) {
        List<Offer> offers = offerRepository.findByProviderAndIsDeletedFalseOrIsDeletedIsNull(p);
        List<PriceListOfferDTO> dtos = new ArrayList<>();
        for (Offer offer : offers) {
            PriceListOfferDTO priceListOfferDTO = new PriceListOfferDTO();
            priceListOfferDTO.setPrice(offer.getPrice());
            priceListOfferDTO.setName(offer.getName());
            priceListOfferDTO.setSale(offer.getSale());
            priceListOfferDTO.setSalePrice(offer.getPrice()*offer.getSale()/100);
            dtos.add(priceListOfferDTO);
        }
        return dtos;
    }

    @Override
    public Offer updatePrice(int offerId, NewPriceListOfferDTO dto) {
        Offer o = offerRepository.findById(offerId).orElseThrow(() -> new NotFoundException("Offer not found"));
        o.setPrice(dto.getPrice());
        o.setName(dto.getName());
        o.setSale(dto.getSale());
        return update(o);
    }

    @Override
    public List<Offer> findAll() {
        return offerRepository.findByIsDeletedFalseOrIsDeletedIsNull();
    }

    @Override
    public Offer findById(Integer offerId) {
        Offer o = offerRepository.findById(offerId).orElseThrow(() -> new NotFoundException("Offer not found"));
        if(o.getIsDeleted()){
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
        return offerRepository.save(offer);
    }

    @Override
    public Offer save(Offer offer) {
        offer.setLastChanged(LocalDateTime.now());
        return offerRepository.save(offer);
    }

    @Override
    public Offer update(Offer offer) {
        Offer existingOffer = offerRepository.findById(offer.getId()).orElseThrow(() -> new NotFoundException("Offer not found"));
        existingOffer.setName(offer.getName());
        existingOffer.setCategory(offer.getCategory());
        existingOffer.setSale(offer.getSale());
        existingOffer.setIsDeleted(offer.getIsDeleted());
        existingOffer.setPrice(offer.getPrice());
        existingOffer.setDescription(offer.getDescription());
        existingOffer.setEventTypes(offer.getEventTypes());
        existingOffer.setIsVisible(offer.getIsVisible());
        existingOffer.setIsAvailable(offer.getIsAvailable());
        existingOffer.setPhotos(offer.getPhotos());
        existingOffer.setLastChanged(LocalDateTime.now());
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
    public List<Offer> searchItems(String name, String description, Double minPrice, Double maxPrice, LocalDateTime startDate, LocalDateTime endDate, String category, Boolean isService) {
        return offerRepository.searchItems(name, description, minPrice, maxPrice, startDate, endDate, category, isService);
    }
}

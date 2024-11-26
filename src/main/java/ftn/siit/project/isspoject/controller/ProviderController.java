package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.OfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.CategoryService;
import ftn.siit.project.isspoject.service.OfferService;
import ftn.siit.project.isspoject.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/providers")
public class ProviderController {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OfferService offerService;

    @GetMapping("offers/{providerId}/all")
    public ResponseEntity<List<OfferDTO>> getAllOffers(@PathVariable int providerId) {
        Provider provider = providerService.findById(providerId);
        if (provider == null) {ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider not found");}
        List<OfferDTO> dtos = provider.getMyOffers().stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("offers/{providerId}/create")
    public ResponseEntity<String> createOffer(@PathVariable int providerId, @RequestBody OfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider not found");}
        Offer created = toOffer(dto);
        provider.getMyOffers().add(created);
        providerService.update(provider);
        offerService.save(created);
        return ResponseEntity.ok("Offer created");
    }

    @PutMapping("offers/{providerId}/update")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable int providerId, @RequestBody OfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {return ResponseEntity.notFound().build();}
        Offer oldOffer = offerService.findById(dto.getId());
        if(oldOffer == null) {return ResponseEntity.notFound().build();}
        Offer updated = offerService.update(oldOffer);
        provider.getMyOffers().remove(oldOffer);
        provider.getMyOffers().add(updated);
        providerService.update(provider);
        return ResponseEntity.ok().body(new OfferDTO(updated));
    }
    @PutMapping("offers/{providerId}/delete/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable int providerId, @PathVariable int offerId) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {return ResponseEntity.notFound().build();}
        Offer offer = offerService.findById(offerId);
        if(offer == null) {return ResponseEntity.notFound().build();}
        provider.getMyOffers().remove(offer);
        offerService.delete(offer);
        providerService.update(provider);
        return ResponseEntity.ok().body("Offer deleted");
    }

    @GetMapping("/{providerId}/services")
    public ResponseEntity<List<OfferDTO>> getFilteredServices( @PathVariable int providerId, @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String eventType,
        @RequestParam(required = false) Double price,
        @RequestParam(required = false) Boolean isAvailable){
        Provider provider = providerService.findById(providerId);
        if(provider == null) {return ResponseEntity.notFound().build();}
        List<Offer> filteredServices = offerService.getFilteredServices(provider, name, category, eventType, price, isAvailable);
        List<OfferDTO> dtos = filteredServices.stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }


    public Offer toOffer(OfferDTO dto) {
        if (dto == null) {return null;}
        if (dto.getType().equals("product")){
            return toProduct(dto);
        }else{
            return toService(dto);
        }
    }
    public Product toProduct(OfferDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setStatus((Status) Status.fromString(dto.getStatus()).orElse(Status.REJECTED));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setSale(dto.getSale());
        product.setPhotos(dto.getPhotos());
        product.setIsVisible(dto.getIsVisible());
        product.setIsAvailable(dto.getIsAvailable());
        product.setIsDeleted(dto.getIsDeleted());
        product.setLastChanged(dto.getLastChanged());
        product.setCategory(categoryService.findByName(dto.getCategory()));
        return product;
    }
    public Service toService(OfferDTO dto) {
        Service service = new Service();
        service.setId(dto.getId());
        service.setStatus((Status) Status.fromString(dto.getStatus()).orElse(Status.REJECTED));
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setSale(dto.getSale());
        service.setPhotos(dto.getPhotos());
        service.setIsVisible(dto.getIsVisible());
        service.setIsAvailable(dto.getIsAvailable());
        service.setIsDeleted(dto.getIsDeleted());
        service.setLastChanged(dto.getLastChanged());
        service.setCategory(categoryService.findByName(dto.getCategory()));
        service.setMaxDuration(dto.getMaxDuration());
        service.setMinDuration(dto.getMinDuration());
        service.setPreciseDuration(dto.getPreciseDuration());
        service.setSpecifics(dto.getSpecifics());
        service.setLatestReservation(dto.getLatestReservation());
        service.setLatestCancelation(dto.getLatestCancelation());
        return service;
    }
}

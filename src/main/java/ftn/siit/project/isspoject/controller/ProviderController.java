package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers/")
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
        if (provider == null) { throw new NotFoundException("Provider not found."); }
        List<OfferDTO> dtos = provider.getMyOffers().stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("offers/{providerId}/create")
    public ResponseEntity<String> createOffer(@PathVariable int providerId, @RequestBody OfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        if (provider == null) { throw new NotFoundException("Provider not found."); }
        Offer created = new Offer();
        created.toOffer(dto, categoryService.findByName(dto.getCategory()));
        provider.getMyOffers().add(created);
        providerService.update(provider);
        offerService.save(created);
        return ResponseEntity.ok("Offer created");
    }

    @PutMapping("offers/{providerId}/update")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable Integer providerId, @RequestBody OfferDTO dto) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {throw new NotFoundException("Provider not found."); }
        Offer oldOffer = offerService.findById(dto.getId());
        if(oldOffer == null) {throw new NotFoundException("Offer not found."); }
        Offer updated = offerService.update(oldOffer);
        provider.getMyOffers().remove(oldOffer);
        provider.getMyOffers().add(updated);
        providerService.update(provider);
        return ResponseEntity.ok().body(new OfferDTO(updated));
    }
    @DeleteMapping("offers/{providerId}/delete/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable int providerId, @PathVariable int offerId) {
        Provider provider = providerService.findById(providerId);
        if(provider == null) {throw new NotFoundException("Provider not found.");}
        Offer offer = offerService.findById(offerId);
        if(offer == null) {throw new NotFoundException("Offer not found.");}
        provider.getMyOffers().remove(offer);
        offerService.delete(offer);
        providerService.update(provider);
        return ResponseEntity.ok().body("Offer deleted");
    }

    @GetMapping("{providerId}/services")
    public ResponseEntity<List<OfferDTO>> getFilteredServices( @PathVariable int providerId, @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String eventType,
        @RequestParam(required = false) Double price,
        @RequestParam(required = false) Boolean isAvailable){
        Provider provider = providerService.findById(providerId);
        if(provider == null) {throw new NotFoundException("Provider not found."); }
        List<Offer> filteredServices = offerService.getFilteredServices(provider, name, category, eventType, price, isAvailable);
        List<OfferDTO> dtos = filteredServices.stream().map(OfferDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }
}

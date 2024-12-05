package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.implementations.OfferServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/offers/")
public class OfferController {

    @Autowired
    private OfferService offerService;
    @Autowired
    private OfferHistoryService offerHistoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OfferServiceImpl offerServiceImpl;

    @GetMapping()
    public ResponseEntity<List<OfferDTO>> getAll() {
        List<Offer> offers = offerService.findAll();

        List<OfferDTO> dtos = offers.stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

//    @GetMapping
//    public ResponseEntity<List<OfferDTO>> getOffersPage(Pageable page) {
//
//        Page<Offer> offers = offerService.findAll(page);
//
//        List<OfferDTO> offerDTOs = offers.stream()
//                .map(OfferDTO::new)
//                .toList();
//
//        return ResponseEntity.ok(offerDTOs);
//    }

//    @GetMapping(value = "/all_elements")
//    public ResponseEntity<PagedResponse<OfferDTO>> getOffersPageAllElements(Pageable page) {
//
//        Page<Offer> offersPage = offerService.findAll(page);
//
//        List<OfferDTO> offerDTOs = offersPage.stream()
//                .map(OfferDTO::new)
//                .toList();
//
//        PagedResponse<OfferDTO> response = new PagedResponse<>(
//                offerDTOs,
//                offersPage.getTotalPages(),
//                offersPage.getTotalElements()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    @GetMapping("top-five")
    public ResponseEntity<List<OfferDTO>> getTopFive() {
        List<Offer> offers = offerService.findTopFive();

        List<OfferDTO> dtos = offers.stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<OfferDTO> getOffer(@PathVariable int id) {
        Offer offer = offerService.findById(id);
        if (offer == null) {throw new NotFoundException("Offer not found");}
        return ResponseEntity.ok(new OfferDTO(offer));
    }

    @PutMapping("{id}")
    public ResponseEntity<PriceListOfferDTO> updatePrice(@PathVariable int id, @RequestBody NewPriceListOfferDTO dto) {
        Offer updatedOffer = offerService.updatePrice(dto);
        // add implementation of updating the offer in the list of its provider's offers
        offerHistoryService.add(offerService.findById(id));
        return ResponseEntity.ok(new PriceListOfferDTO(updatedOffer));
    }

    @GetMapping("{providerId}/price-list")
    public ResponseEntity<List<PriceListOfferDTO>> getPriceList(@PathVariable int providerId) {
        Provider provider = (Provider) userService.findById(providerId);
        if (provider == null) {throw new NotFoundException("Provider not found");}
        List<PriceListOfferDTO> prices = offerService.getPriceList(provider.getMyOffers());
        if (prices.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(prices);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Offer>> searchOffers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isService) {

        List<Offer> filteredItems = offerService.searchItems(name, description, minPrice, maxPrice, startDate, endDate, category, isService);
        return ResponseEntity.ok(filteredItems);
    }
}

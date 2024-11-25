package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.dto.OfferDTO;
import ftn.siit.project.isspoject.dto.PriceListOfferDTO;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;
    @Autowired
    private OfferHistoryService offerHistoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private OfferServiceImpl offerServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<OfferDTO>> getAll() {
        List<Offer> offers = offerService.findAll();
        if (offers.isEmpty()) {
            return ResponseEntity.noContent().build();  //error 204
        }

        List<OfferDTO> dtos = offers.stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/top-five")
    public ResponseEntity<List<OfferDTO>> getTopFive() {
        List<Offer> offers = offerService.findTopFive();
        if (offers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OfferDTO> dtos = offers.stream()
                .map(OfferDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/update-price")
    public ResponseEntity<PriceListOfferDTO> updatePrice(@RequestBody PriceListOfferDTO dto) {
        Offer updatedOffer = offerService.updatePrice(dto);
        // add implementation of updating the offer in the list of its provider's offers
        offerHistoryService.add(offerService.findById(dto.getId()));
        return ResponseEntity.ok(new PriceListOfferDTO(updatedOffer));
    }

    @GetMapping("/price-list/{providerId}")
    public ResponseEntity<List<PriceListOfferDTO>> getPriceList(@PathVariable Integer providerId) {
        Provider provider = (Provider) userService.findById(providerId);
        List<PriceListOfferDTO> prices = offerService.getPriceList(provider.getMyOffers());
        if (prices.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(prices);
    }
}

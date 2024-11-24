package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.dto.OfferDTO;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

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
}

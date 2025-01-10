package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.offer.NewPriceListOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.PriceListOfferDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.implementations.OfferServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferHistoryService;
import ftn.siit.project.isspoject.service.interfaces.OfferService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/offers/")
@CrossOrigin(origins = "http://localhost:4200")
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
    @Autowired
    private TokenUtils tokenUtils;
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

    @GetMapping(value = "/all-elements")
    public ResponseEntity<PagedResponse<OfferDTO>> getOffersPageAllElements(Pageable page) {

        Page<Offer> offersPage = offerService.findAll(page);

        List<OfferDTO> offerDTOs = offersPage.stream()
                .map(OfferDTO::new)
                .toList();

        PagedResponse<OfferDTO> response = new PagedResponse<>(
                offerDTOs,
                offersPage.getTotalPages(),
                offersPage.getTotalElements()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


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
        Offer updatedOffer = offerService.updatePrice(id, dto);
        // add implementation of updating the offer in the list of its provider's offers
        offerHistoryService.add(updatedOffer.getId(), updatedOffer);
        return ResponseEntity.ok(new PriceListOfferDTO(updatedOffer));
    }

    @GetMapping("{providerId}/price-list")
    public ResponseEntity<List<PriceListOfferDTO>> getPriceList(@PathVariable int providerId) {
        Provider provider = (Provider) userService.findById(providerId);
        if (provider == null) {throw new NotFoundException("Provider not found");}
        List<PriceListOfferDTO> prices = offerService.getPriceList(provider);
        if (prices.isEmpty()) {return ResponseEntity.noContent().build();}
        return ResponseEntity.ok(prices);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<OfferDTO>> searchOffers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isOnSale,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Boolean isService,
            @RequestParam(required = false) Boolean isProduct,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<OfferDTO> filteredOffers = offerService.searchOffers(name, description, maxPrice, isOnSale, startDate, endDate, category, eventType, isService, isProduct, pageable);

        if (filteredOffers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        System.out.println("Filtered Offers: " + filteredOffers);

        return ResponseEntity.ok(filteredOffers);
    }


    @PostMapping("{offerId}/favorite")
    public ResponseEntity<UserDTO> addOfferToFavorites(@PathVariable Integer offerId, HttpServletRequest request) {
        // Проверка существования пользователя
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Проверка существования события
        Offer offer = offerService.findById(offerId);

        // Проверка, что событие уже добавлено в избранное
        if (user.getFavouriteOffers().contains(offer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Событие уже в избранном
        }

        // Добавление события в избранное
        user.getFavouriteOffers().add(offer);
        User updatedUser = userService.save(user);

        // Преобразование в DTO
        UserDTO updatedUserDTO = new UserDTO(updatedUser);

        return ResponseEntity.ok(updatedUserDTO); // Возвращаем обновлённого пользователя
    }
    @DeleteMapping("{offerId}/favorite")
    public ResponseEntity<UserDTO> removeEOfferFromFavorites(@PathVariable Integer offerId, HttpServletRequest request) {
        // Проверка существования пользователя
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Проверка существования события
        Offer offer = offerService.findById(offerId);

        // Проверка, что событие уже добавлено в избранное
        if (user.getFavouriteOffers().contains(offer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Событие уже в избранном
        }

        // Добавление события в избранное
        user.getFavouriteOffers().remove(offer);
        User updatedUser = userService.save(user);

        // Преобразование в DTO
        UserDTO updatedUserDTO = new UserDTO(updatedUser);

        return ResponseEntity.ok(updatedUserDTO); // Возвращаем обновлённого пользователя
    }

    @GetMapping("favorites")
    public ResponseEntity<List<OfferDTO>> getFavorites(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Offer> events = user.getFavouriteOffers();

        List<OfferDTO> od = new ArrayList<>();

        for (Offer o: events){
            od.add(new OfferDTO(o));
        }

        return ResponseEntity.ok(od);

    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categoryNames = categoryService.findAllNames();
        if (categoryNames.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        System.out.println("Categories: " + categoryNames);
        return ResponseEntity.ok(categoryNames);
    }
}

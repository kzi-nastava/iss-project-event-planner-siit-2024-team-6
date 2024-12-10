package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.OfferService;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ServiceRepository;
import ftn.siit.project.isspoject.service.interfaces.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<OfferService> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable) {
        return serviceRepository.findFilteredServices(p.getId(), name, category, eventType, price, isAvailable);
    }

    @Override
    public OfferService findById(Integer id) {
        OfferService s = serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service with id " + id + " not found"));
        if(s.getIsDeleted()){
            throw new NotFoundException("Service with id " + id + " not found");
        }
        return s;
    }

    @Override
    public OfferService save(OfferService offerService) {
        return serviceRepository.save(offerService);
    }

    @Override
    public OfferService update(OfferService offerService) {
        OfferService existingOfferService = this.findById(offerService.getId());
        existingOfferService.setName(offerService.getName());
        existingOfferService.setCategory(offerService.getCategory());
        existingOfferService.setDescription(offerService.getDescription());
        existingOfferService.setPrice(offerService.getPrice());
        existingOfferService.setIsAvailable(offerService.getIsAvailable());
        existingOfferService.setSpecifics(offerService.getSpecifics());
        existingOfferService.setSale(offerService.getSale());
        existingOfferService.setPhotos(offerService.getPhotos());
        existingOfferService.setIsDeleted(false);
        existingOfferService.setProvider(offerService.getProvider());
        existingOfferService.setIsVisible(offerService.getIsVisible());
        existingOfferService.setPreciseDuration(offerService.getPreciseDuration());
        existingOfferService.setMaxDuration(offerService.getMaxDuration());
        existingOfferService.setMinDuration(offerService.getMinDuration());
        existingOfferService.setLatestCancelation(offerService.getLatestCancelation());
        existingOfferService.setLatestReservation(offerService.getLatestReservation());
        existingOfferService.setLastChanged(LocalDateTime.now());
        return serviceRepository.save(existingOfferService);
    }

    @Override
    public void delete(OfferService s) {
        s.setIsDeleted(true);
        serviceRepository.save(s);
    }
}

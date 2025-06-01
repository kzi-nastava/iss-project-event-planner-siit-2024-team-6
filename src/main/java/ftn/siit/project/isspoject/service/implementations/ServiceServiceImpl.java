package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ServiceRepository;
import ftn.siit.project.isspoject.service.interfaces.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Make sure this is the correct import

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<Service> findByProvider(Provider provider) {
        List<Service> s = serviceRepository.findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNullOrderById(provider.getId());
        if (s.isEmpty()) {
            throw new NotFoundException("Provider has no services");
        }
        return s;
    }

    @Override
    public Page<Service> findByProvider(Provider provider, Pageable pageable) {
        return serviceRepository.findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNullOrderByIdAsc(provider.getId(), pageable);
    }

    @Override
    public List<Service> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable) {
        List<Service> s = serviceRepository.findFilteredServices(p.getId(), name, category, eventType, price, isAvailable);
        if (s.isEmpty()) {
            throw new NotFoundException("No service are found under given filters");
        }
        return s;
    }

    @Override
    public Page<Service> getFilteredServices(Provider p, List<String> category, List<String> eventType, Double price, Boolean isAvailable, Pageable page) {
        // Log the input parameters for debugging purposes
        System.out.println("Provider ID: " + p.getId());
        System.out.println("Categories: " + category);
        System.out.println("Event Types: " + eventType);
        System.out.println("Price: " + price);
        System.out.println("Is Available: " + isAvailable);
        System.out.println("Page Request: " + page);
        // Call the repository method and handle potential exceptions
        Page<Service> services = serviceRepository.findServicesByFilters(p.getId(), category != null ? category.toArray(new String[0]) : null, eventType != null ? eventType.toArray(new String[0]) : null, isAvailable, price, page);

        // Check if the result is empty and throw a custom exception
        if (services.isEmpty()) {
            throw new NotFoundException("No services are found under the given filters");
        }

        return services;
    }


    @Override
    public Page<Service> searchByName(Provider p, String name, Pageable pageable) {
        Page<Service> s = serviceRepository.searchByName(p.getId(), name, pageable);
        if (s.isEmpty()) {
            throw new NotFoundException("No service are found under given filters");
        }
        return s;
    }

    @Override
    public Service findById(Integer id) {
        Service s = serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service with id " + id + " not found"));
        if (s.getIsDeleted()) {
            throw new NotFoundException("Service with id " + id + " not found");
        }
        return s;
    }

    @Override
    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public Service save(NewOfferDTO dto, Provider p, List<EventType> eventTypes, Category c, Status s) {
        Service existingService = new Service();
        existingService.setName(dto.getName());
        if (c != null) {
            existingService.setCategory(c);
        }
        existingService.setStatus(s);
        existingService.setDescription(dto.getDescription());
        existingService.setPrice(dto.getPrice());
        existingService.setIsAvailable(dto.getIsAvailable());
        existingService.setSpecifics(dto.getSpecifics());
        existingService.setSale(dto.getSale());
        existingService.setPhotos(dto.getPhotos());
        existingService.setProvider(p);
        existingService.setIsDeleted(false);
        existingService.setIsVisible(dto.getIsVisible());
        existingService.setPreciseDuration(dto.getPreciseDuration());
        existingService.setMaxDuration(dto.getMaxDuration());
        existingService.setMinDuration(dto.getMinDuration());
        existingService.setEventTypes(eventTypes);
        existingService.setLatestCancelation(dto.getLatestCancelation());
        existingService.setLatestReservation(dto.getLatestReservation());
        existingService.setLastChanged(LocalDateTime.now());
        existingService.setIsReservationAutoApproved(dto.isReservationAutoApproved());
        return serviceRepository.save(existingService);
    }

    @Override
    public Service update(Service service) {
        if (service == null || service.getId() == null) {
            throw new NotFoundException("Service with id " + service.getId() + " not found and cannot be updated");
        }
        Service existingService = this.findById(service.getId());
        existingService.setStatus(service.getStatus());
        existingService.setName(service.getName());
        existingService.setDescription(service.getDescription());
        existingService.setPrice(service.getPrice());
        existingService.setIsAvailable(service.getIsAvailable());
        existingService.setSpecifics(service.getSpecifics());
        existingService.setSale(service.getSale());
        existingService.setPhotos(service.getPhotos());
        existingService.setIsDeleted(false);
        existingService.setEventTypes(service.getEventTypes());
        existingService.setProvider(service.getProvider());
        existingService.setIsVisible(service.getIsVisible());
        existingService.setPreciseDuration(service.getPreciseDuration());
        existingService.setMaxDuration(service.getMaxDuration());
        existingService.setMinDuration(service.getMinDuration());
        existingService.setLatestCancelation(service.getLatestCancelation());
        existingService.setLatestReservation(service.getLatestReservation());
        existingService.setLastChanged(LocalDateTime.now());
        existingService.setIsReservationAutoApproved(service.getIsReservationAutoApproved());
        return serviceRepository.save(existingService);
    }

    @Override
    public Service update(int id, NewOfferDTO dto, List<EventType> eventTypes) {
        Service existingService = this.findById(id);
        existingService.setName(dto.getName());
        existingService.setDescription(dto.getDescription());
        existingService.setPrice(dto.getPrice());
        existingService.setIsAvailable(dto.getIsAvailable());
        existingService.setSpecifics(dto.getSpecifics());
        existingService.setSale(dto.getSale());
        existingService.setPhotos(dto.getPhotos());
        existingService.setIsDeleted(false);
        existingService.setIsVisible(dto.getIsVisible());
        existingService.setPreciseDuration(dto.getPreciseDuration());
        existingService.setEventTypes(eventTypes);
        existingService.setMaxDuration(dto.getMaxDuration());
        existingService.setMinDuration(dto.getMinDuration());
        existingService.setLatestCancelation(dto.getLatestCancelation());
        existingService.setLatestReservation(dto.getLatestReservation());
        existingService.setLastChanged(LocalDateTime.now());
        existingService.setIsReservationAutoApproved(dto.isReservationAutoApproved());
        return serviceRepository.save(existingService);
    }

    @Override
    public Service update(int id, Category c, Status st) {
        Service s = findById(id);
        s.setCategory(c);
        s.setStatus(st);
        return serviceRepository.save(s);
    }

    @Override
    public void delete(Service s) {
        findById(s.getId());
        s.setIsDeleted(true);
        serviceRepository.save(s);
    }
}

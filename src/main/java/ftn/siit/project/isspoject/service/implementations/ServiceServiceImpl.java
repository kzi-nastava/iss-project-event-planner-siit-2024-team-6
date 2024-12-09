package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Service;
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
    public List<Service> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable) {
        return serviceRepository.findFilteredServices(p.getId(), name, category, eventType, price, isAvailable);
    }

    @Override
    public Service findById(Integer id) {
        Service s = serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service with id " + id + " not found"));
        if(s.getIsDeleted()){
            throw new NotFoundException("Service with id " + id + " not found");
        }
        return s;
    }

    @Override
    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public Service update(Service service) {
        Service existingService = this.findById(service.getId());
        existingService.setName(service.getName());
        existingService.setCategory(service.getCategory());
        existingService.setDescription(service.getDescription());
        existingService.setPrice(service.getPrice());
        existingService.setIsAvailable(service.getIsAvailable());
        existingService.setSpecifics(service.getSpecifics());
        existingService.setSale(service.getSale());
        existingService.setPhotos(service.getPhotos());
        existingService.setIsDeleted(false);
        existingService.setProvider(service.getProvider());
        existingService.setIsVisible(service.getIsVisible());
        existingService.setPreciseDuration(service.getPreciseDuration());
        existingService.setMaxDuration(service.getMaxDuration());
        existingService.setMinDuration(service.getMinDuration());
        existingService.setLatestCancelation(service.getLatestCancelation());
        existingService.setLatestReservation(service.getLatestReservation());
        existingService.setLastChanged(LocalDateTime.now());
        return serviceRepository.save(existingService);
    }

    @Override
    public void delete(Service s) {
        s.setIsDeleted(true);
        serviceRepository.save(s);
    }
}

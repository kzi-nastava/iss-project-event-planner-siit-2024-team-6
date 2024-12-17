package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Make sure this is the correct import

import java.util.List;

public interface ServiceService {
    List<Service> findByProvider(Provider provider);
    Page<Service> findByProvider(Provider provider, Pageable pageable);
    List<Service> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable);
    Page<Service> getFilteredServices(Provider p, List<String> category, List<String> eventType, Double price, Boolean isAvailable, Pageable page);
    Page<Service> searchByName(Provider p, String name, Pageable pageable);
    Service findById(Integer id);
    Service save(Service service);
    Service save(NewOfferDTO offerDTO, Provider p, List<EventType> eventTypes, Category c);
    Service update(Service service);
    Service update(int id, NewOfferDTO dto, List<EventType> eventTypes);
    void delete(Service service);
}

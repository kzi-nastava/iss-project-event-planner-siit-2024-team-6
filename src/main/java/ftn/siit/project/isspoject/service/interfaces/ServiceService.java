package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
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
    Service findById(Integer id);
    Service save(Service service);
    Service save(NewOfferDTO offerDTO, Provider p);
    Service update(Service service);
    Service update(int id, NewOfferDTO dto);
    void delete(Service service);
}

package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Service;

import java.util.List;

public interface ServiceService {
    List<Service> getFilteredServices(Provider p, String name, String category, String eventType, Double price, Boolean isAvailable);
    Service findById(Integer id);
    Service save(Service service);
    Service update(Service service);
    void delete(Service service);
}

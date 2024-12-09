package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.repository.ProviderRepository;
import ftn.siit.project.isspoject.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public Provider findById(Integer id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider with ID " + id + " not found"));
    }

    @Override
    public Provider update(Provider provider) {
        if (!providerRepository.existsById(provider.getId())) {
            throw new IllegalArgumentException("Provider with ID " + provider.getId() + " does not exist");
        }
        return providerRepository.save(provider);
    }
}

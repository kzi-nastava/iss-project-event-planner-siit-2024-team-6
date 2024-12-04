package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Provider;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderDTO extends UserDTO{
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String[] companyPhotos;
    private String openingTime;
    private String closingTime;

    public ProviderDTO(Provider savedUser) {
    }
}

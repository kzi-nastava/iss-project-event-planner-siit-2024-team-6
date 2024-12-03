package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
}

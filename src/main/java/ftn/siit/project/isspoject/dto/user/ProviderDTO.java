package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Provider;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderDTO extends UserDTO {
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String[] companyPhotos;
    private String openingTime;
    private String closingTime;

    public ProviderDTO() {
        super();
    }

    public ProviderDTO(Provider savedUser) {
        super(savedUser);
        if (savedUser != null) {
            this.companyEmail = savedUser.getCompanyEmail();
            this.companyName = savedUser.getCompanyName();
            this.companyAddress = savedUser.getCompanyAddress();
            this.description = savedUser.getDescription();

            List<String> photos = savedUser.getCompanyPhotos();
            this.companyPhotos = (photos != null) ? photos.toArray(new String[0]) : null;

            this.openingTime = savedUser.getOpeningTime();
            this.closingTime = savedUser.getClosingTime();
        }
    }
}

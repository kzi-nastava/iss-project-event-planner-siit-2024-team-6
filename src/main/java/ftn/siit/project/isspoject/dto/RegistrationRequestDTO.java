package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String photoUrl;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String companyPhoto;
    private String openingTime;
    private String closingTime;

    private String role;
}

package ftn.siit.project.isspoject.dto.user;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String email;
    private String oldPassword;
    private String newPasswordFirst;
    private String newPasswordSecond;
}

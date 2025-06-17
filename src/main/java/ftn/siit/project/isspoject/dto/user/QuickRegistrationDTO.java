package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

@Data
public class QuickRegistrationDTO {
    private String email;
    private String password;
    private String name;
    private String lastname;
    private Integer eventId;
    public User toUser(){
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setLastname(lastname);
        user.setSuspendedSince(null);
        return user;
    }
}

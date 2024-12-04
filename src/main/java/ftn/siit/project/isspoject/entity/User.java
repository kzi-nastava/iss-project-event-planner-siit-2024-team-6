package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String photoUrl;

    @Column(nullable = false)
    private Boolean isActive;

    private LocalDateTime suspendedSince;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    private String address;

    private String phoneNumber;
}


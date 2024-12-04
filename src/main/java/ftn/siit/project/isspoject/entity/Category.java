package ftn.siit.project.isspoject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    public Category() {}
    public Category(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}


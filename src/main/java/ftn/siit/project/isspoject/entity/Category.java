package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class Category {
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


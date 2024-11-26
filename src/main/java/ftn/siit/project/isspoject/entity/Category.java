package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class Category {
    private Integer id;
    private String name;
    private String description;
    private List<Offer> offers;
}


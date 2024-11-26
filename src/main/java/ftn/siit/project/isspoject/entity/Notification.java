package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Notification {
    private Integer id;
    private String text;
    private User receiver;
}

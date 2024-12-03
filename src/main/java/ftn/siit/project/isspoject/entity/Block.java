package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Block {
    private Integer id;
    private User blockerId;
    private User blockedId;
}

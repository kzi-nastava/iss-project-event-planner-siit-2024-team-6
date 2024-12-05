package ftn.siit.project.isspoject.entity;

import lombok.Data;

import jakarta.persistence.*;
@Data
@Entity
@Table(name = "block")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blockerId;

    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blockedId;
}

package ftn.siit.project.isspoject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "chats")
public class Chat {
    @Id @GeneratedValue
    private int id;

    @ManyToOne
    private User participant1;

    @ManyToOne
    private User participant2;

    private Instant lastUpdated;

    private boolean isBlocked;

    public Chat(){}
    public Chat(User participant1, User participant2, Instant lastUpdated, boolean isBlocked) {
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.lastUpdated = lastUpdated;
        this.isBlocked = isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

}

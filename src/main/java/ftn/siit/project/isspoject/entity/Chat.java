package ftn.siit.project.isspoject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")  // foreign key in messages table
    @OrderBy("time ASC") //
    private List<Message> messages = new ArrayList<>();

    public Chat(){}
    public Chat(User participant1, User participant2, Instant lastUpdated, boolean isBlocked) {
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.lastUpdated = lastUpdated;
        this.isBlocked = isBlocked;
    }


    // Getters
    public int getId() {
        return id;
    }

    public User getParticipant1() {
        return participant1;
    }

    public User getParticipant2() {
        return participant2;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setParticipant1(User participant1) {
        this.participant1 = participant1;
    }

    public void setParticipant2(User participant2) {
        this.participant2 = participant2;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}

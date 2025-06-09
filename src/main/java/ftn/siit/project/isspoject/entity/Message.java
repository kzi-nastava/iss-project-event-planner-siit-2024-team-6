package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.message.MessageDTO;
import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private LocalDateTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    public Message(){}
    public Message (MessageDTO dto, User sender, User receiver) {
        this.id = dto.getId();
        this.text = dto.getText();
        this.time = dto.getTime();
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(NewMessageDTO dto, User sender, User receiver) {
        this.text = dto.getText();
        this.time = LocalDateTime.now();
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(Integer integer, String s, LocalDateTime localDateTime, int i, int j) {
        this.id = integer;
        this.text = s;
        this.time = localDateTime;
//        this.sender = i;
//        this.receiver = j;
    }
}

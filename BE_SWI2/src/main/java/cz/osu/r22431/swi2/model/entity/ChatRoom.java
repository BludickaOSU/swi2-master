package cz.osu.r22431.swi2.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "chat_id", nullable = false)
    private Integer chatId;

    @ManyToMany(mappedBy = "joinedRooms")
    private List<ChatUser> joinedUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "chat_name")
    private String chatName;

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatRoom(this);
    }
}

package cz.osu.r22431.swi2.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "user_id")
    private UUID userId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "chat_member",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    List<ChatRoom> joinedRooms = new ArrayList<>();

    @OneToMany(mappedBy = "chatUser")
    private List<Message> messages = new ArrayList<>();

    public void addRoom(ChatRoom chatRoom) {
        this.joinedRooms.add(chatRoom);
        chatRoom.getJoinedUsers().add(this);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatUser(this);
    }
}

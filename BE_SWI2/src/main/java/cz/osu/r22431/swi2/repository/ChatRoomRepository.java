package cz.osu.r22431.swi2.repository;

import cz.osu.r22431.swi2.model.entity.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Integer> {
    List<ChatRoom> findByJoinedUsers_UserId(UUID userId);
    List<ChatRoom> findByJoinedUsers_Username(String username);
    boolean existsByChatNameIgnoreCase(String chatName);
    ChatRoom findByChatNameIgnoreCase(String chatName);
    ChatRoom findByChatName(String chatName);

}

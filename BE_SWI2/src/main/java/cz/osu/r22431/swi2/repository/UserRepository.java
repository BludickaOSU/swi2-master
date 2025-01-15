package cz.osu.r22431.swi2.repository;

import cz.osu.r22431.swi2.model.entity.ChatUser;
import org.springframework.data.repository.CrudRepository;


import java.util.UUID;

public interface UserRepository extends CrudRepository<ChatUser, UUID> {
    ChatUser findChatUserByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    ChatUser findByUsername(String username);

}
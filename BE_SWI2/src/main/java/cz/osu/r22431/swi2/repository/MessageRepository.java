package cz.osu.r22431.swi2.repository;

import cz.osu.r22431.swi2.model.entity.ChatRoom;
import cz.osu.r22431.swi2.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {

    Page<Message> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);
}

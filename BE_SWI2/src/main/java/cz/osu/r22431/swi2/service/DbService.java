package cz.osu.r22431.swi2.service;

import cz.osu.r22431.swi2.repository.ChatRoomRepository;
import cz.osu.r22431.swi2.repository.UserRepository;
import cz.osu.r22431.swi2.model.entity.ChatRoom;
import cz.osu.r22431.swi2.model.entity.ChatUser;
import cz.osu.r22431.swi2.model.entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DbService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;


    public ResponseEntity<List<ChatUser>> getUsers() {
        List<ChatUser> retList = (List<ChatUser>) userRepository.findAll();
        retList.forEach(chatUser -> chatUser.setJoinedRooms(null));
        retList.forEach(chatUser -> chatUser.setMessages(null));
        return new ResponseEntity<>(retList, HttpStatus.OK);
    }

    public ResponseEntity<List<ChatRoom>> getChatRooms(UUID userId) {
        List<ChatRoom> retList = chatRoomRepository.findByJoinedUsers_UserId(userId);
        retList.forEach(chatRoom -> chatRoom.setJoinedUsers(null));
        for (ChatRoom chatRoom : retList) {
            for (Message message : chatRoom.getMessages()) {
                message.setChatRoom(null);
                message.getChatUser().setMessages(null);
                message.getChatUser().setJoinedRooms(null);
            }
        }
        return new ResponseEntity<>(retList, HttpStatus.OK);
    }


}

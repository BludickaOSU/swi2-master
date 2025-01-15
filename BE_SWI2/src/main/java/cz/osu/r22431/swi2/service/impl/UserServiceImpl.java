package cz.osu.r22431.swi2.service.impl;

import cz.osu.r22431.swi2.Component.ChatMessageListener;
import cz.osu.r22431.swi2.config.RabbitMQConfig;
import cz.osu.r22431.swi2.repository.ChatRoomRepository;
import cz.osu.r22431.swi2.repository.MessageRepository;
import cz.osu.r22431.swi2.repository.UserRepository;
import cz.osu.r22431.swi2.model.entity.ChatUser;
import cz.osu.r22431.swi2.model.dto.user.UserLoginDTO;
import cz.osu.r22431.swi2.model.dto.user.UserRegisterDTO;
import cz.osu.r22431.swi2.model.dto.user.UserToken;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepo;
    private final ChatRoomRepository chatRoomRepo;
    private final MessageRepository messageRepo;
    private final RabbitMQConfig rabbitMQConfig;
    private final ChatMessageListener chatMessageListener;
    private final RabbitAdmin rabbitAdmin;

    public ResponseEntity<String> signup(UserRegisterDTO userDTO) {
        ResponseEntity<String> ret;

        if (userRepo.existsByUsernameIgnoreCase(userDTO.getUsername())) {
            ret = new ResponseEntity<>("Username already taken!",
                    HttpStatus.CONFLICT);
        } else {
            ChatUser user = new ChatUser();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.addRoom(chatRoomRepo.findByChatNameIgnoreCase("Public"));

            ChatUser newUser = userRepo.save(user);

            Queue userQueue = rabbitMQConfig.createUserQueue(newUser.getUserId().toString());
            rabbitAdmin.declareQueue(userQueue);
            rabbitAdmin.declareBinding(rabbitMQConfig.bindUserQueue(userQueue, rabbitMQConfig.chatroomExchange()));

            //chatMessageListener.startListeningForUser(userQueue.getName());

            ret = new ResponseEntity<>("User registered!", HttpStatus.OK);
        }
        return ret;
    }

    public ResponseEntity<Object> authenticate(UserLoginDTO userCredentials) {
        ResponseEntity<Object> ret;

        ChatUser user = userRepo.findChatUserByUsernameIgnoreCase(
                userCredentials.getUsername());
        if (user != null) {
            if (user.getPassword().equals(userCredentials.getPassword())) {
                UserToken userToken = new UserToken(user.getUserId(),
                        user.getUsername());
                ret = new ResponseEntity<>(userToken, HttpStatus.OK);
            } else {
                ret = new ResponseEntity<>("Password is incorrect!",
                        HttpStatus.CONFLICT);
            }
        } else {
            ret = new ResponseEntity<>("Username doesn't exist!",
                    HttpStatus.CONFLICT);
        }
        return ret;
    }
}

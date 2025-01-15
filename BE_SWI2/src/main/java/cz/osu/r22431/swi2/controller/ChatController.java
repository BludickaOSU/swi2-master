package cz.osu.r22431.swi2.controller;

import cz.osu.r22431.swi2.model.dto.message.MessageCreateDTO;
import cz.osu.r22431.swi2.model.dto.message.MessageGetDTO;
import cz.osu.r22431.swi2.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/message") // url: /app/message
    public MessageCreateDTO receivePublicMessage(@Payload MessageCreateDTO message) {
        chatMessageService.broadcastMessage(message);
        chatMessageService.saveMessage(message);

        String destination = "/chatroom/" + message.getReceiverChatRoomId();
        messagingTemplate.convertAndSend(destination, message);
        return message;
    }

    @MessageMapping("/private-message")
    public MessageCreateDTO receivePrivateMessage(@Payload MessageCreateDTO message) {
        messagingTemplate.convertAndSendToUser(message.getChatUserId(), "/private", message);
        return message;
    }

    @GetMapping("/messages/{chatId}")
    public Page<MessageGetDTO> getMessages(@PathVariable Integer chatId,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sendTime").descending());
        return chatMessageService.getMessagesForChatRoom(chatId, pageable);
    }


}

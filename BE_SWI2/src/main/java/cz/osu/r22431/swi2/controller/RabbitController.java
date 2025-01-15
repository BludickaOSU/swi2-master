package cz.osu.r22431.swi2.controller;

import cz.osu.r22431.swi2.model.dto.message.MessageCreateDTO;
import cz.osu.r22431.swi2.model.dto.message.MessageGetDTO;
import cz.osu.r22431.swi2.service.ChatMessageService;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin
public class RabbitController {
    @Autowired
    private RabbitAdmin admin;
    @Autowired
    private RabbitTemplate template;


    @GetMapping(value = "/api/queue")
    public List<MessageGetDTO> getMessages(@RequestParam String userId) {
        List<MessageGetDTO> receivedMessages = new ArrayList<>();
        while (Objects.requireNonNull(admin.getQueueInfo("chatroom.queue." + userId))
                .getMessageCount() != 0) {
            MessageCreateDTO messageCreateDTO = (MessageCreateDTO) template
                    .receiveAndConvert("chatroom.queue." + userId);
            assert messageCreateDTO != null;
            System.out.println("Received: " + messageCreateDTO.getContent());

            MessageGetDTO messageGetDTO = convertToMessageGetDTO(messageCreateDTO);
            receivedMessages.add(messageGetDTO);
        }

        return receivedMessages;
    }

    private MessageGetDTO convertToMessageGetDTO(MessageCreateDTO messageCreateDTO) {
        MessageGetDTO messageGetDTO = new MessageGetDTO();
        messageGetDTO.setContent(messageCreateDTO.getContent());
        messageGetDTO.setChatUserId(messageCreateDTO.getChatUserId());
        messageGetDTO.setChatRoomId(messageCreateDTO.getReceiverChatRoomId());
        messageGetDTO.setSendTime(messageCreateDTO.getSendTime());
        messageGetDTO.setSenderName(messageCreateDTO.getSenderName());
        return messageGetDTO;
    }
}

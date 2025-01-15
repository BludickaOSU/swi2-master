// ChatMessageService.java
package cz.osu.r22431.swi2.service;

import cz.osu.r22431.swi2.config.RabbitMQConfig;
import cz.osu.r22431.swi2.model.dto.message.MessageCreateDTO;
import cz.osu.r22431.swi2.model.dto.message.MessageGetDTO;
import cz.osu.r22431.swi2.model.entity.ChatRoom;
import cz.osu.r22431.swi2.model.entity.ChatUser;
import cz.osu.r22431.swi2.model.entity.Message;
import cz.osu.r22431.swi2.repository.ChatRoomRepository;
import cz.osu.r22431.swi2.repository.MessageRepository;
import cz.osu.r22431.swi2.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;

    public ChatMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void broadcastMessage(MessageCreateDTO message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHATROOM_EXCHANGE, "", message);
    }

    public void saveMessage(MessageCreateDTO messageDTO) {
        if (messageDTO.getContent() == null || messageDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }
        if (messageDTO.getSendTime() == null || messageDTO.getSendTime().isEmpty()) {
            throw new IllegalArgumentException("Send time cannot be null or empty");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getReceiverChatRoomId()).orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        ChatUser sender = userRepository.findByUsername(messageDTO.getSenderName());
        Message message = new Message();

        message.setContent(messageDTO.getContent());
        message.setChatRoom(chatRoom);
        message.setChatUser(sender);


        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            message.setSendTime(dateFormat.parse(messageDTO.getSendTime()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format for sendTime", e);
        }

        messageRepository.save(message);

    }

    public Page<MessageGetDTO> getMessagesForChatRoom(Integer chatId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        Page<Message> messages = messageRepository.findAllByChatRoom(chatRoom, pageable);
        return messages.map(this::mapToMessageGetDTO);
    }

    private MessageGetDTO mapToMessageGetDTO(Message message) {
        MessageGetDTO dto = new MessageGetDTO();
        dto.setContent(message.getContent());
        dto.setSendTime(message.getSendTime().toString());
        dto.setSenderName(message.getChatUser().getUsername());
        dto.setChatUserId(message.getChatUser().getUserId().toString());
        dto.setChatRoomId(message.getChatRoom().getChatId());
        return dto;
    }
}
package cz.osu.r22431.swi2.model.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MessageCreateDTO implements Serializable {
    private String senderName;
    private Integer receiverChatRoomId;
    private String content;
    private String chatUserId;
    private String sendTime;

}

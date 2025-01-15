package cz.osu.r22431.swi2.model.dto.message;

import lombok.Data;

@Data
public class MessageGetDTO {
    private Integer chatRoomId;
    private String chatUserId;
    private String senderName;
    private String content;
    private String sendTime;
}

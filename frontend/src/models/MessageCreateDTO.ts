export interface MessageCreateDTO {
    senderName: string;
    receiverChatRoomId: number;
    content: string;
    chatUserId: string;
    sendTime: string;
}
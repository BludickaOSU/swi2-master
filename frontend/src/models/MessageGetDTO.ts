export interface MessageGetDTO {
    chatRoomId: number;
    chatUserId: string | null;
    senderName: string | null;
    content: string;
    sendTime: string;
    duplicate: boolean;
}
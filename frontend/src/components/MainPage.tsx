import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Button, TextField } from '@mui/material';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { over } from 'stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { Navigation } from './Navigation';
import { Sidebar } from './Sidebar';
import '../css/MainPage.css';
import {MessageGetDTO} from "../models/MessageGetDTO";

var stompClient: any = null;
const PICKUP_URL = "http://localhost:8080/api/queue";
const MESSAGES_DB_URL = "http://localhost:8080/messages";

const MainPage = (props: any) => {
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState<MessageGetDTO[]>([]);
  const [newMessageIds, setNewMessageIds] = useState<Set<string>>(new Set());
  const [page, setPage] = useState(0);
  const listRef = useRef<HTMLUListElement>(null);
  const topElementRef = useRef<HTMLDivElement>(null);

  /**
   * Připojeni k Websocketu a získání zpráv z DB
   */
  useEffect(() => {
    const sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(sock);
    stompClient.connect({}, onConnected, onError);
    fetchMessagesFromDb(1, 0, 10);
    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, []);

  /**
   * Scrolluj dolů při přidání nové zprávy
   */
  useEffect(() => {
    if (listRef.current) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, [messages]);
  /**
   * Intersection observer pro načtení starších zpráv
   */
  useEffect(() => {
    const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            fetchOlderMessages();
          }
        },
        { threshold: 1.0 }
    );

    if (topElementRef.current) {
      observer.observe(topElementRef.current);
    }
    return () => {
      if (topElementRef.current) {
        observer.unobserve(topElementRef.current);
      }
    };
  }, [topElementRef.current]);

  const fetchOlderMessages = useCallback(() => {
    fetchMessagesFromDb(1, page + 1, 10);
  }, [page]);

  function onConnected() {
    stompClient.subscribe("/chatroom/1", onPublicMessageReceived);
  }

  function onError(e: any) {
    console.log("WebSocket connection error: " + e);
  }

  function onPublicMessageReceived(payload: any) {
    const receivedMessage: MessageGetDTO = JSON.parse(payload.body);
    const newMessage: MessageGetDTO = {
      ...receivedMessage,
      chatRoomId: 1,
      senderName: receivedMessage.senderName || props.user.username,
      duplicate: false,
    };
    pickupAndCompareMessages();
    setMessages((prevMessages) => mergeMessages([newMessage], prevMessages));
    if (newMessage.senderName === null) {
      setNewMessageIds((prevIds) => new Set(prevIds).add(newMessage.sendTime));
      setTimeout(() => {
        setNewMessageIds((prevIds) => {
          const newIds = new Set(prevIds);
          newIds.delete(newMessage.sendTime);
          return newIds;
        });
      }, 2000);
    }
  }

  function sendMessage() {
    if (!message.trim()) {
      console.error("Message content cannot be empty");
      return;
    }
    if (stompClient && stompClient.connected) {
      const chatMessage = {
        senderName: props.user.username,
        receiverChatRoomId: 1,
        content: message,
        chatUserId: props.user.username,
        sendTime: new Date().toISOString().split('.')[0] + 'Z',
      };
      stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
      setMessage("");
      pickupAndCompareMessages();
    }
  }

  async function pickupAndCompareMessages() {
    try {
      const [rabbitResponse, dbResponse] = await Promise.all([
        axios.get(PICKUP_URL, { params: { userId: props.user.userId } }),
        axios.get(`${MESSAGES_DB_URL}/1?page=${page}&size=10`),
      ]);
      const rabbitMessages: MessageGetDTO[] = rabbitResponse.data.map((msg: MessageGetDTO) => ({
        ...msg,
        duplicate: false,
      }));
      const dbMessages: MessageGetDTO[] = dbResponse.data.content.map((msg: MessageGetDTO) => ({
        ...msg,
        duplicate: false, // Inicializuj jako ne-duplicitní
      }));
// Označ duplicitní zprávy z databáze
      dbMessages.forEach(dbMsg => {
        if (rabbitMessages.some(rabbitMsg =>
            rabbitMsg.senderName === dbMsg.senderName &&
            rabbitMsg.content === dbMsg.content
        )) {
          dbMsg.duplicate = true;
        }
      });

      const combinedMessages = mergeMessages(rabbitMessages, dbMessages);
      setMessages(combinedMessages);
// Add new RabbitMQ messages to newMessageIds
      rabbitMessages.forEach((msg) => {
        setNewMessageIds((prevIds) => new Set(prevIds).add(msg.sendTime));
        setTimeout(() => {
          setNewMessageIds((prevIds) => {
            const newIds = new Set(prevIds);
            newIds.delete(msg.sendTime);
            return newIds;
          });
        }, 2000);
      });
    } catch (error) {
      console.error("Error fetching or comparing messages:", error);
    }
  }

  function mergeMessages(
      newMessages: MessageGetDTO[],
      existingMessages: MessageGetDTO[]
  ): MessageGetDTO[] {
    const mergedMessages: MessageGetDTO[] = [...existingMessages];
    newMessages.forEach((newMsg) => {

      /**
       * Jestli je zpráva v DB tak je označena jako duplicate
       */
      const index = mergedMessages.findIndex(
          (oldMsg) =>
              oldMsg.content === newMsg.content &&
              oldMsg.senderName === newMsg.senderName &&
              oldMsg.duplicate
      );
      if (index === -1) {
        mergedMessages.push(newMsg);
      }
    });
    return mergedMessages.sort((a, b) =>
        new Date(a.sendTime).getTime() - new Date(b.sendTime).getTime()
    );
  }

  function fetchMessagesFromDb(chatRoomId: number, page: number, size: number) {
    axios
        .get(`${MESSAGES_DB_URL}/${chatRoomId}?page=${page}&size=${size}`)
        .then((response) => {
          const dbMessages: MessageGetDTO[] = response.data.content.map((msg: MessageGetDTO) => ({
            ...msg,
            duplicate: true,
          }));
          setMessages((prevMessages) => mergeMessages(dbMessages, prevMessages));
          setPage(page);
        })
        .catch((error) => {
          console.log("Error fetching messages from the database:", error);
        });
  }

  function reloadMessages() {
    if (listRef.current) {
      const scrollPosition = listRef.current.scrollTop;
      fetchMessagesFromDb(1, page + 1, 10);
      setPage((prevPage) => prevPage + 1);
      setTimeout(() => {
        if (listRef.current) {
          listRef.current.scrollTop = scrollPosition;
        }
      }, 100);
    }
  }

  function logout() {
    if (stompClient) {
      stompClient.disconnect(() => {
        console.log("Disconnected from WebSocket.");
      });
    }
    props.setUserToken("");
  }

  return (
      <div className="background-flipped">
        <Navigation logout={logout} />
        <Sidebar />
        <div className="margin-all">
          <div className="center margin-all">
            <h1>Hello {props.user.username}</h1>
          </div>
          <div ref={topElementRef}></div>
          <div className="margin-all">
            <Button variant="contained" onClick={reloadMessages} style={{ display: 'block', margin: '0 auto' }}>
              Reload
            </Button>
          </div>
          <List ref={listRef} style={{ maxHeight: '600px', overflow: 'auto' }}>
            {messages.map((msg, index) => (
                <ListItem key={index} className={msg.senderName === props.user.username ? 'my-message' : 'other-message'}>
                  <ListItemText
                      primary={msg.content}
                      secondary={`From: ${msg.senderName} at ${msg.sendTime}`}
                      className={newMessageIds.has(msg.sendTime) ? 'new-message' : ''}
                  />
                </ListItem>
            ))}
          </List>
          <div className="message-input-container fill-ou-form margin-all">
            <TextField
                label="Type your message"
                variant="outlined"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    sendMessage();
                  }
                }}
                fullWidth
            />
            <Button variant="contained" onClick={sendMessage}>
              Send
            </Button>
          </div>
        </div>
      </div>
  );
};

export default MainPage;
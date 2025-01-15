package cz.osu.r22431.swi2.controller;

import cz.osu.r22431.swi2.model.entity.ChatRoom;
import cz.osu.r22431.swi2.model.entity.ChatUser;
import cz.osu.r22431.swi2.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class DbController {

    @Autowired
    private DbService dbService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<ChatUser>> getUsers() {
        return dbService.getUsers();
    }

    @GetMapping(value = "/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestParam UUID userId) {
        return dbService.getChatRooms(userId);
    }
}

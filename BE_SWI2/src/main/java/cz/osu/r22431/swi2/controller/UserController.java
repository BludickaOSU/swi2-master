package cz.osu.r22431.swi2.controller;

import cz.osu.r22431.swi2.model.dto.user.UserLoginDTO;
import cz.osu.r22431.swi2.model.dto.user.UserRegisterDTO;
import cz.osu.r22431.swi2.service.DbService;
import cz.osu.r22431.swi2.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private DbService dbService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDTO userDTO) {
        return userServiceImpl.signup(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@RequestBody UserLoginDTO userCredentials) {
        return userServiceImpl.authenticate(userCredentials);
    }




}

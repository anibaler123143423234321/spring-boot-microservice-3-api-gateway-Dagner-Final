package com.dagnerchuman.springbootmicroservice3ApiGateway.controller;

import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.ChatMessage;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.User;
import com.dagnerchuman.springbootmicroservice3ApiGateway.service.UserService; // Importa tu servicio de usuario aquí
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> connectedUsers = new HashSet<>();
    private final UserService userService; // Asegúrate de inyectar correctamente tu servicio de usuario

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage chat(@DestinationVariable String roomId, ChatMessage message) {
        // Obtener el usuario correspondiente al ID recibido
        Long userId = Long.parseLong(message.getUserId()); // Suponiendo que el ID del usuario es un número
        User user = userService.findUserById(userId); // Suponiendo que tienes un método en tu servicio de usuario para buscar un usuario por su ID

        // Agregar el usuario a la lista de usuarios conectados
        connectedUsers.add(user.getId() + ": " + user.getNombre() + " " + user.getApellido());

        // Enviar la lista actualizada de usuarios conectados
        messagingTemplate.convertAndSend("/topic/userList/" + roomId, new ArrayList<>(connectedUsers));
        System.out.println("User list sent: " + connectedUsers);

        // Devolver el mensaje junto con la información del usuario al cliente
        return new ChatMessage(message.getMessage(), message.getUserId(), user.getNombre(), user.getApellido());
    }
}

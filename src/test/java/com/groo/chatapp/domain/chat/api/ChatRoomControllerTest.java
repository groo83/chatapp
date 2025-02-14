package com.groo.chatapp.domain.chat.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatRoomControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createChatRoom() throws Exception {
        mockMvc.perform(post("/api/chatroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomName\": \"HelloRoom\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.chatRoomId").value(2L))
                .andExpect(jsonPath("$.data.name").value("HelloRoom"));
    }*/
}
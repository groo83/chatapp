package com.groo.chatapp.domain.chat;

import com.groo.chatapp.common.entity.BaseEntity;
import com.groo.chatapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public ChatRoom(String name, Member member) {
        this.name = name;
        this.member = member;
    }
}


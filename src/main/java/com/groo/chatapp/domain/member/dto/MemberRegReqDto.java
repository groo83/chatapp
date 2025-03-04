package com.groo.chatapp.domain.member.dto;

import com.groo.chatapp.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegReqDto {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호은 필수입니다.")
    private String password;

    public Member toEntity(String encPassword) {
        return Member.builder()
                .nickname(this.nickname)
                .email(this.email)
                .password(encPassword)
                .build();
    }
}

package com.groo.chatapp.domain.member.api;


import com.groo.chatapp.common.annotation.UserInfo;
import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.common.exception.UnauthenticatedUserException;
import com.groo.chatapp.domain.member.dto.MemberDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    @GetMapping()
    public ResponseEntity<DataResponse<MemberResDto>> getMember(@UserInfo MemberDto memberDto) {
        if (memberDto == null) {
            throw new UnauthenticatedUserException();
        }

        MemberResDto resDto = MemberResDto.builder()
                .nickname(memberDto.getNickname())
                .email(memberDto.getEmail())
                .build();

        return ResponseEntity.ok(DataResponse.create(resDto));
    }
}

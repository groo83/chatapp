package com.groo.chatapp.domain.member.api;

import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.dto.MemberRegResDto;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import com.groo.chatapp.domain.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(value = HttpStatus.CREATED)
    public DataResponse<MemberRegResDto> signup(@Valid @RequestBody MemberRegReqDto reqDto) {
        return DataResponse.create(authService.signup(reqDto));
    }

    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public DataResponse<MemberResDto> signin(@Valid @RequestBody MemberReqDto reqDto) {
        return DataResponse.create(authService.signin(reqDto));
    }

    @PostMapping("/no-cache/login")
    @ResponseStatus(value = HttpStatus.OK)
    public DataResponse<MemberResDto> noCache_signin(@Valid @RequestBody MemberReqDto reqDto) {
        return DataResponse.create(authService.noCache_signin(reqDto));
    }
}
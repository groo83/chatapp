package com.groo.chatapp.config;

import com.groo.chatapp.common.annotation.UserInfo;
import com.groo.chatapp.common.exception.UnauthenticatedUserException;
import com.groo.chatapp.domain.member.CustomUserDetails;
import com.groo.chatapp.domain.member.dto.MemberDto;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return MemberDto.from((CustomUserDetails) authentication.getPrincipal());
        }

        throw new UnauthenticatedUserException("인증되지 않은 사용자입니다.");
    }
}

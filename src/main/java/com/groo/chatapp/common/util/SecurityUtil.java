package com.groo.chatapp.common.util;

import com.groo.chatapp.domain.member.CustomUserDetails;
import com.groo.chatapp.domain.member.dto.MemberDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * CustomUserArgumentResolver 사용으로 대체
 */
public class SecurityUtil {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            return authentication.getPrincipal().toString();
        }

        return null;
    }

    // 현재 로그인한 사용자의 Member 객체 가져오기
    public static MemberDto getCurrentMember() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if ( userDetails != null ) {
            return MemberDto.from(userDetails);
        } else {
            throw new IllegalStateException("Security Context에 Member 객체가 없습니다.");
        }
    }
}

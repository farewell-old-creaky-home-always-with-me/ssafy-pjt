package com.ssafy.home.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 50, message = "이름은 50자 이하여야 합니다")
        String name,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
        String password,

        @NotBlank(message = "전화번호는 필수입니다")
        @Pattern(regexp = "^[0-9\\s-]+$", message = "전화번호는 숫자와 하이픈만 입력할 수 있습니다")
        @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
        String phone
) {
}

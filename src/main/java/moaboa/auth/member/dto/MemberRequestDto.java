package moaboa.auth.member.dto;

import lombok.Getter;

import java.time.LocalDate;

public class MemberRequestDto {

    @Getter
    public static class CreateDto {
        private String nickname;
        private LocalDate birthdate;
        private String socialId;
    }
}

package hello.core.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* 회원
* */

@Getter
@AllArgsConstructor // 모든 필드 생성자로
public class Member {

    private Long id;
    private String name;
    private Grade grade;
}

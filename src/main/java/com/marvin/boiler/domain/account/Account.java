package com.marvin.boiler.domain.account;

import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 회원 Entity
 */

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 안전성 확보
@AllArgsConstructor
@Builder
@ToString(callSuper = true) // 상위 클래스 필드도 toString에 포함
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId; // 회원ID

    @Column(nullable = false, length = 100)
    private String name; // 회원이름

    @Column(nullable = false)
    private Status status; // 상태 (StatusConverter autoApply 적용)

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Embedded
    private Password password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean vipYn = false; // 기본값 설정

    // === 비즈니스 메서드 ===
    public void changeStatus(Status status) {
        if (Objects.nonNull(status)) {
            this.status = status;
        }
    }

    public void updateVipStatus(Boolean vipYn) {
        if (Objects.nonNull(vipYn)) {
            this.vipYn = vipYn;
        }
    }

    public void updateName(String name) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
    }

    public void updateEmail(String email) {
        if (StringUtils.hasText(email)) {
            this.email = email;
        }
    }
}

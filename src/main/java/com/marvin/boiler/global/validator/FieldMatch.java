package com.marvin.boiler.global.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * A필드와 B필드의 값이 같은 지 유효성 체크
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // 클래스 레벨에 붙임
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class) // 검증 로직 연결
@Documented
public @interface FieldMatch {

    String message() default "fields do not match"; // 기본 에러 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String first(); // 비교할 첫 번째 필드 이름 (예: "newPassword")
    String second(); // 비교할 두 번째 필드 이름 (예: "confirmNewPassword")

    // 하나의 클래스에 여러 번 사용할 수 있도록 List 정의
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }
}

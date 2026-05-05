package com.marvin.boiler.global.validator;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            // BeanUtils(Spring)나 자바 리플렉션을 사용하여 필드 값 추출
            final Object firstObj = BeanUtils.getPropertyDescriptor(value.getClass(), firstFieldName)
                    .getReadMethod().invoke(value);
            final Object secondObj = BeanUtils.getPropertyDescriptor(value.getClass(), secondFieldName)
                    .getReadMethod().invoke(value);

            boolean isValid = Objects.equals(firstObj, secondObj);

            if (!isValid) {
                // 특정 필드(secondFieldName)에 에러 메시지를 바인딩
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(secondFieldName)
                        .addConstraintViolation();
            }

            return isValid;
        } catch (final Exception ignore) {
            // 필드를 찾지 못하는 등의 예외 발생 시 검증 실패 처리
            return false;
        }
    }
}

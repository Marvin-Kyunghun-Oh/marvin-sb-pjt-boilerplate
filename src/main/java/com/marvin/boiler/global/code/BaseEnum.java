package com.marvin.boiler.global.code;

/**
 * 모든 Enum의 공통 규격
 */
public interface BaseEnum {
    /**
     * DB에 저장되는 실제 값 또는 Enum 상수명
     */
    String getName();

    /**
     * 화면에 표시될 설명 (한글명 등)
     */
    String getDescription();
}

package com.marvin.boiler.global.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 공통 페이징 응답 규격
 * @param <T> 데이터 타입
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Page 객체와 변환된 DTO 리스트를 받아 PageResponse를 생성합니다.
     *
     * @param page    Spring Data JPA의 Page 객체
     * @param content DTO로 변환된 데이터 리스트
     * @param <T>     엔티티 타입
     * @param <R>     DTO 타입
     * @return PageResponse
     */
    public static <T, R> PageResponse<R> from(Page<T> page, List<R> content) {
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}

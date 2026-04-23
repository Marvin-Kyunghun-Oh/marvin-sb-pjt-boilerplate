package com.marvin.boiler.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 공통 페이징 응답 규격
 * @param <T> 데이터 타입
 */
public record PageResponse<T>(
        List<T> content,
        @Schema(description = "현재 페이지", example = "1")
        int pageNumber,
        @Schema(description = "페이지 당 컨텐츠 수", example = "10")
        int pageSize,
        @Schema(description = "전체 컨텐츠 수", example = "1")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "1")
        int totalPages,
        @Schema(description = "마지막 페이지 여부", example = "true")
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

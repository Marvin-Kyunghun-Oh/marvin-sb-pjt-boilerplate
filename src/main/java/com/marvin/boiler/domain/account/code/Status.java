package com.marvin.boiler.domain.account.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE(1, "활성"),
    SUSPENDED(9, "정지"),
    DELETED(0, "삭제");

    private final int code;
    private final String description;

    private static final Map<Integer, Status> CODE_TO_ENUM =
            Stream.of(values()).collect(Collectors.toMap(Status::getCode, e -> e));

    public static Status fromCode(int code) {
        return CODE_TO_ENUM.getOrDefault(code, ACTIVE);
    }
}
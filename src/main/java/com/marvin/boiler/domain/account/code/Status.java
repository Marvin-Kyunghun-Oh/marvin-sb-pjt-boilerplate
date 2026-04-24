package com.marvin.boiler.domain.account.code;

import com.marvin.boiler.global.code.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Status implements BaseEnum {
    ACTIVE(1, "account.status.active"),
    SUSPENDED(9, "account.status.suspended"),
    DELETED(0, "account.status.deleted");

    private final int code;
    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

    private static final Map<Integer, Status> CODE_TO_ENUM =
            Arrays.stream(values()).collect(Collectors.toMap(Status::getCode, e -> e));

    public static Status fromCode(int code) {
        Status status = CODE_TO_ENUM.get(code);
        if (Objects.isNull(status)) {
            throw new IllegalArgumentException("Unknown Status code: " + code);
        }
        return status;
    }
}
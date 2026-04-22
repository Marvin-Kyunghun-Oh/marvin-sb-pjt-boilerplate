package com.marvin.boiler.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BizException extends RuntimeException {
    private final ErrorCode errorCode;
}

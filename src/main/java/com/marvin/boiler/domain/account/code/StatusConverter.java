package com.marvin.boiler.domain.account.code;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Status status) {
        return Optional.ofNullable(status)
                .map(Status::getCode)
                .orElse(null);
    }

    @Override
    public Status convertToEntityAttribute(Integer dbData) {
        return Optional.ofNullable(dbData)
                .map(Status::fromCode)
                .orElse(Status.ACTIVE); // DB 데이터가 없을 경우 기본값
    }
}

package com.example.demospringbatchcourse.config;

import javax.persistence.AttributeConverter;
import java.time.Instant;
import java.sql.Timestamp;

public class InstantTimeJPAConverter implements AttributeConverter<Instant, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Instant instant) {
        return (instant == null ? null : Timestamp.from(instant));
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp timestamp) {
        return (timestamp == null ? null : timestamp.toInstant());
    }
}

package com.fakedevelopers.bidderbidder.config;

import com.fakedevelopers.bidderbidder.domain.Constants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
// String 형태로 입력받은 마감날짜를 LocalDateTime 형태로 바꿔줌
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    @Override
    public LocalDateTime convert(String source) {
        return LocalDateTime.parse(source, formatter);
    }
}

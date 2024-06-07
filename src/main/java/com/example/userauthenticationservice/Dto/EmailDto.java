package com.example.userauthenticationservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class EmailDto {
    private String to;
    private String from;
    private String subject;
    private String body;
}

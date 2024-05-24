package com.example.userauthenticationservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Session extends BaseModel
{
    private String token;

    @Enumerated(EnumType.ORDINAL)
    private SessionState sessionState;

    @ManyToOne
    private User user;
}

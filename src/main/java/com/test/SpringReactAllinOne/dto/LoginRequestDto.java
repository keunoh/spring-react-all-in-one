package com.test.SpringReactAllinOne.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;

    public LoginRequestDto(){}
}

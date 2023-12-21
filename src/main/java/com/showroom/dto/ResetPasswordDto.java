package com.showroom.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    private String email;
    private Integer otp;
}

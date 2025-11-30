package com.telegrambot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDto {

    @NotBlank(message = "Contact name cannot be blank")
    private String name;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be valid (7-15 digits, optional +)")
    private String phone;

    private String relation;
}

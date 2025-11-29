package com.safewalk.springboot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO representing a single emergency contact.
 * Plain Java version (no Lombok) so it compiles without Lombok on classpath.
 */
public class EmergencyContactDto {

    private String name;
    private String phone;

    public EmergencyContactDto() {
    }

    public EmergencyContactDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    @NotBlank(message = "Contact name cannot be blank")
    @Size(max = 200, message = "Contact name must be at most 200 characters")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank(message = "Contact phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must be a valid international format (7-15 digits, optional +)")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "EmergencyContactDto{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

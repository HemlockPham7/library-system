package com.librarysystem.bookservice.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailRequestModel {
    @NotBlank(message = "Email is mandatory")
    private String email;
}

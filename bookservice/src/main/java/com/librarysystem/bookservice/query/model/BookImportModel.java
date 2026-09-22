package com.librarysystem.bookservice.query.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookImportModel {
    @NotBlank
    private String name;
    @NotBlank
    private String author;
    @NotBlank
    private Boolean isReady;
}

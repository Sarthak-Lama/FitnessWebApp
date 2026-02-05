package com.project.fitness.dto;

import com.project.fitness.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

 @NotBlank(message = "Password is required")
 @Min(8)
 @NotNull
 @NotEmpty
    private String password;

    private String firstName;
    private String lastName;
    private UserRole role;
}

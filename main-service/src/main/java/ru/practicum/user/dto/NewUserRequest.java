package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

//    @NotBlank
//    @Pattern(regexp = "^[^@]{1,64}@([a-zA-Z0-9-]{1,63}\\.)+[a-zA-Z]{2,6}$",
//            message = "Некорректный формат email или превышена длина его частей")
//    @Size(max = 254, message = "Email должен превышать 254 символов")

    @NotBlank
    @Email(message = "Некорректный формат email")
    @Size(min = 6, max = 254, message = "Email должен быть от 6 до 254 символов")
    private String email;
}

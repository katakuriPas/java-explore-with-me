package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank
    @Pattern(regexp = "^[^@]{1,64}@([a-zA-Z0-9-]{1,63}\\.)+[a-zA-Z]{2,6}$",
            message = "Некорректный формат email или превышена длина его частей")
    @Size(max = 254, message = "Email должен превышать 254 символов")
    private String email;
}

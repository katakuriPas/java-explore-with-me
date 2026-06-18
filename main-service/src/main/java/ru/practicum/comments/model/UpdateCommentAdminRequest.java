package ru.practicum.comments.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentAdminRequest {

    @NotBlank(message = "Статус не может быть пустым")
    private String status;
}

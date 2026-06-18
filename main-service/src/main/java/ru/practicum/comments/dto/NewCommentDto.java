package ru.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comments.CommentStatus;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotBlank(message = "Отзыв не должен быть пустым")
    @Size(max = 2000, message = "Длина комментария должна до 2000 символов.")
    private String description;

    private CommentStatus status;
}

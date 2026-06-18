package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comments.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {

    private Long id;

    private String description;

    private Long authorId;

    private LocalDateTime createdOn;

    private CommentStatus status;

    private List<CommentShortDto> comments;
}
package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private List<String> errors; // Список стектрейсов или описания ошибок

    private String message;     // Сообщение об ошибке

    private String reason;      // Общее описание причины ошибки

    private String status;      // Код статуса HTTP-ответа

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}

package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

//    // --- 400 --- Запрос составлен некорректно
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
//        String message = e.getBindingResult().getFieldErrors().stream()
//                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
//                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
//                .collect(java.util.stream.Collectors.joining("; "));
//
//        List<String> errors = Arrays.stream(e.getStackTrace())
//                .map(StackTraceElement::toString)
//                .collect(Collectors.toList());
//
//        return ApiError.builder()
//                .errors(errors)
//                .message(message)
//                .reason("Incorrectly made request.")
//                .status(HttpStatus.BAD_REQUEST.name())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }

    // --- 400 --- Запрос составлен некорректно
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // --- 404 --- Событие не найдено или недоступно
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // --- 409 --- Событие не удовлетворяет правилам редактирования
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        String databaseErrorMessage = e.getMostSpecificCause().getMessage();

        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                //.errors(Collections.emptyList())
                .errors(errors)
                .message(databaseErrorMessage)
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                //.errors(Collections.emptyList())
                .errors(errors)
                .message(e.getMessage())
                .reason("Internal server error occurred.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now())
                .build();
    }


}

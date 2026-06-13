package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {

    private final PublicCompilationService publicCompService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("GetMapping: getCompilationById compId = {}", compId);

        return publicCompService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GetMapping: getCompilations from = {}, size = {}", from, size);

        return publicCompService.getCompilations(from, size);
    }
}

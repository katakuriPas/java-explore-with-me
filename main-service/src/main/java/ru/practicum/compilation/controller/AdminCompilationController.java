package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.service.AdminCompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("@PostMapping: createCompilation compilationDto = {}", compilationDto);

        return compilationService.createCompilation(compilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{compId}")
    void deleteCompilationById(@PathVariable Long compId) {
        log.info("@DeleteMapping: deleteCompilationById compId = {}", compId);

        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateComp) {
        log.info("@PatchMapping: updateCompilationById compId = {}, UpdateCompilationRequest = {}",
                compId, updateComp);

        return compilationService.updateCompilationById(compId, updateComp);
    }
}

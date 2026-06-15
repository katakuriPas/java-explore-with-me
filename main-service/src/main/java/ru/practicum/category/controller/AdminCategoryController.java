package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    // --- POST ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newDto) {
        log.info("@PostMapping: createCategory NewCategoryDto = {}", newDto);
        return categoryService.createCategory(newDto);
    }

    // --- DELETE ---
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteUser(@PathVariable Long catId) {
        log.info("@DeleteMapping: deleteUser catId = {}", catId);
        categoryService.deleteCategory(catId);
    }

    // --- PATCH ---
    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable Long catId,
                                     @Valid @RequestBody CategoryDto dto) {
        log.info("@PatchMapping: patchCategory catId = {}, dto = {}", catId, dto);
        return categoryService.patchCategory(catId, dto);
    }
}

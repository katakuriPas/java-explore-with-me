package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // --- GET ---
    @GetMapping("/{catId}")
    public CategoryDto getCategoryDtoById(@PathVariable Long catId) {
        log.info("GetMapping(\"/{catId}\"): getCategoryById catId = {}", catId);
        return categoryService.getCategoryDtoById(catId);
    }

    @GetMapping("/all")
    public List<CategoryDto> findAllCategory() {
        return categoryService.findAllCategory();
    }

    @GetMapping
    public List<CategoryDto> getCategoriesFromAndSize(
            @RequestParam(name = "from", defaultValue = "0") Long from,
            @RequestParam(name = "size", defaultValue = "10") Long size
    ) {
        log.info("@GetMapping: getCategoriesFromAndSize from = {}, size = {})", from, size);
        return categoryService.getCategoriesFromAndSize(from, size);
    }
}

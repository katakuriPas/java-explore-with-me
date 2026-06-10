package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // --- POST ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody NewCategoryDto newDto) {
        log.info("PostMapping: createCategory NewCategoryDto = {}", newDto);
        return categoryService.createCategory(newDto);
    }

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
        log.info("@GetMapping: getUser from = {}, size = {})", from, size);
        return categoryService.getCategoriesFromAndSize(from, size);
    }

    // --- DELETE ---
    @DeleteMapping("/{catId}")
    public void deleteUser(@PathVariable Long catId) {
        log.info("DeleteMapping(\"/{catId}\"): deleteUser catId = {}", catId);
        categoryService.deleteCategory(catId);
    }

    // --- PATCH ---
    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable Long catId, @RequestBody CategoryDto dto) {
        log.info("PatchMapping(\"/{catId}\"): patchCategory catId = {}, dto = {}", catId, dto);
        return categoryService.patchCategory(catId, dto);
    }
}

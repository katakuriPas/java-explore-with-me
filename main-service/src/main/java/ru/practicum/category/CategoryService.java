package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private static final String CATEGORY_NOT_FOUND = "Category with id =%d not found";

    private final CategoryMapping mapping;
    private final CategoryRepository repository;

    public CategoryDto createCategory(NewCategoryDto newDto) {
        Category category = mapping.toEntity(newDto);
        if (repository.existsByName(category.getName())) {
            log.warn("The name '{}' is already in use", category.getName());
            throw new ConflictException("The name is already in use");
        }

        Category categorySave = repository.save(category);

        return mapping.toDto(categorySave);
    }

    public List<CategoryDto> findAllCategory() {
        return repository.findAllByOrderByIdAsc().stream().map(mapping::toDto).toList();
    }

    public void deleteCategory(Long catId) {
        if (!repository.existsById(catId)) {
            throw new NotFoundException(CATEGORY_NOT_FOUND.formatted(catId));
        }
        log.info("Received a request to delete category with id {}", catId);
        repository.deleteById(catId);
    }

    public CategoryDto patchCategory(Long catId, CategoryDto dto) {
        Category existingCategory = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.formatted(catId)));

        log.info("Editing a category with catId = {} on CategoryDto: {}", catId, dto);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existingCategory.setName(dto.getName());
        }

        Category savedCategory = repository.save(existingCategory);

        return mapping.toDto(savedCategory);
    }

    public CategoryDto getCategoryDtoById(Long catId) {
        Category existingCategory = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.formatted(catId)));
        log.info("Getting a categoryDto {}", existingCategory);
        return mapping.toDto(existingCategory);
    }

    public Category getCategoryById(Long catId) {
        Category existingCategory = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND.formatted(catId)));
        log.info("Getting a categoryEntity  {}", existingCategory);
        return existingCategory;
    }

    public List<CategoryDto> getCategoriesFromAndSize(Long from, Long size) {
        List<Category> categories = repository.findCategoriesFromAndSize(from, size);
        return categories.stream()
                .map(mapping::toDto)
                .toList();
    }
}

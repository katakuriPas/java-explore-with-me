package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationService {
    private static final String COMPILATION_NOT_FOUND = "Compilation with id=%d was not found";

    private final CompilationRepository compRepository;

    private final CompilationMapper compMapper;

    public CompilationDto getCompilationById(Long compId) {
        Compilation comp = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(COMPILATION_NOT_FOUND.formatted(compId)));

        CompilationDto compDto = compMapper.toCompilationDto(comp);

        return compDto;
    }

    public List<CompilationDto> getCompilations() {
        List<Compilation> comps = compRepository.findAll();

        List<CompilationDto> compDtos = new ArrayList<>(comps.size());
        for (Compilation comp : comps) {
            CompilationDto compDto = compMapper.toCompilationDto(comp);

            compDtos.add(compDto);
        }

        return compDtos;
    }

    public List<CompilationDto> getCompilations(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> comp = compRepository.findAll(pageable).getContent();

        return comp.stream()
                .map(compMapper::toCompilationDto)
                .toList();
    }
}

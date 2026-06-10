package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationService {
    private static final String COMPILATION_NOT_FOUND = "Compilation with id=%d was not found";

    private final CompilationRepository compRepository;
    private final EventRepository eventRepository;

    private final CompilationMapper compMapper;

    public CompilationDto createCompilation(NewCompilationDto newDto) {

        Compilation compilation = compMapper.toCompilation(newDto);

        List<Long> eventIds = newDto.getEvents() != null ? newDto.getEvents() : List.of();

        List<Event> events = eventRepository.findAllById(eventIds);
        compilation.setEvents(events);

        Compilation savedCompilation = compRepository.save(compilation);

        return compMapper.toCompilationDto(savedCompilation);
    }

    public void deleteCompilationById(Long compId) {
        // existsById работает быстрее, так как не тащит все поля и связи из БД
        if (!compRepository.existsById(compId)) {
            throw new NotFoundException(COMPILATION_NOT_FOUND.formatted(compId));
        }

        compRepository.deleteById(compId);
    }

    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequest updateComp) {
        Compilation comp = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(COMPILATION_NOT_FOUND.formatted(compId)));

        compMapper.updateCompilation(updateComp, comp);

        Compilation savedComp = compRepository.save(comp);

        return compMapper.toCompilationDto(savedComp);
    }
}

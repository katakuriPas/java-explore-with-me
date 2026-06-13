package ru.practicum.compilation;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public abstract class CompilationMapper {

    @Autowired
    protected EventRepository eventRepository;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "events", source = "events")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "pinned", source = "pinned")
    public abstract CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    public abstract Compilation toCompilation(NewCompilationDto newDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "pinned", source = "pinned")
    public abstract void updateCompilation(UpdateCompilationRequest updateComp, @MappingTarget Compilation comp);

    protected List<Event> mapIdsToEvents(List<Long> eventIds) {
        if (eventIds == null) {
            return null;
        }
        return eventRepository.findAllById(eventIds);
    }
}

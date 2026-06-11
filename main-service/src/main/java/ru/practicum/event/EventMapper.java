package ru.practicum.event;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.category.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.enumState.EventState;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.location.Location;
import ru.practicum.stats.StatsManager;
import ru.practicum.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    protected StatsManager statsManager;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "state", expression = "java(ru.practicum.event.enumState.EventState.PENDING)")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    public abstract Event toEvent(NewEventDto newEventDto, Category category, User initiator, Location location);

    @Mapping(target = "views", ignore = true)
    public abstract EventFullDto toEventFullDto(Event event);

    @Mapping(target = "views", ignore = true)
    public abstract EventShortDto toEventShortDto(Event event);

    public abstract List<EventShortDto> toEventShortDtoList(List<Event> events);

    abstract List<EventFullDto> toEventFullDtoList(List<Event> events);

    @AfterMapping
    protected void enrichSingleFullDto(@MappingTarget EventFullDto dto, Event event) {
        if (event != null && dto != null) {
            statsManager.enrichWithViews(List.of(dto), List.of(event));
        }
    }

    @AfterMapping
    protected void enrichSingleShortDto(@MappingTarget EventShortDto dto, Event event) {
        if (event != null && dto != null) {
            statsManager.enrichWithViews(List.of(dto), List.of(event));
        }
    }

    @AfterMapping
    protected void enrichFullDtoList(@MappingTarget List<EventFullDto> dtos, List<Event> events) {
        statsManager.enrichWithViews(dtos, events);
    }

    @AfterMapping
    protected void enrichShortDtoList(@MappingTarget List<EventShortDto> dtos, List<Event> events) {
        statsManager.enrichWithViews(dtos, events);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state",
            expression = "java(mapStateAction(updateEvent.getStateAction(), eventTarget.getState()))")
    public abstract void updateEvent(UpdateEventUserRequest updateEvent, @MappingTarget Event eventTarget);

    EventState mapStateAction(String stateAction, EventState currentState) {
        if (stateAction == null) {
            return currentState;
        }

        return switch (stateAction) {
            case "SEND_TO_REVIEW" -> EventState.PENDING;
            case "CANCEL_REVIEW" -> EventState.CANCELED;
            default -> throw new IllegalArgumentException("Неизвестное действие: " + stateAction);
        };
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state",
            expression = "java(mapAdminStateAction(updateAdminEvent.getStateAction(), eventTarget.getState()))")
    public abstract void updateEventAdmin(UpdateEventAdminRequest updateAdminEvent, @MappingTarget Event eventTarget);

    EventState mapAdminStateAction(String stateAction, EventState currentState) {
        if (stateAction == null) {
            return currentState;
        }

        return switch (stateAction) {
            case "PUBLISH_EVENT" -> EventState.PUBLISHED;
            case "REJECT_EVENT" -> EventState.CANCELED;
            default -> throw new IllegalArgumentException("Неизвестное действие: " + stateAction);
        };
    }

}

package ru.practicum.comments;

import org.mapstruct.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "authorId", source = "author.id")
    CommentShortDto toShortDto(Comment comment);

    List<CommentDto> toCommentDtoList(List<Comment> comments);

    List<CommentShortDto> toCommentShortDtoList(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "status", source = "status", defaultValue = "NEUTRAL")
    Comment toCommentByNew(NewCommentDto newComment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    Comment updateComment(NewCommentDto updateComment, @MappingTarget Comment commentTarget);
}
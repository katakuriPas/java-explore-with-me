package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    public EndpointHitDto saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = endpointHitMapper.toEntity(hitDto);

        return endpointHitMapper.toDto(statsRepository.save(hit));
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return statsRepository.getViewStatsUnique(start, end, uris);
        } else {
            return statsRepository.getViewStats(start, end, uris);
        }
    }
}

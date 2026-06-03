package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.stats.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM EndpointHit s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR s.uri IN :uris) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getViewStatsUnique(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM EndpointHit s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR s.uri IN :uris) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getViewStats(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("uris") List<String> uris);
}

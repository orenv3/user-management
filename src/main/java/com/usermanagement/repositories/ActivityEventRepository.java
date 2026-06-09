package com.usermanagement.repositories;

import com.usermanagement.entities.ActivityEvent;
import com.usermanagement.entities.ActivityEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {

    long countByEventType(ActivityEventType eventType);

    @Query("SELECT COUNT(DISTINCT e.sessionId) FROM ActivityEvent e WHERE e.eventType = com.usermanagement.entities.ActivityEventType.PAGE_VIEW AND e.sessionId IS NOT NULL")
    long countDistinctPageViewSessions();

    @Query("SELECT e.email, COUNT(e) FROM ActivityEvent e WHERE e.eventType = com.usermanagement.entities.ActivityEventType.LOGIN AND e.email IS NOT NULL GROUP BY e.email ORDER BY COUNT(e) DESC")
    List<Object[]> countLoginsByEmail();

    @Query("SELECT e.email, COUNT(e) FROM ActivityEvent e WHERE e.eventType = com.usermanagement.entities.ActivityEventType.ACTION AND e.email IS NOT NULL GROUP BY e.email ORDER BY COUNT(e) DESC")
    List<Object[]> countActionsByEmail();

    Page<ActivityEvent> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<ActivityEvent> findByEventTypeOrderByTimestampDesc(ActivityEventType eventType, Pageable pageable);
}

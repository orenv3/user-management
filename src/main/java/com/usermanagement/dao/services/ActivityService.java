package com.usermanagement.dao.services;

import com.usermanagement.entities.ActivityEvent;
import com.usermanagement.entities.ActivityEventType;
import com.usermanagement.repositories.ActivityEventRepository;
import com.usermanagement.requestObjects.RecordActivityEventRequest;
import com.usermanagement.responseObjects.ActivityEventResponse;
import com.usermanagement.responseObjects.ActivitySummaryResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityEventRepository activityEventRepository;

    public ActivityEventResponse recordEvent(
            RecordActivityEventRequest request,
            Long userId,
            String email
    ) {
        ActivityEvent event = new ActivityEvent();
        event.setTimestamp(Instant.now());
        event.setEventType(request.eventType());
        event.setPath(request.path());
        event.setSessionId(trimToNull(request.sessionId()));
        event.setAction(trimToNull(request.action()));
        event.setDetails(trimToNull(request.details()));
        event.setUserId(userId);
        event.setEmail(trimToNull(email));
        ActivityEvent saved = activityEventRepository.save(event);
        log.debug("Activity recorded. type={} path={} email={}", saved.getEventType(), saved.getPath(), saved.getEmail());
        return ActivityEventResponse.from(saved);
    }

    public void recordLogin(Long userId, String email, String path) {
        ActivityEvent event = new ActivityEvent();
        event.setTimestamp(Instant.now());
        event.setEventType(ActivityEventType.LOGIN);
        event.setPath(path);
        event.setAction("login");
        event.setUserId(userId);
        event.setEmail(email);
        activityEventRepository.save(event);
        log.info("Login recorded. email={}", email);
    }

    public void recordAction(String path, String action, Long userId, String email, String details) {
        ActivityEvent event = new ActivityEvent();
        event.setTimestamp(Instant.now());
        event.setEventType(ActivityEventType.ACTION);
        event.setPath(path);
        event.setAction(action);
        event.setUserId(userId);
        event.setEmail(email);
        event.setDetails(details);
        activityEventRepository.save(event);
    }

    public ActivitySummaryResponse getSummary() {
        long pageViews = activityEventRepository.countByEventType(ActivityEventType.PAGE_VIEW);
        long uniqueSessions = activityEventRepository.countDistinctPageViewSessions();
        long logins = activityEventRepository.countByEventType(ActivityEventType.LOGIN);
        long actions = activityEventRepository.countByEventType(ActivityEventType.ACTION);

        List<ActivitySummaryResponse.UserActivityCount> loginsByUser = activityEventRepository.countLoginsByEmail()
                .stream()
                .map(row -> new ActivitySummaryResponse.UserActivityCount((String) row[0], (Long) row[1]))
                .toList();

        List<ActivitySummaryResponse.UserActivityCount> actionsByUser = activityEventRepository.countActionsByEmail()
                .stream()
                .map(row -> new ActivitySummaryResponse.UserActivityCount((String) row[0], (Long) row[1]))
                .toList();

        return new ActivitySummaryResponse(pageViews, uniqueSessions, logins, actions, loginsByUser, actionsByUser);
    }

    public Page<ActivityEventResponse> getEvents(int pageNumber, int pageSize, ActivityEventType eventType) {
        PageRequest pageable = PageRequest.of(Math.max(pageNumber, 0), Math.max(pageSize, 1));
        Page<ActivityEvent> page = eventType == null
                ? activityEventRepository.findAllByOrderByTimestampDesc(pageable)
                : activityEventRepository.findByEventTypeOrderByTimestampDesc(eventType, pageable);
        return page.map(ActivityEventResponse::from);
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

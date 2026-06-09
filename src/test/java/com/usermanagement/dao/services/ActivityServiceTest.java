package com.usermanagement.dao.services;

import com.usermanagement.entities.ActivityEvent;
import com.usermanagement.entities.ActivityEventType;
import com.usermanagement.repositories.ActivityEventRepository;
import com.usermanagement.requestObjects.RecordActivityEventRequest;
import com.usermanagement.responseObjects.ActivitySummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityEventRepository activityEventRepository;

    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        activityService = new ActivityService(activityEventRepository);
    }

    @Test
    void recordEvent_persistsPageView() {
        RecordActivityEventRequest request = new RecordActivityEventRequest(
                ActivityEventType.PAGE_VIEW,
                "/users",
                "session-1",
                null,
                null
        );
        when(activityEventRepository.save(any(ActivityEvent.class))).thenAnswer(inv -> {
            ActivityEvent e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        activityService.recordEvent(request, null, null);

        ArgumentCaptor<ActivityEvent> captor = ArgumentCaptor.forClass(ActivityEvent.class);
        verify(activityEventRepository).save(captor.capture());
        ActivityEvent saved = captor.getValue();
        assertThat(saved.getEventType()).isEqualTo(ActivityEventType.PAGE_VIEW);
        assertThat(saved.getPath()).isEqualTo("/users");
        assertThat(saved.getSessionId()).isEqualTo("session-1");
    }

    @Test
    void getSummary_aggregatesCounts() {
        when(activityEventRepository.countByEventType(ActivityEventType.PAGE_VIEW)).thenReturn(10L);
        when(activityEventRepository.countDistinctPageViewSessions()).thenReturn(4L);
        when(activityEventRepository.countByEventType(ActivityEventType.LOGIN)).thenReturn(3L);
        when(activityEventRepository.countByEventType(ActivityEventType.ACTION)).thenReturn(7L);
        when(activityEventRepository.countLoginsByEmail())
                .thenReturn(List.of(new Object[][]{new Object[]{"user@example.com", 2L}}));
        when(activityEventRepository.countActionsByEmail())
                .thenReturn(List.of(new Object[][]{new Object[]{"admin@example.com", 5L}}));

        ActivitySummaryResponse summary = activityService.getSummary();

        assertThat(summary.totalPageViews()).isEqualTo(10);
        assertThat(summary.uniqueSessions()).isEqualTo(4);
        assertThat(summary.totalLogins()).isEqualTo(3);
        assertThat(summary.totalActions()).isEqualTo(7);
        assertThat(summary.loginsByUser()).hasSize(1);
        assertThat(summary.actionsByUser()).hasSize(1);
    }
}

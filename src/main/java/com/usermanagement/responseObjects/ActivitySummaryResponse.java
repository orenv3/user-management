package com.usermanagement.responseObjects;

import java.util.List;

public record ActivitySummaryResponse(
        long totalPageViews,
        long uniqueSessions,
        long totalLogins,
        long totalActions,
        List<UserActivityCount> loginsByUser,
        List<UserActivityCount> actionsByUser
) {
    public record UserActivityCount(String email, long count) {
    }
}

package com.usermanagement.utils;

import java.util.Collections;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public final class TaskStatus {
  private final String PENDING = "PENDING";
  private final String COMPLETED = "COMPLETED";
  private final String ARCHIVED = "ARCHIVED";
  private final Set<String> statusOptions = Collections.unmodifiableSet(Set.of(PENDING, COMPLETED, ARCHIVED));

  public boolean isValidStatus(String status) {
    return statusOptions.contains(status.toUpperCase());
  }
}

package com.usermanagement.utils;

import lombok.Getter;

import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;

@Getter
@Component(role = TaskStatus.class)
public final class TaskStatus {
  private final String PENDING = "PENDING";
  private final String COMPLETED = "COMPLETED";
  private final String ARCHIVED = "ARCHIVED";
  private final Set<String> statusOptions = Set.of( PENDING,COMPLETED,ARCHIVED);
  public boolean isValidStatus(String status){
    return statusOptions.contains(status.toUpperCase());
  }
}

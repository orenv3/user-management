package com.usermanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "activity_events")
public class ActivityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityEventType eventType;

    @Column
    private Long userId;

    @Column
    private String email;

    @Column
    private String sessionId;

    @Column
    private String path;

    @Column
    private String action;

    @Column(length = 500)
    private String details;
}

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
@Table(
        name = "visitor_notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = "client_ip")
)
public class VisitorNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_ip", nullable = false, unique = true)
    private String clientIp;

    @Column
    private String loginEmail;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private Instant firstSeenAt;

    @Column(nullable = false)
    private Instant lastSeenAt;
}

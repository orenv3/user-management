package com.usermanagement.repositories;

import com.usermanagement.entities.VisitorNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorNotificationRepository extends JpaRepository<VisitorNotification, Long> {

    boolean existsByClientIp(String clientIp);

    Optional<VisitorNotification> findByClientIp(String clientIp);
}

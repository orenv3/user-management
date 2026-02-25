package com.usermanagement.repositories;

import com.usermanagement.entities.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    @Query("FROM Task WHERE assignee.id = :assignee AND status != :arch")
    List<Task> getAllByAssignee(@Param("assignee")long assignee,@Param("arch") String status);
    
    Optional<Task> findByTitle(String title);

    @Transactional
    @Modifying(clearAutomatically = true) // After changing the persistence context become old.
                                        // Via method clear() we force/make sure that persistence context will fetch the new details from DB next time
    @Query("UPDATE Task t SET t.status = :completed WHERE t.id = :taskId")
    Integer updateTaskToComplete(long taskId, String completed);
}

package com.usermanagement.entities;

import com.usermanagement.requestObjects.CreateTaskRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "task")
public final class Task {

    public Task(CreateTaskRequest taskObj){
        this.description = taskObj.description();
        this.status = taskObj.status();
        this.title = taskObj.title();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String status; //(pending/completed/archived)

    // Many tasks to one user
    @ManyToOne
    @JoinColumn(name = "assignee")
    private User assignee;

    // Explicit getters/setters to break compilation cycle
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // public String getTitle() { return title; }
    // public String getDescription() { return description; }
    // public String getStatus() { return status; }
    // public User getAssignee() { return assignee; }
    // public void setTitle(String title) { this.title = title; }
    // public void setDescription(String description) { this.description = description; }
    // public void setStatus(String status) { this.status = status; }
    // public void setAssignee(User assignee) { this.assignee = assignee; }

}

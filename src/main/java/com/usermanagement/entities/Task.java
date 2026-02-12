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


}

package com.usermanagement.entities;

import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UserTaskCommentRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@Data
@Entity
@Table(name = "comment")
public final class Comment {

    public Comment(AdminCreateCommentRequest commentObj){
        this.comment = commentObj.comment();
        this.timestamp = new Date();
    }

    public Comment(UserTaskCommentRequest commentObj){
        this.comment = commentObj.comment();
        this.timestamp = new Date();
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date timestamp;

    @Column
    private String comment;


    // Many comments to one user
    // Many tasks to one user
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;
     //(foreign key)


    // Many comments to one task
    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task taskId;
     //(foreign key)

    // Explicit getters/setters to break compilation cycle
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // public Date getTimestamp() { return timestamp; }
    // public String getComment() { return comment; }
    // public User getUserId() { return userId; }
    // public Task getTaskId() { return taskId; }
    // public void setComment(String comment) { this.comment = comment; }
    // public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    // public void setTaskId(Task taskId) { this.taskId = taskId; }
    // public void setUserId(User userId) { this.userId = userId; }

}

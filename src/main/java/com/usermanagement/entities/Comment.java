package com.usermanagement.entities;

import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UserTaskCommentRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "comment")
public final class Comment {

    @Transient
    private final AdminCreateCommentRequest adminCreateCommentRequest;
    @Transient
    private final UserTaskCommentRequest userTaskCommentRequest;
   

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



    // Many comments to one task
    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task taskId;

      public Comment(AdminCreateCommentRequest commentObj){
        this.comment = commentObj.comment();
        this.timestamp = new Date();
    }

    public Comment(UserTaskCommentRequest commentObj){
        this.comment = commentObj.comment();
        this.timestamp = new Date();
    }
   
}

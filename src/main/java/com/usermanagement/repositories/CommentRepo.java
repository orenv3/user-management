package com.usermanagement.repositories;

import com.usermanagement.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
   List<Comment> findByTaskId_TitleIn( @Param("titles") List<String> titles);

}

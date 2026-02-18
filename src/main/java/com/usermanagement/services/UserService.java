package com.usermanagement.services;

import com.usermanagement.entities.User;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Service("AdminUserImpl")
public class UserService {

    private UserRepo userRepo;
    private EntityMapper entityMapper;
   
    public UserService(UserRepo userRepo, EntityMapper entityMapper){
        this.userRepo = userRepo;
        this.entityMapper = entityMapper;
    }
    public UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }
    public UserService(EntityMapper entityMapper){
        this.entityMapper = entityMapper;
    }

    public UserResponse updateUser(UpdateUserRequest updateObj){
        User user = userRepo.getReferenceById(updateObj.id());
        entityMapper.updateUserFromRequest(updateObj, user);
        User savedUser =  userRepo.save(user);
        return entityMapper.toUserResponse(savedUser);
    }

    public String deleteUser(long id){
        // User user = userRepo.getReferenceById(id);
        userRepo.deleteById(id);
        return "Deleted: "+!(userRepo.existsById(id));
    }


    public List<UserResponse> getAllUserList(){
        List<User> usersList = userRepo.findAll();
        return entityMapper.toUserResponseList(usersList);
    }

    public List<UserResponse> getAllUserListWithPageRequest(int pageNo, int pageSize){
        Pageable pageable =  PageRequest.of(pageNo-1,pageSize);
        return entityMapper.toUserResponseList(userRepo.findAll(pageable).getContent());
    }

    public User getUserById(long id){
        return userRepo.getReferenceById(id);
    }
    public Optional<User> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }
}


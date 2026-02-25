package com.usermanagement.services;

import com.usermanagement.entities.User;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service("AdminUserImpl")
public class UserService {

    private final UserRepo userRepo;
    private final EntityMapper entityMapper;

    public UserResponse updateUser(UpdateUserRequest updateObj){
        User user = userRepo.getReferenceById(updateObj.id());
        entityMapper.updateUserFromRequest(updateObj, user);
        User savedUser =  userRepo.save(user);
        return entityMapper.toUserResponse(savedUser);
    }

    public String deleteUser(long id){
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


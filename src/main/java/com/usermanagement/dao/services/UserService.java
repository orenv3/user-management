package com.usermanagement.dao.services;

import com.usermanagement.entities.User;
import com.usermanagement.mappers.DtoMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("AdminUserImpl")
public class UserService {

    private final UserRepo userRepo;
    private final DtoMapper dtoMapper;
    
    // Explicit constructor to break compilation cycle
    // public UserService(UserRepo userRepo, DtoMapper dtoMapper) {
    //     this.userRepo = userRepo;
    //     this.dtoMapper = dtoMapper;
    // }

    public UserResponse updateUser(UpdateUserRequest updateObj){
        User user = userRepo.getReferenceById(updateObj.id());
        user = updateObj.updateUserParameters(updateObj,user);
        User savedUser =  userRepo.save(user);
        return dtoMapper.toUserResponse(savedUser);
    }

    public String deleteUser(long id){
        // User user = userRepo.getReferenceById(id);
        userRepo.deleteById(id);
        return "Deleted: "+!(userRepo.existsById(id));
    }


    public List<UserResponse> getAllUserList(){
        List<User> usersList = userRepo.findAll();
        return usersList.stream()
                .map(dtoMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUserListWithPageRequest(int pageNo, int pageSize){
        Pageable pageable =  PageRequest.of(pageNo-1,pageSize);
        return userRepo.findAll(pageable)
                .getContent()
                .stream()
                .map(dtoMapper::toUserResponse)
                .collect(Collectors.toList());

    }

    public User getUserById(long id){
        return userRepo.getReferenceById(id);
    }
    public Optional<User> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }
}


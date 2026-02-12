package com.usermanagement.dao.services;

import com.usermanagement.entities.User;
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

    public UserResponse updateUser(UpdateUserRequest updateObj){
        User user = userRepo.getReferenceById(updateObj.id());
        user = updateObj.updateUserParameters(updateObj,user);
        User savedUser =  userRepo.save(user);
        return mapToUserResponse(savedUser);
    }

    public String deleteUser(long id){
        User user = userRepo.getReferenceById(id);
        userRepo.deleteById(id);
        return "Deleted: "+!(userRepo.existsById(id));
    }


    public List<UserResponse> getAllUserList(){
        List<User> usersList = userRepo.findAll();
        return usersList.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUserListWithPageRequest(int pageNo, int pageSize){
        Pageable pageable =  PageRequest.of(pageNo-1,pageSize);
        return userRepo.findAll(pageable)
                .getContent()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

    }

    public User getUserById(long id){
        return userRepo.getReferenceById(id);
    }
    public Optional<User> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isAdmin(),
                user.isActive()
        );
    }
}


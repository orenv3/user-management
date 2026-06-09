package com.usermanagement.dao.services;

import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.entities.Users;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service("AdminUserImpl")
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final EntityMapper entityMapper;
    private final PrivateAdminPolicy privateAdminPolicy;

    public UserResponse updateUser(UpdateUserRequest updateObj){
        log.info("Update user requested. id={}", updateObj.id());
        Users user = userRepo.getReferenceById(updateObj.id());
        privateAdminPolicy.assertNotPrivateAdmin(user.getEmail());
        if (updateObj.email() != null) {
            privateAdminPolicy.assertNotPrivateAdminEmailChange(updateObj.email());
        }
        entityMapper.updateUserFromRequest(updateObj, user);
        Users savedUser =  userRepo.save(user);
        log.info("User updated. id={} email={}", savedUser.getId(), savedUser.getEmail());
        return entityMapper.toUserResponse(savedUser);
    }

    public String deleteUser(long id){
        log.info("Delete user requested. id={}", id);
        Users user = userRepo.getReferenceById(id);
        privateAdminPolicy.assertNotPrivateAdmin(user.getEmail());
        userRepo.deleteById(id);
        boolean deleted = !(userRepo.existsById(id));
        log.info("Delete user result. id={} deleted={}", id, deleted);
        return "Deleted: " + deleted;
    }


    public List<UserResponse> getAllUserList(){
        log.info("Get all users requested.");
        List<Users> usersList = userRepo.findAll();
        log.info("Get all users result count={}", usersList.size());
        return privateAdminPolicy.filterFromList(entityMapper.toUserResponseList(usersList));
    }

    public List<UserResponse> getAllUserListWithPageRequest(int pageNo, int pageSize){
        log.info("Get users page requested. pageNo={} pageSize={}", pageNo, pageSize);
        Pageable pageable =  PageRequest.of(pageNo-1,pageSize);
        List<Users> content = userRepo.findAll(pageable).getContent();
        log.info("Get users page result count={}", content.size());
        return privateAdminPolicy.filterFromList(entityMapper.toUserResponseList(content));
    }

    public Users getUserById(long id){
        log.debug("Get user by id requested. id={}", id);
        return userRepo.getReferenceById(id);
    }
    public Optional<Users> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }
}


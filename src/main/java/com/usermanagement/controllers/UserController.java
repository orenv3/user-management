package com.usermanagement.controllers;


import com.usermanagement.dao.services.UserService;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "UserController", description = "The User API. " +
        "Contains all the operations that can be performed on User table.")
@RequestMapping("/api/userTable/")
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    

    /**
     * Admin privilege
     * Delete user by ID
     * @param id User id
     * @return
     */
    @DeleteMapping("admin/deleteUser/{id}")
    @Operation(summary = "Delete a user by id (admin only)")
    public String delete(@NotNull @Min(2) @PathVariable("id") long id) {//1 is default admin
        logRequest("deleteUser", "id=" + id);
        return userService.deleteUser(id);
    }

    /**
     * Admin privilege
     * update user details
     * @param updateObj
     * @return
     */
    @PutMapping("admin/updateUser")
    @Operation(summary = "Update a user (admin only)")
    public UserResponse update(@Valid @RequestBody() UpdateUserRequest updateObj) {
        logRequest("updateUser", "id=" + updateObj.id());
        return userService.updateUser(updateObj);
    }

    /**
     * Admin privilege
     * Get list of all user in DB
     * @return
     */
    @GetMapping("admin/allUserList")
    @Operation(summary = "List all users (admin only)")
    public List<UserResponse> getAllUserList(){
        logRequest("allUserList", null);
        return userService.getAllUserList();
    }

    /**
     * Admin privilege
     * Get list of all user in DB with PAGINATION
     * @return List<User>
     */
    @GetMapping("admin/allUserListWithPagination")
    public List<UserResponse> getAllUserListWithPagination(@NotNull int pageNumber, @NotNull int pageSize){
        logRequest("allUserListWithPagination", "pageNumber=" + pageNumber + ", pageSize=" + pageSize);
        return userService.getAllUserListWithPageRequest(pageNumber, pageSize);
    }

    private static void logRequest(String action, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth == null ? "anonymous" : String.valueOf(auth.getName());
        Object authorities = auth == null ? "[]" : auth.getAuthorities();
        if (details == null || details.isBlank()) {
            log.info("UserController action={} principal={} authorities={}", action, principal, authorities);
        } else {
            log.info("UserController action={} principal={} authorities={} details={}", action, principal, authorities, details);
        }
    }

}

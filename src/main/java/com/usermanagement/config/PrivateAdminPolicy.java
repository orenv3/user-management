package com.usermanagement.config;

import com.usermanagement.responseObjects.UserResponse;
import com.usermanagement.errorHandler.ProtectedUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PrivateAdminPolicy {

    private final String privateAdminEmail;

    public PrivateAdminPolicy(@Value("${app.private-admin-email:}") String privateAdminEmail) {
        this.privateAdminEmail = privateAdminEmail == null ? "" : privateAdminEmail.trim();
    }

    public boolean isConfigured() {
        return !privateAdminEmail.isBlank();
    }

    public boolean isPrivateAdmin(String email) {
        return isConfigured() && email != null && privateAdminEmail.equalsIgnoreCase(email.trim());
    }

    public List<UserResponse> filterFromList(List<UserResponse> users) {
        if (!isConfigured()) {
            return users;
        }
        return users.stream()
                .filter(u -> !isPrivateAdmin(u.email()))
                .toList();
    }

    public void assertNotPrivateAdmin(String email) {
        if (isPrivateAdmin(email)) {
            throw new ProtectedUserException("This account is protected and cannot be modified.");
        }
    }

    public void assertNotPrivateAdminEmailChange(String newEmail) {
        if (isPrivateAdmin(newEmail)) {
            throw new ProtectedUserException("This email is reserved for a protected account.");
        }
    }

    public boolean emailsEqual(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b.trim());
    }

    public String configuredEmail() {
        return privateAdminEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrivateAdminPolicy that)) return false;
        return Objects.equals(privateAdminEmail, that.privateAdminEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privateAdminEmail);
    }
}

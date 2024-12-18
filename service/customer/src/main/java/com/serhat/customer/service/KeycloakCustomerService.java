package com.serhat.customer.service;

import com.serhat.customer.entity.Admin;
import com.serhat.customer.entity.Customer;
import com.serhat.customer.entity.Role;
import com.serhat.customer.exception.CustomerNotFoundException;
import com.serhat.customer.exception.SameRequestException;
import com.serhat.customer.repository.CustomerRepository;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakCustomerService {

    private final CustomerRepository customerRepository;
    private final Keycloak keycloak;

    public void createKeycloakUser(Customer customer) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(customer.getName());
            user.setFirstName(customer.getName());
            user.setLastName(customer.getSurname());
            user.setEmail(customer.getEmail());
            user.setEmailVerified(true);
            user.setEnabled(true);

            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(customer.getPassword());
            credentials.setTemporary(false);
            user.setCredentials(Collections.singletonList(credentials));

            Response response = keycloak.realm("eCommerce").users().create(user);

            if (response.getStatus() == 201) {
                log.info("Keycloak User Created: {}", customer.getName());

                String userId = getUserIdByEmail(customer.getEmail());

                String roleName = customer.getRole().name();
                createRoleIfNotExist(roleName);
                assignRoleToUser(userId, roleName);
            } else {
                log.error("Keycloak user is not created. Error code: {}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("An error occurred while creating Keycloak User", e);
        }
    }
    public void createKeycloakAdmin(Admin admin) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(admin.getName());
            user.setFirstName(admin.getName());
            user.setLastName(admin.getSurname());
            user.setEmail(admin.getEmail());
            user.setEmailVerified(true);
            user.setEnabled(true);

            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(admin.getPassword());
            credentials.setTemporary(false);
            user.setCredentials(Collections.singletonList(credentials));

            Response response = keycloak.realm("eCommerce").users().create(user);

            if (response.getStatus() == 201) {
                log.info("Keycloak User Created: {}", admin.getName());

                String userId = getUserIdByEmail(admin.getEmail());

                String roleName = admin.getRole().name();
                createRoleIfNotExist(roleName);
                assignRoleToUser(userId, roleName);
            } else {
                log.error("Keycloak user is not created. Error code: {}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("An error occurred while creating Keycloak User", e);
        }
    }
    public void updateKeycloakEmail(String email, String newEmail) {
        try {
            String userId = getUserIdByEmail(email);

            UserRepresentation user = keycloak.realm("eCommerce")
                    .users()
                    .get(userId)
                    .toRepresentation();

            if (user.getEmail().equals(newEmail)) {
                throw new SameRequestException("The requested email is the same as the current one.");
            }

            user.setEmail(newEmail);
            keycloak.realm("eCommerce").users().get(userId).update(user);

            log.info("Keycloak email successfully updated to '{}'", newEmail);

        } catch (NotFoundException e) {
            log.error("User not found with email: {}", email, e);
            throw new CustomerNotFoundException("User not found in Keycloak.");
        } catch (Exception e) {
            log.error("An error occurred while updating email in Keycloak", e);
        }
    }

    private String getUserIdByEmail(String email) {
        List<UserRepresentation> users = keycloak.realm("eCommerce")
                .users()
                .search(email);

        if (users.isEmpty()) {
            throw new NotFoundException("User not found with email: " + email);
        }
        return users.get(0).getId();
    }

    private void assignRoleToUser(String userId, String roleName) {
        try {
            RoleRepresentation roleRepresentation = keycloak.realm("eCommerce")
                    .roles()
                    .get(roleName)
                    .toRepresentation();

            UserResource userResource = keycloak.realm("eCommerce").users().get(userId);
            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
            log.info("Role '{}' assigned to user '{}'", roleName, userId);
        } catch (Exception e) {
            log.error("Error assigning role '{}' to user '{}':", roleName, userId, e);
        }
    }

    private void createRoleIfNotExist(String roleName) {
        try {
            keycloak.realm("eCommerce").roles().get(roleName).toRepresentation();
            log.info("Role '{}' already exists in Keycloak.", roleName);
        } catch (NotFoundException e) {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            keycloak.realm("eCommerce").roles().create(role);
            log.info("Role '{}' created in Keycloak.", roleName);
        } catch (Exception e) {
            log.error("Error checking/creating role '{}' in Keycloak:", roleName, e);
        }
    }

    public void deleteKeycloakUser(Customer customer) {
        try {
            List<UserRepresentation> users = keycloak.realm("eCommerce")
                    .users()
                    .search(customer.getName());

            if (users.isEmpty()) {
                log.warn("User not found in Keycloak: {}", customer.getName());
                return;
            }
            String userId = users.get(0).getId();

            keycloak.realm("eCommerce")
                    .users()
                    .delete(userId);

        } catch (Exception e) {
            log.error("An error occurred while deleting Keycloak User", e);
        }
    }
}



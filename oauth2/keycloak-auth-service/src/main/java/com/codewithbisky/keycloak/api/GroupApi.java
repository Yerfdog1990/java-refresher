package com.codewithbisky.keycloak.api;

import com.codewithbisky.keycloak.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@Slf4j
public class GroupApi {

    private final GroupService groupService;

    public GroupApi(GroupService groupService) {
        this.groupService = groupService;
    }


    @PutMapping("/{groupId}/assign/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignGroup(@PathVariable String userId, @PathVariable String groupId) {
        groupService.assignGroup(userId, groupId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{groupId}/remove/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unAssignGroup(@PathVariable String userId, @PathVariable String groupId) {
        groupService.deleteGroupFromUser(userId, groupId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

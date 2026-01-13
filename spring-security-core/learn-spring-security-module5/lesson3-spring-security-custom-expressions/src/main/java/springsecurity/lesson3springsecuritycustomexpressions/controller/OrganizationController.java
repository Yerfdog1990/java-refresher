package springsecurity.lesson3springsecuritycustomexpressions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Organization;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.OrganizationRepository;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @PreAuthorize("isMember(#id)")
    @GetMapping( "/organizations/{id}")
    public Organization findOrgById(@PathVariable final long id) {
        return organizationRepository.findById(id)
                .orElse(null);
    }

    //
    //

    @PreAuthorize("hasAuthority('USER_READ_PRIVILEGE')")
    @GetMapping( "/organizations")
    public Organization findOrgByName(@RequestParam final String name) {
        return organizationRepository.findByName(name);
    }
}

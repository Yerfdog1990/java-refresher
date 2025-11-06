package com.baeldung.ls.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectCreatedEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProjectCreatedEventListener.class);
    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent event) {
        LOG.info("New project created with id: {}", event.getProjectId());
    }
}

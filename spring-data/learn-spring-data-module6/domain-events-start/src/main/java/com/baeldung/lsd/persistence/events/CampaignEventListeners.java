package com.baeldung.lsd.persistence.events;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CampaignEventListeners {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignEventListeners.class);

    @TransactionalEventListener(classes = TaskUpdated.class)
    public void taskUpdatedListener(TaskUpdated event) {
        LOG.info("Task(id={}) Updated - Invoke Campaign Application Service Method to "
                + "Process the Event", event.getTaskId());
    }
}


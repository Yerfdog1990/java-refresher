package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Task;
import com.baeldung.lsd.persistence.model.TaskStatus;
import com.baeldung.lsd.persistence.specification.TaskSpecifications;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.lsd.persistence.repository.TaskRepository;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static com.baeldung.lsd.persistence.specification.TaskSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

@SpringBootApplication
public class BuildingQueriesWithSpecificationsApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingQueriesWithSpecificationsApp.class);

    @Autowired
    private TaskRepository taskRepository;

    public static void main(final String... args) {
        SpringApplication.run(BuildingQueriesWithSpecificationsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Task> allTasksInProgressResults = taskRepository.findAll(isTaskInProgress());

        LOG.info("All tasks in progress :");
        allTasksInProgressResults.forEach(t -> LOG.info("{}", t));


        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 6, 30);

        List<Task> allInProgressTasksUnassignedWithinDatesResults = taskRepository.findAll(
                where(isDueDateBetween(fromDate, toDate))
                        .and(isTaskInProgress())
                        .and(isTaskUnassigned())
        );

        LOG.info("All tasks within date range in progress and unassigned  :");
        allInProgressTasksUnassignedWithinDatesResults.forEach(t -> LOG.info("{}", t));

    }

}

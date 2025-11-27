package com.baeldung.lsd.persistence.specification;


import com.baeldung.lsd.persistence.model.Task;
import com.baeldung.lsd.persistence.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecifications {

    public static Specification<Task> isTaskInProgress() {
        return (root, query, builder) -> {
            return builder.equal(root.get("status"), TaskStatus.IN_PROGRESS);
        };
    }


    public static Specification<Task> isDueDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, builder) -> {
            return builder.between(root.get("dueDate"), fromDate, toDate);
        };
    }

    public static Specification<Task> isTaskUnassigned() {
        return (root, query, builder) -> {
            return builder.isNull(root.get("assignee"));
        };
    }

}

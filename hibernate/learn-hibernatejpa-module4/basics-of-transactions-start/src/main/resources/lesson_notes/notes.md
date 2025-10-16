1. Overview
In this lesson, we’ll explore how to manage transactions in JPA and understand how they help maintain data consistency in our applications.

The relevant module we need to import when starting this lesson is: basics-of-transactions-start.

If we want to reference the fully implemented lesson, we can import: basics-of-transactions-end.

2. What Is a Transaction?
   A transaction represents a logical unit of work in which all database operations must either succeed together or, if any part fails, none should be carried out at all, ensuring data consistency.

A transaction can involve a single operation or multiple related operations that need to be executed together as a group.

Transactions guarantee four essential properties, commonly known as ACID:

Atomicity: this ensures that all operations within a transaction are treated as a single unit. The transaction is either fully completed or not executed at all.
Consistency: this property guarantees that the database moves from one valid state to another, preserving all defined rules and constraints.
Isolation: this property aims to ensure that concurrent transactions do not affect each other. Changes made within an active transaction are not visible to others until it is complete.
Durability: this ensures that once a transaction is successfully completed, its effects are permanent, even in the event of a system failure.
Together, these properties provide a foundation for reliable and predictable data operations in any application.

3. Working with Transactions in JPA
Now that we understand what a transaction is, let’s take a practical look at how to manage transactions.

In JPA, we manage transactions using the EntityTransaction API, which we obtain from an EntityManager instance.

Let’s explore the various methods this interface provides that allow us to control the transaction lifecycle.

3.1. Exploring Our Basic Transaction Management
For a clearer demonstration, we’ve added a new method to the DefaultCampaignRepository class in the start module: createCampaignWithTasks(). This method creates a Campaign along with its associated Task entities. While it doesn’t yet handle transactions in a truly ACID-compliant way, it follows the basic requirement of JPA: using transactions simply to allow persistence operations, as we’ve been doing throughout earlier lessons:

@Override
public void createCampaignWithTasks(Campaign campaign, List<Task> tasks) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        entityManager.persist(campaign);
        entityTransaction.commit();

        for (Task task : tasks) {
            task.setCampaign(campaign);

            entityTransaction.begin();
            entityManager.persist(task);
            entityTransaction.commit();
        }
    }
}
Copy
In our method, we obtain an EntityTransaction instance from the EntityManager and use it to manage the database operations. For each persist() operation, we start a new transaction using the begin() method and finalize the changes using commit().

Let’s see this method in action by examining the code existing in our main() method:

CampaignRepository campaignRepository = new DefaultCampaignRepository();
Campaign campaign1 = new Campaign("Campaign 1", "Campaign 1 Name", "Campaign 1 Description");
Task task1 = new Task("Task 1", "Task 1 Description", LocalDate.now(), null, TaskStatus.TO_DO, null);
Task task2 = new Task("Task 2", "Task 2 Description", LocalDate.now(), null, TaskStatus.TO_DO, null);

campaignRepository.createCampaignWithTasks(campaign1, List.of(task1, task2));
logger.info("{} campaign(s) present in the database", campaignRepository.findAll().size());
Copy
Here, we simply create a new Campaign and two associated Task entities and then call our createCampaignWithTasks() method to persist them. At the end, we log the total number of campaign entities present in the database.

Now, let’s execute the main() method to clearly observe the transaction behavior through the generated logs:

Hibernate:
insert
into
Campaign ...
Hibernate:
insert
into
Task ...
Hibernate:
insert
into
Task ...
[main] INFO  com.baeldung.lhj.LhjApp -- 1 campaign(s) present in the database
Copy
As expected, Hibernate executes three separate INSERT statements, one for the campaign and two for the tasks. Additionally, the final log confirms that one campaign is now present in the database.

3.2. Simulating a Failure Within the Transaction
Our current implementation seems to work, but it has a critical flaw. Namely, if our business rules require that a Campaign and all its associated Task entities must be saved together as a single unit, then this approach fails to ensure that. Let’s explore what happens when something goes wrong during the operation.

To simulate a failure, specifically for this lesson, we’ve already added a NOT NULL constraint on the name field of our Task entity:

@Column(nullable = false)
private String name;
Copy
With the nullable attribute set to false, Hibernate will throw a PropertyValueException when we try to persist a Task entity with name as null.

Next, let’s add the following code to our main() method to see what happens when we try to save a nameless Task:

Campaign campaign2 = new Campaign("Campaign 2", "Campaign 2 Name", "Campaign 2 Description");
Task taskWithNoName = new Task(null, "Task Description", LocalDate.now(), null, TaskStatus.TO_DO, null);

try {
campaignRepository.createCampaignWithTasks(campaign2, List.of(taskWithNoName));
} catch (PropertyValueException exception) {
logger.error("{}", exception.getMessage());
}
logger.info("{} campaign(s) present in the database", campaignRepository.findAll().size());
Copy
Here, we create a second Campaign and a single Task with a null name. We wrap the call to our createCampaignWithTasks() method in a try-catch block to handle the expected exception. Finally, we log the total number of Campaign entities in the database again.

Now, let’s run the main() method again and examine the logs:

... logs from previous code
Hibernate:
insert
into
Campaign ...
[main] ERROR com.baeldung.lhj.LhjApp -- not-null property references a null or transient value: com.baeldung.lhj.persistence.model.Task.name
[main] INFO  com.baeldung.lhj.LhjApp -- 2 campaign(s) present in the database
Copy
The logs reveal a serious issue. The INSERT statement for our second campaign was successfully committed. However, the subsequent attempt to persist the task failed due to the NOT NULL database constraint.

This leaves our database in an inconsistent state according to the business rules we’ve established for our solution, and it violates the atomicity property we discussed earlier.

3.3. Implementing Rollback
To fix this issue, we need to make sure that all database operations happen within a single transaction. If anything goes wrong, we want to revert all the changes made so far, a process known as a rollback.

Let’s refactor our createCampaignWithTasks() method to handle this correctly:

@Override
public void createCampaignWithTasks(Campaign campaign, List<Task> tasks) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
EntityTransaction entityTransaction = entityManager.getTransaction();
try {
entityTransaction.begin();
entityManager.persist(campaign);
for (Task task : tasks) {
task.setCampaign(campaign);
entityManager.persist(task);
}
entityTransaction.commit();
} catch (RuntimeException exception) {
if (entityTransaction.isActive()) {
entityTransaction.rollback();
}
throw exception;
}
}
}
Copy
The key difference in our new, improved implementation is that we now wrap all the operations within a single transaction. We begin the transaction once, perform all our persist() operations, and only then do we commit the transaction.

If any exception occurs during this process, we check to see if the transaction is still active and, if it is, we roll it back. This ensures no partial changes are saved, and the atomicity property is met.

Finally, let’s re-run the main() method with the same failing scenario to verify the fix:

... logs from previous code
Hibernate:
insert
into
Campaign ...
[main] ERROR com.baeldung.lhj.LhjApp -- not-null property references a null or transient value: com.baeldung.lhj.persistence.model.Task.name
[main] INFO  com.baeldung.lhj.LhjApp -- 1 campaign(s) present in the database
Copy
This time, although the application still encounters the same error when trying to persist the invalid task, the final log shows that only one campaign is present in the database.

This happens because the transaction for the second campaign is rolled back, and the changes (including the campaign insert) are not persisted.

4. Defining a Utility Class to Manage Transactions
   Now that we’ve fixed our method implementation and understand how to correctly handle transactions, let’s address a new challenge: code redundancy.

As our application grows, we’ll quickly notice that the same transaction-handling logic is repeated across multiple methods.

To avoid this duplication, let’s define a centralized utility class named TransactionUtil inside the com.baeldung.lhj.persistence.util package:

public class TransactionUtil {

    private static final Logger logger = LoggerFactory.getLogger(TransactionUtil.class);
    private final EntityManager entityManager;

    // standard constructor

    public void inTransaction(Consumer<EntityManager> action) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            action.accept(entityManager);
            entityTransaction.commit();
            logger.info("Transaction completed successfully");
        } catch (RuntimeException exception) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            logger.error("Transaction failed", exception);
            throw exception;
        }
    }
}
Copy
Here, we encapsulate the entire transaction lifecycle in our utility class.

The inTransaction() method accepts a Consumer representing the persistence logic we want to execute. The method then takes care of beginning, committing, and rolling back the transaction in case of failure.

This functional approach allows us to pass our persistence logic as a lambda expression. Let’s see it in action by rewriting the same logic from earlier into a new CampaignRepository.createCampaignWithTasksSimplified() method:

public interface CampaignRepository {
// ...

    void createCampaignWithTasksSimplified(Campaign campaign, List<Task> tasks);
}

public class DefaultCampaignRepository implements CampaignRepository {
// ...

    @Override
    public void createCampaignWithTasksSimplified(Campaign campaign, List<Task> tasks) {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            TransactionUtil transactionUtil = new TransactionUtil(entityManager);
            transactionUtil.inTransaction(em -> {
                em.persist(campaign);
                for (Task task : tasks) {
                    task.setCampaign(campaign);
                    em.persist(task);
                }
            });
        }
    }
}
Copy
As we can see, our simplified method is significantly cleaner. It now only contains the core business logic: persisting the Campaign entity and its associated Tasks, while the responsibility of managing the transaction lifecycle is delegated to our helper TransactionUtil class.

5. Using @Transactional in Enterprise Applications
   While our custom TransactionUtil class helps us significantly reduce boilerplate code, in large enterprise Java applications, particularly those built using Spring or Jakarta EE, we can use a more convenient and declarative approach.

We won’t be using this here since our solution is not container-based, but it’s worth knowing that the JPA specification offers the @Transactional annotation.

By annotating a method with @Transactional, we instruct the framework to automatically begin a transaction before the method executes and commit it upon successful completion. If a runtime exception occurs, the framework automatically rolls the transaction back.

This approach abstracts away the manual transaction management code from our methods and reduces boilerplate code without the need to create a custom utility class.

6. Concurrency Considerations
   While transactions are essential for maintaining data consistency, they don’t fully address concurrency challenges that arise when multiple threads or users try to modify or access the same data simultaneously.

Handling concurrent access requires dedicated concurrency control strategies like optimistic or pessimistic locking. These mechanisms work alongside transactions to avoid conflicts and ensure data integrity in multi-user environments.

This topic is beyond the scope of this lesson, but it’s important to be aware of its relevance in real-world applications.
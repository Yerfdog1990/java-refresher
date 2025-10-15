1. Overview
   In this lesson, we’ll explore the Jakarta Persistence Query Language (JPQL), formerly known as Java Persistence Query Language, and learn how to use it to query entities stored in our database.

One quick heads-up: Besides the standard repository methods, we’ve added several new method signatures with empty implementations to our project specifically for this lesson to explore various JPQL features. We’ll introduce them as needed throughout the sections and convert the empty implementations into concrete JPQL queries. The start project also includes a JPQLUnitTest class that creates some test data, so we can focus on the queries.

The relevant module we need to import when starting this lesson is: jpql-start.

To reference the fully implemented lesson, we can import: jpql-end.

2. What Is JPQL?
   JPQL is a query language defined by the JPA specification that enables us to write database queries using Java objects instead of the underlying database tables. We instead reference the database tables and columns through the corresponding entity class and field names, respectively.

The JPA provider, such as Hibernate, translates the written JPQL queries into native SQL queries behind the scenes. This enables us to retrieve and manipulate data in a database-agnostic manner using our object model.

It’s worth noting that Hibernate provides its unique extension to JPQL called HQL (Hibernate Query Language). HQL is a superset of JPQL and predates it, meaning it includes all JPQL features plus additional capabilities.

However, using HQL makes our code more tightly coupled to Hibernate. If we were to switch to a different JPA provider, queries using HQL-specific features would need rewriting. Due to this, it’s recommended to stick to standard JPQL queries whenever possible to ensure portability and avoid vendor lock-in.

We won’t be exploring HQL in this course, but it’s good to be aware of its existence.

3. A Simple Query
   The simplest form of a JPQL query involves selecting all objects of a specific entity type. Let’s begin by implementing the findAll() method in our DefaultCampaignRepository class to retrieve all Campaign entities from the database:

@Override
public List<Campaign> findAll() {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
return entityManager
.createQuery("SELECT c FROM Campaign c", Campaign.class)
.getResultList();
}
}
Copy
Here, we use the createQuery() method of the EntityManager to write our JPQL query that retrieves all Campaign entities. We pass the expected result type, i.e., the Campaign class, as the second argument.

Notice that we use the entity name Campaign and assign it an alias c, which provides a shorthand reference to the Campaign entity within the query.

Finally, we call the getResultList() method to execute the query and retrieve the results as a List of Campaign objects.

Now we can simply call this method in the JPQLUnitTest test class:

@Test
void givenCampaignRepository_whenFindingAllCampaigns_thenListOfCampaignsReturned() {
List campaigns = campaignRepository.findAll();

    assertTrue(campaigns.size() > 0);
}
Copy
4. Filtering Entities With Parameters
   More often than not, we need to retrieve a subset of entities matching certain conditions, rather than fetching all the entities.

Let’s implement the findByStatuses() method in our DefaultTaskRepository class to retrieve tasks matching any of the statuses provided in the method argument:

@Override
public List<Task> findByStatuses(List<TaskStatus> statuses) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
return entityManager
.createQuery("SELECT t FROM Task t WHERE t.status IN (:statuses)", Task.class)
.setParameter("statuses", statuses)
.getResultList();
}
}
Copy
Here, we’re using the WHERE clause to add a condition that filters the Task entities based on their status field.

The IN operator allows us to match the status field against a List of TaskStatus enums. The parentheses around the :statuses parameter are needed as we’re working with a collection-based parameter.

We use the parameter :statuses in the JPQL query, which acts as a placeholder, and provide the actual value within the setParameter() method call.

Let’s see how we can call this method in the test class:

@Test
void givenTaskRepository_whenFindingTasksByStatus_thenListOfTasksReturned() {
List tasks = taskRepository.findByStatuses(List.of(TaskStatus.ON_HOLD));

    assertTrue(tasks.size() > 0);
    assertEquals(tasks.get(0).getStatus(), TaskStatus.ON_HOLD);
}
Copy
This type of query is called a parameterized query.

In JPQL, we can use two types of parameters:

named parameters – prefixed with a colon followed by a name, like :status or :campaignId.
positional parameters – specified using a question mark followed by a number that indicates the parameter’s position (like ?1, ?2)
Using parameterized queries is recommended as it helps prevent SQL injection attacks and improves performance through SQL statement caching.

Throughout the lesson, we’ll continue to use named parameters as they significantly improve the readability and maintainability of our queries.

As a reference, you can find an example using positional parameters in the codebase: the findByCodeAndName() method in the DefaultCampaignRepository class.

5. Joining Entities
   So far, our queries have dealt with a single entity type at a time. However, in real-world applications, we often need to retrieve data from multiple related entities. JPQL allows us to navigate these relationships using JOINS, similar to SQL.

Let’s take a scenario where we want to find all workers who currently have tasks in progress.

We’ll implement this logic in the findWorkersWithActiveTasks() method of our DefaultWorkerRepository class by joining the Worker and Task entities:

@Override
public List<Worker> findWorkersWithActiveTasks() {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
return entityManager
.createQuery("SELECT DISTINCT w FROM Worker w JOIN w.tasks t WHERE t.status = :inProgressStatus", Worker.class)
.setParameter("inProgressStatus", TaskStatus.IN_PROGRESS)
.getResultList();
}
}
Copy
In the above query, we use the JOIN keyword to join the Worker and Task entities on the assignedTasks field.

Additionally, we use the DISTINCT keyword to eliminate duplicate results, as a worker can have multiple active tasks assigned, and we only want to return unique Worker entities.

Let’s call this method in the test class:

@Test
void givenWorkerRepository_whenFindingWorkersWithActiveTasks_thenListOfWorkersReturned() {
List workers = workerRepository.findWorkersWithActiveTasks();

    List<String> workerEmails = workers.stream().map(w -> w.getEmail()).toList();

    assertTrue(workerEmails.contains("active.worker@baeldung.com"));
}
Copy
Alternatively, we can use the dot operator to join entities and access the fields of the related entities directly:

@Override
public List<Task> findByWorkerEmail(String email) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
return entityManager
.createQuery("SELECT t FROM Task t WHERE t.assignee.email = :email", Task.class)
.setParameter("email", email)
.getResultList();
}
}
Copy
Here, we implement the findByWorkerEmail() method in our DefaultTaskRepository class to find all tasks assigned to a worker with a given email.

In the JPQL query, we use the dot operator to access the email field of the Worker entity associated with the Task.

This approach makes the query straightforward and reads almost like we’re traversing object properties. Hibernate automatically joins the Task and Worker tables behind the scenes and applies the specified condition on the email field.

Let’s call this method in the test class:

@Test
void givenTaskRepository_whenFindingTasksByWorkerEmail_thenListOfTasksReturned() {
List tasksByWorkerEmail = taskRepository.findByWorkerEmail("john@test.com");

    assertEquals(1, tasksByWorkerEmail.size());
    assertEquals(tasksByWorkerEmail.get(0).getAssignee().getEmail(),"john@test.com");    
}
Copy
6. Sorting Results
   When displaying data to users, we often need to present it in a specific order. We can use the ORDER BY clause in JPQL to sort the retrieved entities based on one or more fields.

Let’s implement the findAllOrderByFirstName() method in our DefaultWorkerRepository class to retrieve all Worker entities sorted by their first name in ascending order:

@Override
public List<Worker> findAllOrderByFirstName() {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
return entityManager
.createQuery("SELECT w FROM Worker w ORDER BY w.firstName ASC", Worker.class)
.getResultList();
}
}
Copy
Here, we use the ORDER BY clause followed by the firstName field and specify the ASC keyword to sort the results in ascending order. This is the default order, so we can also skip the ASC  keyword if we want.

Let’s call this method in the test class:

@Test
void givenWorkerRepository_whenFindingWorkersInAscNameOrder_thenListOfWorkersReturned() {
List workers = workerRepository.findAllOrderByFirstName();

    List<String> workerNames = workers.stream().map(w -> w.getFirstName()).toList();
    List<String> workerNamesSorted = new ArrayList<String>(workerNames);
    Collections.sort(workerNamesSorted);

    assertIterableEquals(workerNames, workerNamesSorted);
}
Copy
Alternatively, to sort the results in descending order, we use the DESC keyword.

7. Updating and Deleting Entities
   Despite its name, JPQL not only supports querying entities but also allows us to perform UPDATE and DELETE operations on them.

Let’s see an example by implementing the holdTasksByCampaignId() method to update the status of all tasks belonging to a specific campaign to ON_HOLD:

@Override
public int holdTasksByCampaignId(Long campaignId) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
entityManager.getTransaction().begin();
int updatedCount = entityManager
.createQuery("UPDATE Task t SET t.status = :onHoldStatus WHERE t.campaign.id = :campaignId")
.setParameter("onHoldStatus", TaskStatus.ON_HOLD)
.setParameter("campaignId", campaignId)
.executeUpdate();
entityManager.getTransaction().commit();
return updatedCount;
}
}
Copy
Here, we use the UPDATE statement on our Task entity. In the SET clause, we specify the field to update and use the WHERE clause to filter the tasks associated with the provided campaignId.

Additionally, since we’re modifying the state of the database, we wrap our query execution within an active transaction.

Finally, instead of calling the getResultList() method, we now invoke the executeUpdate() method, which executes the query and returns the number of updated records.

Let’s call this method in the test class to verify the number of tasks put on hold:

@Test
void givenATask_whenUpdatingStatus_thenTaskStatusGetsUpdated() {
Optional<Campaign> campaign = campaignRepository.findByCodeAndName("C2", "Campaign 2");
int numberOfUpdatedTasks = taskRepository.holdTasksByCampaignId(campaign.get().getId());

    assertEquals(1, numberOfUpdatedTasks);
}
Copy
Similarly, we can use the DELETE statement to delete records from the database. For reference, we’ve added a deleteCampaignsWithoutTasks() method in the DefaultCampaignRepository class to delete all campaigns that don’t have any associated tasks. The method implementation can be found in this lesson’s end module.


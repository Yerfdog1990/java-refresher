1. Overview
   In this lesson, we’ll explore the Criteria API, a powerful feature of the JPA specification that allows us to build database queries programmatically using Java objects. This approach offers a more object-oriented and type-safe alternative to writing string-based queries.

We’ll find a few empty method stubs in our repository classes. We’ll implement these throughout the lesson using the Criteria API.

To reinforce usage, we’ve added example calls to these repository methods in the main() method of the end module, which we can use as a reference for how to invoke and work with each query.

The relevant module we need to import when starting this lesson is: criteria-api-start.

If we want to reference the fully implemented lesson, we can import: criteria-api-end.

2. Introduction to the Criteria API
   2.1. Building a Basic Criteria Query
   Let’s start by using the Criteria API to retrieve all Campaign entities from the database. For this, we’ll implement the findAll() method in our DefaultCampaignRepository class:

public List<Campaign> findAll() {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Campaign> criteriaQuery = criteriaBuilder.createQuery(Campaign.class);
        Root<Campaign> root = criteriaQuery.from(Campaign.class);
        criteriaQuery.select(root);

        return entityManager
          .createQuery(criteriaQuery)
          .getResultList();
    }
}
Copy
Here, we use the EntityManager to obtain an instance of the CriteriaBuilder class, which is the entry point for constructing queries and their components.

Then, we create a CriteriaQuery object using the createQuery() method, specifying the Campaign class as the expected result type. Next, we obtain a Root object for our Campaign entity by calling the from() method on the CriteriaQuery object. The Root object represents the entity we’re querying.

Finally, we use the root object to invoke the select() method of the criteriaQuery object. Without specifying any filtering, we’re indicating that we want to retrieve all Campaign entities. To execute the query, we pass the criteriaQuery object to the createQuery() method of the EntityManager and then retrieve the result using the getResultList() method.

2.2. Adding Filtering Conditions
While retrieving all entities from a database table is useful, more often than not, we’ll only need to retrieve a subset of entities matching certain conditions. Let’s explore this by implementing the findByNameOrDescriptionContaining() method:

public List<Campaign> findByNameOrDescriptionContaining(String text) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Campaign> criteriaQuery = criteriaBuilder.createQuery(Campaign.class);
        Root<Campaign> root = criteriaQuery.from(Campaign.class);
        criteriaQuery
          .select(root)
          .where(criteriaBuilder.or(
            criteriaBuilder.like(root.get("name"), "%" + text + "%"),
            criteriaBuilder.like(root.get("description"), "%" + text + "%")
          ));
    
        return entityManager
          .createQuery(criteriaQuery)
          .getResultList();
    }
}
Copy
Here, after setting up the CriteriaQuery and Root objects as before, we use the where() method to filter the results, which takes a JPA Predicate as a parameter. In this case, we combine two conditions using the or() method of the CriteriaBuilder to apply a logical OR condition.

Each condition is defined using the like() method, which behaves similarly to the SQL LIKE operator. We pass an Expression, typically an attribute obtained from the Root, along with a pattern string. These predicates check whether the name or description fields of the Campaign entity contain the specified text.

Naturally, if we want to construct a query to use the logical AND instead, we can use the and() method of the CriteriaBuilder. As we’ll see throughout the rest of the lesson, CriteriaBuilder provides various methods to define filtering criteria.

3. Building Dynamic Queries
   One of the primary advantages of the Criteria API is its ability to build dynamic queries. Instead of hardcoding the required conditions in a static query string, we can conditionally add predicates to the query based on runtime conditions, such as user input or application logic.

To demonstrate this, let’s implement the findAndOrderByFields() method, which allows us to filter tasks by a dynamically chosen field and sort them by another field, in either ascending or descending order:

@Override
public List<Task> findAndOrderByFields(String filterField, Object filterValue,
String sortField, boolean sortAscending) {

    try (EntityManager entityManager = JpaUtil.getEntityManager()) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = criteriaQuery.from(Task.class);
     
        Order order;
        if (sortAscending) {
            order = criteriaBuilder.asc(root.get(sortField));
        } else {
            order = criteriaBuilder.desc(root.get(sortField));
        }

        criteriaQuery
          .orderBy(order)
          .select(root)
          .where(criteriaBuilder.equal(root.get(filterField), filterValue));

        return entityManager
          .createQuery(criteriaQuery)
          .getResultList();
    }
}
Copy
Here, we use the filterField parameter to determine which field to filter on and the sortField to specify the sorting field.

We first check the value of sortAscending to decide the sorting direction, and create an appropriate Order object using either asc() or desc(). Then, we configure the query with select(), orderBy(), and where(), applying the filtering condition with equal() to match the specified filterField against the given filterValue. Finally, we execute the query to retrieve the results.

4. Joining Multiple Entities
   We’ve already seen how to retrieve data from a single entity. However, in real-world applications, we often need to work with multiple related entities.

4.1. Implicit Joins
When we need to access attributes from related entities, the Criteria API allows us to navigate through entity relationships using implicit joins.

Let’s look at a simple example where we find all the Tasks assigned to a Worker with a given email:

@Override
public List<Task> findByWorkerEmailImplicitJoin(String email) {

    try (EntityManager entityManager = JpaUtil.getEntityManager()) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = criteriaQuery.from(Task.class);
    
        criteriaQuery
          .select(root)
          .where(criteriaBuilder.equal(root.get("assignee").get("email"), email));

        return entityManager
          .createQuery(criteriaQuery)
          .getResultList();
    }
}
Copy
Here, we implement the findByWorkerEmailImplicitJoin() method of our DefaultTaskRepository class. We use the root.get(“assignee”).get(“email”) to navigate from the Task entity to the Worker entity through the assignee relationship, and then access the email field. This creates an implicit inner join behind the scenes.

4.2. Explicit Joins
While implicit joins are convenient for simple cases, they have limitations when we need more control over the join behavior. For instance, we can’t specify the join type and can’t reuse the same join for multiple conditions.

For these reasons, it’s often better to use explicit joins, which give us full control over how entities are joined.

Let’s implement the same query we looked at in the previous section using an explicit join:

@Override
public List<Task> findByWorkerEmailExplicitJoin(String email) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
Root<Task> root = criteriaQuery.from(Task.class);

        Join<Task, Worker> assigneeJoin = root.join("assignee");
        criteriaQuery
          .select(root)
          .where(criteriaBuilder.equal(assigneeJoin.get("email"), email));

        return entityManager
          .createQuery(criteriaQuery)
          .getResultList();
    }
}
Copy
In the findByWorkerEmailExplicitJoin() method of our DefaultTaskRepository class, we create a Join object using the join() method on the root entity. We pass “assignee” as the argument, which is the name of the field in the Task entity that defines the relationship to the Worker entity.

We then use the assigneeJoin instance to access the email attribute of the Worker entity and build a predicate using equal(). The rest of the process is the same as in previous examples.

5. Using CriteriaUpdate and CriteriaDelete
   In addition to querying entities, the Criteria API also supports bulk update and delete operations through the CriteriaUpdate and CriteriaDelete classes. This is especially useful when we need to update or delete multiple entities based on certain conditions, without loading them individually into memory.

Let’s start with an example of an UPDATE operation. We’ll implement the holdTasksByCampaignId() method in our DefaultTaskRepository class to update the status of all tasks belonging to a specific campaign to ON_HOLD:

@Override
public int holdTasksByCampaignId(Long campaignId) {
try (EntityManager entityManager = JpaUtil.getEntityManager()) {
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaUpdate<Task> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Task.class);
Root<Task> root = criteriaUpdate.from(Task.class);

        criteriaUpdate
          .set(root.get("status"), TaskStatus.ON_HOLD)
          .where(criteriaBuilder.equal(root.get("campaign").get("id"), campaignId));

        entityManager.getTransaction().begin();
        int updatedCount = entityManager.createQuery(criteriaUpdate).executeUpdate();
        entityManager.getTransaction().commit();

        return updatedCount;
    }
}
Copy
Here, instead of createQuery(), we call createCriteriaUpdate() to create a CriteriaUpdate object.

We then use set() to specify the new value for the status attribute of the Task entity, and apply a where() condition to match Tasks by campaign ID.

To execute the update, we use executeUpdate() instead of getResultList(), and we wrap the operation in a transaction since it modifies the database state. This returns the number of updated records.

Similarly, we can perform DELETE operations by calling createCriteriaDelete() to obtain a CriteriaDelete instance for the target entity. For reference, we’ve added a deleteCampaignsWithoutTasks() method in the DefaultCampaignRepository class to delete all campaigns that have no associated tasks. The method implementation can be found in the end module of this lesson.

6. Comparison With JPQL
Now that we’ve explored the Criteria API, let’s compare it with the Jakarta Persistence Query Language (JPQL), which we covered in a previous lesson:


Aspect	Criteria API	JPQL
Type Safety	Queries are constructed using Java objects and methods, allowing the compiler to catch many type-related errors at compile time.	Queries are represented as strings, so type-related issues are only detected at runtime.
Dynamic Queries	Easier to build queries conditionally at runtime.	Requires manipulating query strings, which is more complex and error-prone.
Readability	More verbose and harder to read for simple queries.	More concise and readable for static queries.
While the Criteria API provides better type safety overall, specifying field names using expressions like root.get(“name”) can still lead to runtime errors if the field name is incorrect. For true compile-time safety of field references, we can use the JPA Metamodel API. This is beyond the scope of this lesson, but it’s good to be aware of its existence.

In the end, both JPQL and the Criteria API are translated into native SQL by the JPA provider, and there’s no significant performance difference between them. In practice, it’s common to use both approaches in the same project, choosing the one that best fits the specific use case.
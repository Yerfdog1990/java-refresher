
---

# # **Lesson Notes: Domain Events in Spring — Declarative & Programmatic Approaches**

---

# # **1. Introduction to Domain Events**

Domain Events are a powerful mechanism used in **Domain-Driven Design (DDD)** and modern enterprise applications. They represent **something that happened in the domain that other parts of the system should react to**.

Domain Events allow:

* Loose coupling between components
* Improved modularity
* Cleaner separation between domain logic and side-effects
* Better scalability and integration
* Event-driven and reactive architectures
* Event sourcing / audit trails

Fundamentally, **events are messages representing state changes**, and Spring provides two categories of support:

---

# **⟦ Category 1: Programmatic Events ⟧**

Events created, published, and handled manually.

# **⟦ Category 2: Declarative Domain Events ⟧**

Events automatically fired by Spring Data repositories on entity persistence operations.

---

# # **2. What Is an Event?**

An Event in Spring consists of three parts:

1. **Event Object** – the message or payload
2. **Publisher** – something that fires the event
3. **Listener** – code that reacts to the event

Spring’s event system is built on `ApplicationEventPublisher`, and since Spring 4.2 **events can be any POJO**—they no longer need to extend `ApplicationEvent`.

---

# # **3. Programmatic (Manual) Domain Events in Spring**

This is the classic, explicit event publishing approach.

---

# ## **3.1 Creating a Custom Event**

```java
public class TaskCreatedEvent {
    private final Long taskId;
    private final String name;

    public TaskCreatedEvent(Long taskId, String name) {
        this.taskId = taskId;
        this.name = name;
    }

    public Long getTaskId() { return taskId; }
    public String getName() { return name; }
}
```

✔ No parent class required
✔ Lightweight and flexible

---

# ## **3.2 Publishing Events Programmatically**

A service publishes the event manually:

```java
@Component
public class TaskEventPublisher {

    @Autowired
    private ApplicationEventPublisher publisher;

    public void publish(Task task) {
        publisher.publishEvent(new TaskCreatedEvent(task.getId(), task.getName()));
    }
}
```

Or simply inject `ApplicationEventPublisher` directly into a service.

---

# ## **3.3 Manual Event Publishing Inside a Transaction**

```java
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskEventPublisher eventPublisher;

    @Transactional
    public Task createTask(Task task) {
        Task saved = taskRepository.save(task);

        // publish event AFTER database operation
        eventPublisher.publish(saved);

        return saved;
    }
}
```

---

# ## **3.4 Listening to Programmatic Events**

```java
@Component
public class TaskCreatedListener {

    private static final Logger LOG = LoggerFactory.getLogger(TaskCreatedListener.class);

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        LOG.info("Received TaskCreatedEvent: taskId={}", event.getTaskId());
    }
}
```

---

# ## **3.5 Asynchronous Event Listening**

Enable async:

```java
@Configuration
@EnableAsync
public class AsyncConfig {}
```

Then:

```java
@Async
@EventListener
public void handle(TaskCreatedEvent event) {
    // runs in separate thread
}
```

---

# ## **3.6 Transaction-Bound Event Listeners**

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void afterCommit(TaskCreatedEvent event) {
    // Only invoked after a successful transaction commit
}
```

Other phases:

* BEFORE_COMMIT
* AFTER_ROLLBACK
* AFTER_COMPLETION
* AFTER_COMMIT (default)

---

# # **4. Declarative Domain Events with @DomainEvents**

Spring Data provides a **declarative** mechanism to automatically publish domain events when:

* `save()`
* `saveAll()`
* `delete()`
* `deleteAll()`

are invoked on a repository.

---

# ## **4.1 Declaring a Domain Event Class**

```java
public class TaskUpdated {

    private final Long taskId;

    public TaskUpdated(Task task) {
        this.taskId = task.getId();
    }

    public Long getTaskId() { return taskId; }
}
```

---

# ## **4.2 Publishing Domain Events from an Entity**

```java
@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    // ------------------------------------
    // DECLARATIVE DOMAIN EVENT PUBLISHING
    // ------------------------------------
    @DomainEvents
    public List<Object> domainEvents() {
        return List.of(new TaskUpdated(this));
    }

    @AfterDomainEventPublication
    public void afterPublish() {
        System.out.println("Task Domain Events Published!");
    }
}
```

---

# ## **4.3 Trigger: saving the entity**

```java
taskRepository.save(new Task("Study Spring Data", ...));
```

This automatically:

1. Saves the entity
2. Calls `domainEvents()`
3. Publishes `TaskUpdated`
4. Calls `afterPublish()`

---

# ## **4.4 Listening to Declarative Domain Events**

```java
@Component
public class CampaignEventListeners {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignEventListeners.class);

    @TransactionalEventListener(classes = TaskUpdated.class)
    public void onTaskUpdated(TaskUpdated event) {
        LOG.info("Task {} updated! Trigger Campaign side-effects.", event.getTaskId());
    }
}
```

---

# # **5. Alternative Declarative Approach: AbstractAggregateRoot**

This provides a more DDD-friendly API.

---

# ## **5.1 Using AbstractAggregateRoot**

```java
@Entity
public class Task extends AbstractAggregateRoot<Task> {

    public void updateDescription(String newDesc) {
        this.description = newDesc;
        registerEvent(new TaskUpdated(this));
    }
}
```

---

# ## **5.2 Saving triggers event publishing**

```java
task.updateDescription("New desc");
taskRepository.save(task);  // publishes TaskUpdated event
```

Spring Data handles:

* triggering
* publishing
* clearing events

Automatically.

---

# # **6. Declarative Domain Events — Caveats & Limitations**

### ❌ 1. Only triggered when repository methods are invoked

`save()`, `saveAll()`, `delete()`, `deleteAll()`

→ If an entity is modified inside a transaction **without calling save()**, the Domain Events are **NOT** published.

### ❌ 2. Works only with Spring Data repositories

### ❌ 3. Events can be lost

If an exception occurs during event publishing, there is **no retry and no backpressure**.

### ❌ 4. Local only

Events stay **in the JVM**.
For distributed microservices, you must forward to:

* Kafka
* RabbitMQ
* SNS/SQS
* EventBridge

### ❌ 5. Not always suitable for CQRS or Event Sourcing

Requires custom infrastructure.

---

# # **7. Programmatic vs Declarative Domain Events**

Below is a guided comparison.

---

# # **8. Detailed Comparison Table**

| Aspect                         | Declarative (`@DomainEvents`)                           | Programmatic (`ApplicationEventPublisher`) |
| ------------------------------ | ------------------------------------------------------- | ------------------------------------------ |
| **Trigger Point**              | Automatically on repository calls                       | Manual: anywhere in the code               |
| **Event Definition**           | Domain-focused                                          | Infrastructure-focused                     |
| **Coupling**                   | Very low, but tied to Spring Data                       | Slightly higher, but tech-agnostic         |
| **Flexibility**                | Limited to save/delete operations                       | Fully flexible                             |
| **Control**                    | Low — automatic                                         | High — explicit publishing logic           |
| **DDD Alignment**              | Very good (Aggregate events)                            | Also good, but more boilerplate            |
| **Publishing Location**        | Entity class                                            | Service / application layer                |
| **Supports External Brokers?** | No (must forward manually)                              | Yes (typically used for messaging)         |
| **Transactional support**      | Built-in                                                | Must use `@TransactionalEventListener`     |
| **When events fire**           | Only after repository interaction                       | Anytime you choose                         |
| **Async support**              | Use `@Async` on listeners                               | Same                                       |
| **Good for**                   | Event sourcing inside the domain, automatic propagation | Integrations, messaging systems, workflows |
| **Not good for**               | Complex multi-stage workflows                           | Simple CRUD side-effect notifications      |

---

# # **9. When to Use Declarative Domain Events**

Use `@DomainEvents` when:

* Events are pure domain notifications
* You already use Spring Data repositories extensively
* You want domain-driven side-effects (Campaign reacting to Task)
* You want cleaner code inside entity models
* Event publishing should occur only when persistence occurs

---

# # **10. When to Use Programmatic Events**

Choose manual publishing when:

* You need full control over publishing time
* Events must be sent to **external systems**
* Events must fire **before** save, after save, or independently of save
* You don't use Spring Data repositories
* You want to combine transactions, messaging, workflows

---

# # **11. Full End-to-End Example: Both Approaches**

---

# ## **11.1 Declarative Example Summary**

### **Entity**

```java
@Entity
public class Task {

    @DomainEvents
    public List<Object> domainEvents() {
        return List.of(new TaskUpdated(this));
    }

    @AfterDomainEventPublication
    public void afterPublish() {}
}
```

### **Listener**

```java
@TransactionalEventListener
public void onTaskUpdated(TaskUpdated event) {}
```

---

# ## **11.2 Programmatic Example Summary**

### **Publisher**

```java
publisher.publishEvent(new TaskUpdated(task));
```

### **Listener**

```java
@EventListener
public void onTaskUpdated(TaskUpdated event) {}
```

---

# # **12. Final Summary**

Domain Events are essential for:

* decoupling domain logic
* building reactive and event-driven systems
* coordinating multiple Aggregates
* integrating modular and external systems

Spring gives you two models:

---

# **Declarative Events → Best for Domain-Driven Design**

* Automatic event publishing via Spring Data
* Cleaner aggregates
* Less boilerplate

---

# **Programmatic Events → Best for Application Workflows & Messaging**

* Maximum control
* Works outside Spring Data
* Ideal for distributed systems

---


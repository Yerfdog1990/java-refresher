# Discover a REST API — HATEOAS

## 1. Goal

Learn to build a hypermedia-driven RESTful API using Spring HATEOAS. HATEOAS (Hypermedia as the Engine of Application State) is the highest level of RESTful maturity — the API response includes links (hypermedia) that tell the client what actions are available, making the API self-describing and navigable.

In Spring, this is called **discoverability** — the API response itself provides all the information needed to navigate or operate on resources.

---

## 2. What is HATEOAS?

HATEOAS is a constraint of REST architecture where the client interacts with the API entirely through hypermedia provided dynamically by the server. Rather than the client needing to know all URLs in advance, the API responses include links to related or available actions.

---

## 3. Core Abstractions

| Abstraction | Role |
|---|---|
| `Link` | Stores the URI of a resource and its relationship to the current one. Generated via `WebMvcLinkBuilder`. |
| `LinkRelation` | Describes the relationship with the target resource (e.g., `self`, `collection`). Standard IANA relations are used. |
| `RepresentationModel` | Base class for DTOs. Provides the `.add(Link)` method to attach hypermedia links to a response. |

---

## 4. Setup

Add the HATEOAS starter to `pom.xml`. This auto-enables hypermedia support and configures HAL as the default media type (`application/hal+json`).

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

To override the media type, use `@EnableHypermediaSupport` in a config class:

```java
@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class RwsbApp implements ApplicationRunner { ... }
```

---

## 5. Three Approaches to Implementing HATEOAS

---

### Approach 1 — `RepresentationModel`: DTO Extends Base Class

**Used by:** `TaskController` & `TaskDto`

The simplest approach. Your DTO extends `RepresentationModel<T>`, which gives it a `.add(Link)` method. Links are built using `WebMvcLinkBuilder.linkTo()` and `methodOn()`, then added directly to the DTO.

#### Step 1 — DTO extends RepresentationModel

```java
@Relation(collectionRelation = "taskList", itemRelation = "task")
public class TaskDto extends RepresentationModel<TaskDto> {
    private Long id;
    private String name;
    // ... other fields, constructor, getters/setters
}
```

#### Step 2 — Generate links in the controller

Two static helper methods generate links using `WebMvcLinkBuilder`:

```java
// Item self-link: points to GET /tasks/{id}
private static Link generateTaskSelfLink(TaskDto dto) {
    return linkTo(methodOn(TaskController.class).findOne(dto.getId()))
             .withSelfRel();
}

// Collection self-link: points to GET /tasks?name=...&assigneeId=...
private static Link generateTaskCollectionSelfLink(String name, Long assigneeId) {
    return linkTo(methodOn(TaskController.class).searchTasks(name, assigneeId))
             .withSelfRel();
}
```

**How `WebMvcLinkBuilder` works:**

1. `methodOn(TaskController.class).findOne(id)` creates a proxy of the controller and calls the target method, capturing the route mapping.
2. `linkTo(...)` wraps this proxy call and creates a `WebMvcLinkBuilder` pointing to that route.
3. `.withSelfRel()` produces the final `Link` object with relation `"self"`. Use `.withRel("custom")` for named relations.

#### Step 3 — Add link in the Mapper (toDto)

Links are added inside the Mapper so every DTO gets a self-link regardless of which endpoint returns it:

```java
public static TaskDto toDto(Task model) {
    if (model == null) return null;
    TaskDto dto = new TaskDto(model.getId(), ...);
    return dto.add(generateTaskSelfLink(dto));  // .add() from RepresentationModel
}
```

#### Step 4 — Endpoints: Location header + body links

```java
// POST: 201 + Location header + body with _links
@PostMapping
public ResponseEntity<TaskDto> create(@RequestBody @Valid TaskDto newTask) {
    Task createdModel = this.taskService.save(Mapper.toModel(newTask));
    TaskDto dto = Mapper.toDto(createdModel);
    return ResponseEntity.created(generateTaskSelfLink(dto).toUri())
                         .body(dto);     // body already has _links via Mapper
}

// GET collection: wraps list in CollectionModel with its own self-link
@GetMapping
public CollectionModel<TaskDto> searchTasks(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) Long assigneeId) {
    List<TaskDto> taskDtos = taskService.searchTasks(name, assigneeId)
        .stream().map(Mapper::toDto).collect(Collectors.toList());
    return CollectionModel.of(taskDtos)
             .add(generateTaskCollectionSelfLink(name, assigneeId));
}
```

#### Example Response — POST /tasks → 201 Created

```json
{
  "id": 42,
  "name": "Design landing page",
  "status": "PENDING",
  "_links": {
    "self": { "href": "/tasks/42" }
  }
}
```
```
Location: /tasks/42   (header)
Content-Type: application/hal+json
```

> The `@Relation` annotation controls the JSON field names for HAL collections. Without it, the list appears under `"taskDtoList"` — with it, it appears under `"taskList"`.

---

### Approach 2 — `EntityModel` & `CollectionModel`: Wrapper Approach

**Used by:** `CampaignController` & `CampaignDto`

Use `EntityModel<T>` when you don't want (or can't) extend `RepresentationModel` directly — for example, if you don't control the DTO class. `EntityModel` and `CollectionModel` are subclasses of `RepresentationModel` that act as wrappers.

- **`CampaignDto`** — does not extend `RepresentationModel`. It stays clean. HATEOAS wrapping happens outside it via `EntityModel.of(dto, link)`.
- **`CampaignController`** — controller methods return `EntityModel<CampaignDto>` or `CollectionModel<EntityModel<CampaignDto>>` instead of bare DTOs.

#### Link generators

```java
private static Link generateCampaignSelfLink(CampaignDto dto) {
    return linkTo(methodOn(CampaignController.class).findOne(dto.getId())).withSelfRel();
}

private static Link generateCampaignCollectionSelfLink() {
    return linkTo(methodOn(CampaignController.class).listCampaigns()).withSelfRel();
}
```

#### Mapper — wrapping with EntityModel

```java
public static EntityModel<CampaignDto> toDto(Campaign model) {
    if (model == null) return null;
    Set<TaskDto> tasks = model.getTasks().stream()
        .map(TaskController.Mapper::toDto).collect(Collectors.toSet());
    CampaignDto dto = new CampaignDto(model.getId(), ...);
    return EntityModel.of(dto, generateCampaignSelfLink(dto));  // wraps DTO
}
```

#### Controller endpoints

```java
// GET single: returns EntityModel
@GetMapping("/{id}")
public EntityModel<CampaignDto> findOne(@PathVariable Long id) {
    Campaign model = campaignService.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return Mapper.toDto(model);   // already an EntityModel
}

// GET collection: returns CollectionModel of EntityModels
@GetMapping
public CollectionModel<EntityModel<CampaignDto>> listCampaigns() {
    List<EntityModel<CampaignDto>> dtos = campaignService.findCampaigns()
        .stream().map(Mapper::toDto).collect(Collectors.toList());
    return CollectionModel.of(dtos)
             .add(generateCampaignCollectionSelfLink()); // collection gets its own _links
}

// POST: 201 + Location + EntityModel body
@PostMapping
public ResponseEntity<EntityModel<CampaignDto>> create(@RequestBody @Valid CampaignDto newCampaign) {
    EntityModel<CampaignDto> dto = Mapper.toDto(campaignService.save(Mapper.toModel(newCampaign)));
    return ResponseEntity.created(generateCampaignSelfLink(dto.getContent()).toUri())
                         .body(dto);
}
```

#### Example Response — GET /campaigns → 200 OK

```json
{
  "_embedded": {
    "campaignList": [
      {
        "id": 1,
        "code": "SUMMER24",
        "_links": { "self": { "href": "/campaigns/1" } }
      }
    ]
  },
  "_links": {
    "self": { "href": "/campaigns" }
  }
}
```

> When using `EntityModel`, the raw DTO is accessed via `entityModel.getContent()`. This is needed in the controller when generating the Location URI for the POST endpoint.

---

### Approach 3 — `RepresentationModelAssembler`: Assembler Pattern

**Used by:** `WorkerController` & `WorkerDto`

The assembler pattern centralises all link-building logic in a dedicated `RepresentationModelAssembler` class. The assembler converts a domain model (`Worker`) into a DTO (`WorkerDto`) with links already attached, and also handles collection conversion automatically.

#### WorkerDto — extends RepresentationModel

```java
@Relation(collectionRelation = "workerList", itemRelation = "worker")
public class WorkerDto extends RepresentationModel<WorkerDto> {
    private Long id;
    private String email;
    // firstName, lastName, constructors, getters...
}
```

#### WorkerModelAssembler — inner static class

```java
public static class WorkerModelAssembler
    extends RepresentationModelAssemblerSupport<Worker, WorkerDto> {

    public WorkerModelAssembler() {
        // tells Spring: this assembler converts Worker → WorkerDto
        // and self-links point to WorkerController
        super(WorkerController.class, WorkerDto.class);
    }

    @Override
    public WorkerDto toModel(Worker worker) {
        if (worker == null) return null;
        // createModelWithId: calls instantiateModel() then adds self-link /workers/{id}
        return this.createModelWithId(worker.getId(), worker);
    }

    @Override
    protected WorkerDto instantiateModel(Worker worker) {
        return Mapper.toDto(worker);  // create the DTO (without links)
    }
}
```

**How `RepresentationModelAssemblerSupport` works:**

1. The `super(WorkerController.class, WorkerDto.class)` constructor registers the controller and DTO type so the assembler knows how to construct self-links automatically.
2. `createModelWithId(id, worker)` calls `instantiateModel()` to build the DTO, then automatically adds a self-link pointing to `GET /workers/{id}`.
3. `toCollectionModel(Iterable<W>)` is inherited — it calls `toModel()` for each entity and wraps everything in a `CollectionModel` with a self-link to the collection endpoint.

#### Controller — uses the assembler for all operations

```java
@RestController
@RequestMapping("/workers")
public class WorkerController {

    private WorkerModelAssembler assembler = new WorkerModelAssembler();

    @GetMapping("/{id}")
    public WorkerDto findOne(@PathVariable Long id) {
        Worker model = workerService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return assembler.toModel(model);        // DTO + self-link
    }

    @GetMapping
    public CollectionModel<WorkerDto> listWorkers() {
        List<Worker> models = workerService.findWorkers();
        return assembler.toCollectionModel(models); // handles collection automatically
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkerDto create(@RequestBody @Valid WorkerDto newWorker) {
        Worker created = workerService.save(Mapper.toModel(newWorker));
        return assembler.toModel(created);
    }

    @PutMapping("/{id}")
    public WorkerDto update(@PathVariable Long id,
                            @RequestBody @Validated(WorkerUpdateValidationData.class) WorkerDto updatedWorker) {
        Worker created = workerService.updateWorker(id, Mapper.toModel(updatedWorker))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return assembler.toModel(created);
    }
}
```

#### Example Response — GET /workers → 200 OK

```json
{
  "_embedded": {
    "workerList": [
      {
        "id": 5,
        "email": "alice@example.com",
        "_links": { "self": { "href": "/workers/5" } }
      }
    ]
  },
  "_links": { "self": { "href": "/workers" } }
}
```

---

## 6. Comparison — Which Approach to Use?

| Aspect | Approach 1 — RepresentationModel | Approach 2 — EntityModel | Approach 3 — Assembler |
|---|---|---|---|
| DTO coupling | DTO extends `RepresentationModel` | DTO stays clean (no extends) | DTO extends `RepresentationModel` |
| Return type | `TaskDto` / `CollectionModel<TaskDto>` | `EntityModel<CampaignDto>` | `WorkerDto` / `CollectionModel<WorkerDto>` |
| Link logic location | Mapper's `toDto()` + controller | Mapper's `toDto()` | Centralised in Assembler class |
| Collection handling | Manual with `CollectionModel.of()` | Manual with `CollectionModel.of()` | Automatic via `toCollectionModel()` |
| Best for | DTOs you own and want inline link control | Third-party or clean DTOs | Reusable assembler across controllers |
| Code volume | Low | Low | Medium (assembler class overhead) |

---

## 7. Key Annotations

| Annotation | Purpose |
|---|---|
| `@Relation(collectionRelation, itemRelation)` | Controls the HAL JSON field names. Without it, Spring uses the class name (e.g. `taskDtoList`). With it: `taskList` for collections and `task` for single items. |
| `@EnableHypermediaSupport(type=)` | On the application class. Enables hypermedia support. HAL is the default so usually omitted. |

---

## 8. Summary

| | Approach 1 — RepresentationModel | Approach 2 — EntityModel | Approach 3 — Assembler |
|---|---|---|---|
| DTO | extends `RepresentationModel` | stays plain | extends `RepresentationModel` |
| Link attachment | `.add(link)` in Mapper | `EntityModel.of(dto, link)` | `createModelWithId()` in assembler |
| Collection | `CollectionModel.of()` manually | `CollectionModel.of()` manually | `toCollectionModel()` automatically |
| Example classes | `TaskDto` / `TaskController` | `CampaignDto` / `CampaignController` | `WorkerDto` / `WorkerController` |

> **Important:** The `Location` header (HTTP 201) is also hypermedia — it uses the same `Link` object via `link.toUri()`. HATEOAS isn't limited to the response body: headers, content-type (`application/hal+json`), and body `_links` are all part of the hypermedia contract.

---
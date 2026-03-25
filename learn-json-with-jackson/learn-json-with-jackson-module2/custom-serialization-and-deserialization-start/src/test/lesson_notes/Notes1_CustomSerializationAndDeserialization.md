### Custom Serialization and Deserialization with Jackson — Notes for Beginners

When you use Jackson to turn Java objects into JSON (serialization) or turn JSON back into Java objects (deserialization), it normally looks at your class fields and writes them all out.  
Sometimes you want something different — for example, instead of writing the whole `Campaign` object as `{ "code":"C1", "name":"Campaign one" }`, you just want the string `"C1"`.

That’s where **custom serialization and deserialization** come in.

#### 1. The Big Picture

Jackson’s main tool is `ObjectMapper` (or the newer `JsonMapper`).  
Think of `ObjectMapper` as a post office:

- **Serializer** = a clerk who takes your Java object and writes it as JSON
- **Deserializer** = a clerk who reads JSON and builds a Java object

Jackson already has clerks for common types (`String`, `int`, `List`, `LocalDate`, etc.).  
When it sees a class you wrote (like `Campaign`), it uses its default clerk, which just writes every field.

If you want different behavior, you create your own clerk and tell Jackson to use it.

You can do that in two ways:
1. **Global way** – Put your custom clerk into a `SimpleModule` and register that module with the `JsonMapper`. Every `Campaign` anywhere in your app will use it.
2. **Local way** – Put an annotation on just one field (`@JsonSerialize` / `@JsonDeserialize`). Only that field uses your custom clerk.

#### 2. Creating a Custom Serializer

A serializer’s job is to output JSON.

**Step 2.1 – Write the serializer class**

Instead of implementing the whole `JsonSerializer` interface, extend `StdSerializer`. It handles the boring boilerplate for you.

```java
public class CampaignToCodeSerializer extends StdSerializer<Campaign> {
    // Tell Jackson this serializer works for Campaign
    public CampaignToCodeSerializer() {
        super(Campaign.class);
    }

    @Override
    public void serialize(Campaign value, JsonGenerator gen, SerializationContext ctxt) 
            throws JacksonException {
        // value is the Campaign object we’re writing
        // gen is the tool that writes JSON pieces
        gen.writeString(value.getCode());   // write only the code, as a JSON string
    }
}
```

- `value` – the actual `Campaign` object Jackson is trying to write
- `gen` – the writer; `writeString("C1")` produces `"C1"` in the output
- `ctxt` – context; you can ask it for other serializers if you need them

**Step 2.2 – Register the serializer**

You don’t attach serializers directly to the mapper. You bundle them in a `SimpleModule`.

```java
@Test
void givenTask_whenSerializing_thenCustomSerializerUsed() {
    // 1. Create a module and put our serializer inside it
    SimpleModule module = new SimpleModule();
    module.addSerializer(Campaign.class, new CampaignToCodeSerializer());

    // 2. Build a JsonMapper that knows about this module
    JsonMapper mapper = JsonMapper.builder()
            .addModule(module)
            .enable(SerializationFeature.INDENT_OUTPUT) // pretty-print JSON
            .build();

    // 3. Create a Task that contains a Campaign
    Campaign campaign = new Campaign("C1", "Campaign one", "This is Campaign one");
    Task task = new Task("T1", "Task one", "This is Task one",
                         LocalDate.of(2050, 1, 1), TaskStatus.TO_DO, campaign);

    // 4. Convert to JSON
    String json = mapper.writeValueAsString(task);

    // 5. Verify
    assertTrue(json.contains("\"campaign\" : \"C1\""));
    assertFalse(json.contains("\"name\" : \"Campaign one\""));

    System.out.println(json);
}
```

Result:
```json
{
  "code" : "T1",
  "name" : "Task one",
  "description" : "This is Task one",
  "dueDate" : [ 2050, 1, 1 ],
  "status" : "TO_DO",
  "campaign" : "C1"
}
```
Notice `campaign` is just `"C1"` — not the full object.

#### 3. Creating a Custom Deserializer

A deserializer’s job is to read JSON and build an object.

**Step 3.1 – Write the deserializer class**

Extend `StdDeserializer`.

```java
public class CodeToCampaignDeserializer extends StdDeserializer<Campaign> {
    public CodeToCampaignDeserializer() {
        super(Campaign.class);
    }

    @Override
    public Campaign deserialize(JsonParser p, DeserializationContext ctxt) 
            throws JacksonException {
        // p is the JSON reader; p.getText() reads the current string value "C1"
        String code = p.getText();
        // We only have the code, so we create a Campaign with null name/description
        return new Campaign(code, null, null);
    }
}
```

- `p` – the reader that is positioned on the JSON value `"C1"`
- `ctxt` – context; you can use it to read nested objects or report errors

**Step 3.2 – Register the deserializer**
```java
@Test
void givenJsonWithCustomStatus_whenDeserializing_thenCustomDeserializerUsed() {
    // 1. Create a module and put our deserializer inside it
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Campaign.class, new CodeToCampaignDeserializer());

    // 2. Build the mapper
    JsonMapper mapper = JsonMapper.builder()
            .addModule(module)
            .build();

    // 3. JSON where campaign is just a string
    String json = """
          {
          "code" : "T1",
          "name" : "Task one",
          "description" : "This is Task one",
          "dueDate" : [ 2050, 1, 1 ],
          "status" : "TO_DO",
          "campaign" : "C1"
        }
        """;

    // 4. Convert JSON back to a Task
    Task task = mapper.readValue(json, Task.class);

    // 5. Verify
    assertNotNull(task.getCampaign());
    assertEquals("C1", task.getCampaign().getCode());
    assertNull(task.getCampaign().getName());
    assertNull(task.getCampaign().getDescription());
}
```

#### 4. Using Annotations (Field-Level Control)

Registering a module affects **every** `Campaign` in your application.  
If you only want this behavior for the `campaign` field inside `Task`, use annotations on that field.

```java
public class Task {
    // other fields...

    @JsonSerialize(using = CampaignToCodeSerializer.class)
    @JsonDeserialize(using = CodeToCampaignDeserializer.class)
    private Campaign campaign;
}
```

Now you don’t need to add the module to the mapper — the annotation tells Jackson what to use.

**Serialization test with annotations**
```java
@Test
void givenTask_whenSerializing_thenAnnotationSerializerUsed() {
    JsonMapper mapper = JsonMapper.builder().build(); // no module added

    Campaign campaign = new Campaign("C1", "Campaign one", "This is Campaign one");
    Task task = new Task("T1", "Task one", "This is Task one",
                         LocalDate.of(2050, 1, 1), TaskStatus.TO_DO, campaign);
    String json = mapper.writeValueAsString(task);

    assertTrue(json.contains("\"campaign\":\"C1\""));
    assertFalse(json.contains("\"name\":\"Campaign 1\""));

    System.out.println(json);
}
```

**Deserialization test with annotations**
```java
@Test
void givenJsonWithCustomCampaign_whenDeserializing_thenAnnotationDeserializerUsed() {
    JsonMapper mapper = JsonMapper.builder().build();

    String json = """
          {
          "code" : "T1",
          "name" : "Task one",
          "description" : "This is Task one",
          "dueDate" : [ 2050, 1, 1 ],
          "status" : "TO_DO",
          "campaign" : "C1"
        }
        """;
    Task task = mapper.readValue(json, Task.class);

    assertNotNull(task.getCampaign());
    assertEquals("C1", task.getCampaign().getCode());
    assertNull(task.getCampaign().getName());
    assertNull(task.getCampaign().getDescription());
}
```

#### 5. Things to Keep in Mind (Best Practices)

**Don’t put business logic inside**

Your serializer/deserializer should only map data. Don’t call a database or a service from inside it — that mixes data conversion with business work and can slow things down.

**Global vs. Local**

- Module = global. Every `Campaign` in the whole app uses your custom code.
- Annotation = local. Only the field you annotated uses your custom code.

**Be ready for bad input**

JSON might be missing a value or have the wrong type. Your deserializer should check for `null` or empty strings so the whole app doesn’t crash.

**Make it symmetric**

If you serialize a `Campaign` as `"C1"`, your deserializer should be able to take `"C1"` and create a `Campaign`. If they don’t match, you’ll get data loss.

**Avoid infinite recursion**

If your custom serializer calls `gen.writeObject(value)` on the same object, Jackson will call your serializer again forever. Only write the part you want.

That’s it — you now know how to tell Jackson exactly how to write and read your own classes.

---
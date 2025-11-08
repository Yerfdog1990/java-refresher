
---

## **Spring Boot with Docker**

---

### **1. Introduction**

Spring Boot simplifies application deployment by integrating seamlessly with **Docker**.
Docker allows you to package your Spring Boot application and its dependencies into a lightweight, portable **container image** that can run anywhere.

Before Spring Boot 2.3, developers had to manually write and maintain a **Dockerfile** for building images.
Since **Spring Boot 2.3**, the **Spring Boot Maven Plugin** provides built-in support for creating Docker images more efficiently — without manually managing all Dockerfile layers.

---

### **2. The Spring Boot Plugin and Docker Integration**

The Spring Boot Maven plugin includes two major Docker-related features:

1. **Layertools** – allows inspection and extraction of layers from a Spring Boot JAR.
2. **Cloud Native Buildpacks Integration** – automatically builds Docker-compatible **OCI images** directly from the command line.

These features simplify image creation, enable caching of layers, and reduce image rebuild time.

---

### **3. Building a Docker Image from the Command Line**

To build an image, Docker must be installed locally.

**Step 1: Clean and build the project**

```bash
mvn clean install
```

**Step 2: Use the plugin goal to build the image**

```bash
mvn spring-boot:build-image
```

**Expected Output Example:**

```
[INFO] Building image 'docker.io/library/spring-boot-with-docker:0.1.0-SNAPSHOT'
```

This creates a Docker image named `spring-boot-with-docker` tagged with version `0.1.0-SNAPSHOT`.

---

### **4. Building an Image During Artifact Packaging**

To automate image building during the Maven build phase, add the `build-image` goal to your `pom.xml` configuration.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>com.baeldung/project-api:${project.version}</name>
                </image>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>build-image</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Now, simply run:

```bash
mvn package
```

This command will:

* Package your Spring Boot app into a JAR.
* Build and tag the Docker image automatically.

---

### **5. Exploring and Running the Docker Image**

After building, check available Docker images:

```bash
docker images
```

**Run the container:**

```bash
docker container run -d --name project-api -p 8080:8080 com.baeldung/project-api:0.1.0-SNAPSHOT
```

**Check running containers:**

```bash
docker container ps
```

**View logs:**

```bash
docker logs project-api
```

**Test endpoint:**

Visit [http://localhost:8080/projects](http://localhost:8080/projects)
→ The application should respond successfully.

---

### **6. Layered JARs**

A **Docker image** is composed of **layers**. Each layer can be cached and reused to speed up builds.

Spring Boot helps organize your JAR into **four logical layers**:

| Layer                   | Description                           |
| ----------------------- | ------------------------------------- |
| `dependencies`          | External dependencies (rarely change) |
| `spring-boot-loader`    | Spring Boot loader classes            |
| `snapshot-dependencies` | Dependencies that may change often    |
| `application`           | Your application code and resources   |

To list the layers:

```bash
java -Djarmode=layertools -jar target/spring-boot-with-docker-start-0.1.0-SNAPSHOT.jar list
```

Output:

```
dependencies
spring-boot-loader
snapshot-dependencies
application
```

This structure enables **layer caching**, meaning Docker rebuilds only the changed layer when the application changes.

---

### **7. Using Layertools in a Dockerfile**

Example Dockerfile:

```dockerfile
FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

**Multi-stage builds** (Docker v17+) are used here to:

* Extract layers using layertools in the builder stage.
* Copy only necessary files into the final image, minimizing image size.

**Build and Run Commands:**

```bash
docker build -f docker/Dockerfile . --tag com.baeldung/project-api
docker container run --name project-api -p 8080:8080 com.baeldung/project-api:latest
```

If changes are made to your code and you rebuild, Docker will reuse cached layers for everything except the updated `application` layer — making the rebuild much faster.

---

### **8. Packaging Executable Archives**

The **Spring Boot Maven Plugin** also repackages JARs/WARs into **executable archives**, allowing you to run them directly with:

```bash
java -jar target/myapp.jar
```

Example configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

### **9. Layered JARs and Custom Configuration**

Spring Boot creates layered JARs automatically.
To disable layers, use:

```xml
<configuration>
    <layers>
        <enabled>false</enabled>
    </layers>
</configuration>
```

To define custom layers, create a `layers.xml` configuration file and specify your preferred order or custom inclusions/exclusions.

---

### **10. Summary**

| Concept                     | Description                                        |
| --------------------------- | -------------------------------------------------- |
| **Layertools**              | Used to inspect and extract layers from a JAR      |
| **Buildpacks**              | Automates building OCI-compliant images            |
| **Layered JARs**            | Speeds up Docker builds by caching unchanged parts |
| **spring-boot:build-image** | Builds Docker images without a Dockerfile          |
| **spring-boot:repackage**   | Creates executable archives for deployment         |

---

### **11. Visual Summary: Spring Boot + Docker Workflow**

```plaintext
+-----------------------+
| Spring Boot App (JAR) |
+----------+------------+
           |
           v
+-------------------------+
| Spring Boot Plugin      |
| - build-image           |
| - layertools            |
+-----------+-------------+
            |
            v
+-------------------------+
| Docker Image (Layers)   |
| - dependencies          |
| - loader                |
| - snapshot-dependencies |
| - application           |
+-----------+-------------+
            |
            v
+----------------------+
| Container (Runtime)  |
| - Port 8080:8080     |
+----------------------+
```

---


# 🐳 Docker Integration with Selenium Grid

---

## 1. What is Docker?

Docker is a platform that uses **containerization** — a lightweight alternative to virtual machines. Instead of creating full OS-level VMs, Docker creates isolated containers that share the host OS kernel.

### Evolution of Infrastructure

| Era | Technology | Problem |
|-----|-----------|---------|
| Old Days | Physical Machines | Very costly, hard to manage |
| Mid Era | Virtual Machines (VMs) | Still costly, performance impact |
| Modern Era | Docker Containers | Lightweight, fast, free, scalable |

### Why Docker for Testing?
- No need to install browsers or WebDriver on your local machine
- Start and stop browser environments on demand
- Containers are isolated — no conflicts between environments
- Selenium team publishes ready-made Docker images for Grid setup

---

## 2. Core Docker Concepts

### Docker Hub
Docker Hub (`hub.docker.com`) is the official online repository of Docker images — like an app store for container images.
- Anyone can **push** (upload) or **pull** (download) images
- Selenium team has published official images for Grid setup
- Free for personal/student use

### Images vs Containers

| Concept | Explanation |
|---------|-------------|
| **Image** | A blueprint/template — like a downloaded `.exe` installer file |
| **Container** | A running instance created from an image — like the installed app |
| **Relationship** | One image → can create multiple containers |
| `docker pull` | Downloads the image from Docker Hub |
| `docker run` | Creates a container from an image (downloads it first if needed) |

> 💡 **Analogy:** Image = `.exe` installer file. Container = the running application after installation.

---

## 3. Essential Docker Commands

### 3a — Basic / System Commands

| Command | What It Does |
|---------|-------------|
| `docker version` | Shows client & server Docker version |
| `docker -v` | Short version number only |
| `docker info` | Detailed info about Docker installation |
| `docker --help` | Lists all available Docker commands |
| `docker login` | Log in to Docker Hub account |
| `docker stats` | Live CPU/memory usage of running containers |
| `docker system df` | Disk usage by Docker images/containers |
| `docker system prune -f` | Force-remove ALL stopped containers/networks |

### 3b — Image Commands

| Command | What It Does | Example |
|---------|-------------|---------|
| `docker images` | List all downloaded images on system | `docker images` |
| `docker pull <image>` | Download image from Docker Hub | `docker pull ubuntu` |
| `docker rmi <image-id>` | Remove/delete a downloaded image | `docker rmi abc123` |

> ⚠️ You cannot remove an image if a container is currently using it. Stop/remove the container first.

### 3c — Container Commands

| Command | What It Does | Example |
|---------|-------------|---------|
| `docker ps` | List **RUNNING** containers only | `docker ps` |
| `docker ps -a` | List **ALL** containers (including stopped) | `docker ps -a` |
| `docker run <image>` | Create container from image (downloads if needed) | `docker run ubuntu` |
| `docker run -it <image>` | Create container & interact with it (Linux prompt) | `docker run -it ubuntu` |
| `docker start <id/name>` | Start a stopped container | `docker start selenium-hub` |
| `docker stop <id/name>` | Stop a running container | `docker stop selenium-hub` |
| `docker rm <id/name>` | Remove/delete a container | `docker rm selenium-hub` |
| `docker rm -f <name>` | Force remove even if running | `docker rm -f selenium-hub` |

> 💡 `docker run` = `docker pull` (if needed) + create container. It does **both steps** in one command.

> ⚠️ `docker ps` only shows **RUNNING** containers. Use `docker ps -a` to see all containers including stopped ones.

### 3d — Network Commands

| Command | What It Does | Example |
|---------|-------------|---------|
| `docker network create <name>` | Create a custom network | `docker network create grid` |
| `docker network rm <name>` | Remove a network | `docker network rm grid` |
| `--net <name>` | Attach container to a network (used in `docker run`) | `--net grid` |

---

## 4. Selenium Grid Setup with Docker

### Images Required

| Docker Image | Role in Grid |
|-------------|-------------|
| `selenium/hub` | The Selenium Hub (central controller) |
| `selenium/node-chrome` | Node with Linux + Chrome browser |
| `selenium/node-firefox` | Node with Linux + Firefox browser |
| `selenium/node-edge` | Node with Linux + Edge browser |

### Architecture

```
Your Test Code
      │
      ▼
 [Selenium Hub]  ←── localhost:4444
      │
      ├──── [Node: Chrome]   (Linux Container)
      │
      └──── [Node: Firefox]  (Linux Container)
```

---

## 4a. Manual Setup (Step-by-Step)

### Step 1: Pull the Images (Optional but faster)
```bash
docker pull selenium/hub
docker pull selenium/node-chrome
docker pull selenium/node-firefox
```

### Step 2: Create a Shared Network
```bash
docker network create grid
```

### Step 3: Start the Hub Container
```bash
docker run -d -p 4442-4444:4442-4444 --net grid --name selenium-hub selenium/hub
```

| Flag | Meaning |
|------|---------|
| `-d` | Run in background (detached mode) |
| `-p 4442-4444:4442-4444` | Map port range (host:container) |
| `--net grid` | Connect to the 'grid' network |
| `--name selenium-hub` | Name the container (used by nodes to connect) |
| `selenium/hub` | Image to use |

### Step 4: Start the Chrome Node
```bash
docker run -d --net grid \
  -e SE_EVENT_BUS_HOST=selenium-hub \
  -e SE_EVENT_BUS_PUBLISH_PORT=4442 \
  -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 \
  selenium/node-chrome
```

### Step 5: Start the Firefox Node
```bash
docker run -d --net grid \
  -e SE_EVENT_BUS_HOST=selenium-hub \
  -e SE_EVENT_BUS_PUBLISH_PORT=4442 \
  -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 \
  selenium/node-firefox
```

### Environment Variables Explained

| Variable | Purpose | Value |
|----------|---------|-------|
| `SE_EVENT_BUS_HOST` | Hostname of Selenium Hub container | `selenium-hub` |
| `SE_EVENT_BUS_PUBLISH_PORT` | Port node publishes events to hub | `4442` |
| `SE_EVENT_BUS_SUBSCRIBE_PORT` | Port node subscribes to hub events | `4443` |

### Step 6: Cleanup — Remove Network When Done
```bash
docker network rm grid   # Removes the grid network
```

> ⚠️ **Always start the Hub FIRST, then the nodes.** If nodes start before the hub, they will exit immediately because there's nothing to register to.

---

## 4b. Docker Compose Setup (Recommended ✅)

Docker Compose lets you define your entire Grid in a single YAML file and start everything with **one command** — no manual `docker run` commands needed.

### docker-compose.yml

```yaml
services:

  selenium-hub:
    image: selenium/hub
    container_name: selenium-hub
    ports:
      - "4442-4444:4442-4444"
    networks:
      - grid

  chrome:
    image: selenium/node-chrome
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    networks:
      - grid

  firefox:
    image: selenium/node-firefox
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    networks:
      - grid

  edge:
    image: selenium/node-edge
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    networks:
      - grid

networks:
  grid:
    driver: bridge
```

### Docker Compose Commands

| Command | What It Does | When to Use |
|---------|-------------|-------------|
| `docker compose up -d` | Start all services in background | Start your Grid |
| `docker compose down` | Stop and remove all containers | When done testing |
| `docker compose up -d --scale chrome=3` | Run 3 Chrome instances | Parallel test execution |
| `docker ps` | Verify running containers | After starting Grid |
| `docker compose up` | Start with logs visible | For debugging |

> ✅ Docker Compose is the **recommended approach** for real projects. It avoids manual commands, handles networking automatically, and is easily version-controlled in Git.

---

## 5. Verifying the Grid

### Check Containers Running
```bash
docker ps
```

### Open Selenium Grid UI
```
http://localhost:4444
```

In the Grid console you will see:
- All registered nodes (Chrome, Firefox, Edge)
- Available sessions and current capacity
- Active test sessions in progress

---

## 6. Test Configuration for Docker Grid

### Execution Mode Settings

| Setting | Value |
|---------|-------|
| `execution` | `remote` |
| `remoteURL` | `http://localhost:4444` |
| `platform` | `Linux` |
| `browser` | `chrome` / `firefox` / `edge` |

### RemoteWebDriver in Java
```java
ChromeOptions options = new ChromeOptions();
options.setPlatformName("Linux");

WebDriver driver = new RemoteWebDriver(
    new URL("http://localhost:4444"),
    options
);
```

### Application URL — Important!

| Where Browser Runs | URL to Use for App |
|-------------------|--------------------|
| Local machine (`execution=local`) | `http://localhost/myapp/` |
| Docker container (`execution=remote`) | `http://host.docker.internal/myapp/` |

> ⚠️ Inside a Docker container, `localhost` refers to the **container itself** — NOT your Mac/Windows machine. Use `host.docker.internal` to reach apps running on your host machine.

---

## 7. Common Errors & Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| `network grid not found` | Network not created yet | `docker network create grid` |
| Container exits immediately | Hub not ready when node starts | Start Hub first, wait a moment |
| `'docker containers' not found` | Wrong command | Use: `docker ps` or `docker container ls` |
| `container name already in use` | Old container with same name exists | `docker rm -f <container-name>` |
| Error during connect (version cmd) | Docker service not running | Restart Docker Desktop Service |
| Cannot reach localhost in container | localhost = container, not host | Use `host.docker.internal` |
| Cannot remove image | Container using it still exists | Stop & remove container first |

---

## 8. Browser & OS Support

| Browser | Available on Docker? |
|---------|---------------------|
| Chrome (Linux) | ✅ `selenium/node-chrome` |
| Firefox (Linux) | ✅ `selenium/node-firefox` |
| Edge (Linux) | ✅ `selenium/node-edge` |
| Safari | ❌ Mac only, cannot containerize |
| Windows Edge / IE | ❌ Windows containers not supported on Mac Docker |

> All Docker containers for Selenium run on **Linux** by default. To test on Windows, you need a real Windows machine, VM, or cloud service.

---

## 9. Best Practices

- ✅ Use Docker Compose instead of manual `docker run` commands
- ✅ Always start Hub **before** starting nodes
- ✅ Use `remote` execution mode when running tests on Grid
- ✅ Use `host.docker.internal` (not `localhost`) for app URL inside containers
- ✅ Don't mix `docker run` manual commands with `docker compose` in the same project
- ✅ Keep `docker-compose.yml` in your project root under version control (Git)
- ✅ Stop containers with `docker compose down` when done testing
- ✅ Scale browsers with `--scale` flag for parallel execution needs

---

## 10. Complete Workflow Summary

| Step | Action | Command / Note |
|------|--------|---------------|
| 1 | Install Docker Desktop | Download from docker.com — free for students |
| 2 | Verify installation | `docker version` && `docker compose version` |
| 3 | Create `docker-compose.yml` | Define Hub + all node services |
| 4 | Start the Grid | `docker compose up -d` |
| 5 | Verify containers running | `docker ps` |
| 6 | Open Grid UI | `http://localhost:4444` |
| 7 | Set `execution=remote` in tests | `remoteURL=http://localhost:4444` |
| 8 | Run test suite | Execute via TestNG XML / Maven |
| 9 | View results | Extent Report / TestNG report |
| 10 | Stop Grid when done | `docker compose down` |

---

> 🚀 **Key Takeaway:** Docker + Selenium Grid = Scalable, browser-independent test execution without relying on local machine setup.

---
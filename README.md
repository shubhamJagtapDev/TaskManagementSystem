# Task Management System

This repository contains the complete implementation of an in-memory **Task Management System**, fulfilling a comprehensive set of technical specifications (ranging from basic CRUD operations to complex time-to-live expiration and dynamic user quotas) refer `spec/TaskMangementSpec.md`. 

This project has been developed to demonstrate LLD problem-solving, edge-case handling, and clean code principles.

##  Key Features & Specification Satisfaction

The system handles all four distinct phases outlined in the project specification:

### 1. Task Lifecycle Operations
* **Functionality:** Create, update, and retrieve details of tasks utilizing a JSON-formatted query payload. 
* **Edge Cases Resolved:** Gracefully handles identical name/priority additions through independent task_id generation, mitigates NullPointerExceptions explicitly during creation/updates of task name default string fallback logic, and structures inline JSON extraction for external API consumption.

### 2. Search, Sort, and Filtering
* **Functionality:** Find sub-string matched (case-sensitive) tasks (`searchTasks`) and pull top tasks (`limitTasks`).
* **Edge Cases Resolved:** Simple tie-breaker sorting via a custom `TaskComparator`. If `priorities` identically match, execution delegates to extracting the task sequence number from `task_id`. 

### 3. User Assignment, Quotas & Time-to-Live (TTL)
* **Functionality:** Assign active tasks to users while respecting dynamic task limits and time-to-live restrictions.
* **Edge Cases Resolved:** Dynamically shifts `quotaLimits` up or down gracefully blocking new entries if an administrator reduces a user's quota below their current workload. Also, successfully identifies and blocks attempting to assign tasks that are already being worked on by someone else.

### 4. History, Expiration & Overdue Tracking
* **Functionality:** Idempotent completion markings and extraction of passively expired tasks.

---

##  Architectural Highlights & Engineering Decisions

When completing this system, several deliberate design choices were made to optimize performance, prevent desynchronization, and limit technical debt:

### 1. Dynamic Stream-Based Quota Tracking
Instead of manually maintaining an `activeTaskCount` integer directly on the `User` object, the system mathematically reduces the number of running tasks for a user dynamically at the time of assignment via Java `Streams`. 
**Why this matters:** Explicitly storing active counters creates devastating bugs when tasks naturally "expire" (as the counter would never decrease). By using Streams to filter out `COMPLETED` and overdue tasks on the fly, a user's available quota is **magically and automatically natively replenished** the exact millisecond tasks expire or finish.

### 2. Absolute TTL Translation
When tasks are assigned with a relative Time-To-Live (e.g. "5 seconds"), the `TaskManagerImpl` computes the exact **absolute expiration timestamp** (`timestamp + ttl`) immediately and stores that static calculation on the object. 
**Why this matters:** Constantly recalculating `current_time + TTL` mathematically breaks downstream filtering logic. By statically locking in the *expiration limit*, all downstream operations (finding overdue tasks, checking completion deadlines) natively become simple `< / >` integer evaluations.

### 3. Graceful Idempotency
Operations like `completeTask()` intentionally implement idempotent short-circuits. If a task is marked completed via poor network conditions multiple times, it simply traps the redundant call and returns `true`, guaranteeing a crash-free client experience.



## Tech Stack & Structure
* **Language:** Java 17+
* **Framework:** Pure Core Java (No dependencies)
* **Architecture:** Interface-Driven (`TaskManager`), Models, Repositories layer (in-memory hash maps), and service layouts.
* **LLM Used:** Gemini 3.1 Pro (High) - Antigravity. I used the AI agent for code review and generation of this ReadMe and a small of the commit messages


# Task management system specification

We need to plan our design according to following specifications

### 1. Implement basic operations to add, update and retrieve tasks.
   - **Function Specification: `addTask`**
     - **Requirement:** Create a new task in the system with the specified name and priority, and generate a newly assigned unique identifier for it. Note that tasks with the exact same name and priority can be added multiple times; each instance will be treated as an independent task with a newly generated task ID.
     - **Input Parameters:** `timestamp` (int), `name` (String), `priority` (int)
     - **Output Type:** `String` (A unique task ID)
     - **Edge Cases:** 
       - Ensuring distinct unique IDs are generated even when sequentially adding multiple tasks with perfectly identical names and priorities.
       - Handling null or empty `name`.

   - **Function Specification: `updateTask`**
     - **Requirement:** Modifies the name and priority of an already existing task.
     - **Input Parameters:** `timestamp` (int), `taskId` (String), `name` (String), `priority` (int)
     - **Output Type:** `boolean` (true if the task exists and was successfully updated, false if the task is not found)
     - **Edge Cases:**
       - Attempting to update a `taskId` that does not exist in the system.

   - **Function Specification: `getTask`**
     - **Requirement:** Retrieve a JSON-formatted string representing task details based on its unique identifier.
     - **Input Parameters:** `timestamp` (int), `taskId` (String)
     - **Output Type:** `Optional<String>` (An Optional containing the task representation, or empty if not found)
     - **Edge Cases:**
       - Attempting to retrieve a non-existent `taskId`.
### 2. Add functionality to search and sort tasks based on priority and creation order.
   - **Function Specification: `searchTasks`**
     - **Requirement:** Search for tasks whose names contain a specific `nameFilter`. The results should be sorted primarily by priority in descending order. In case of ties, tasks should be sorted secondarily by their creation order (ascending based on the integer extracted from the `task_id`). Return up to `maxResults` task IDs.
     - **Input Parameters:** `nameFilter` (String), `maxResults` (int)
     - **Output Type:** `List<String>` (A list of sorted task IDs matching the filter criteria)
     - **Edge Cases:**
       - `maxResults` is less than or equal to 0 (returns an empty list).
       - No tasks match the provided `nameFilter`.
       - Multiple matched tasks resolving in tied priorities requiring accurate secondary sorting.

   - **Function Specification: `limitTasks`**
     - **Requirement:** Retrieve up to a specific `limit` number of tasks from the entire system, sorted using the same criteria: mostly by priority in descending order, with ties broken by creation order (ascending based on `task_id` integer).
     - **Input Parameters:** `limit` (int)
     - **Output Type:** `List<String>` (A list of sorted top task IDs)
     - **Edge Cases:**
       - `limit` is less than or equal to 0 (returns an empty list).
       - The total number of tasks fundamentally generated in the system is globally fewer than the requested `limit`.
### 3. Introduce users, time-based task assignment with TTL expiration, and dynamic quota management.
   - **Function Specification: `assignTask`**
     - **Requirement:** Assign an existing task to a specific user with a Time-to-Live (TTL) expiration. Updates should respect the user's quota.
     - **Input Parameters:** `timestamp` (int), `task_id` (String), `user_id` (String), `ttl` (int)
     - **Output Type:** `boolean` (true if successful, false otherwise)
     - **Edge Cases:** 
       - User has reached their dynamic quota limit.
       - Task does not exist or is already actively assigned to another user.
       - The task has already been completed.

   - **Function Specification: `updateUserQuota`**
     - **Requirement:** Updates the maximum active task quota for a specific user dynamically.
     - **Input Parameters:** `timestamp` (int), `user_id` (String), `new_quota` (int)
     - **Output Type:**  `boolean` (true if successful or false the `new_quota` is <=0)
     - **Edge Cases:**
       - The new quota is explicitly set extremely low (e.g., 0).
       - New quota is less than the user's currently active tasks (should still allow update but prevent new assignments).

### 4. Add task completion tracking and historical analysis to identify overdue assignments that expired without completion.
   - **Function Specification: `completeTask`**
     - **Requirement:** Mark an actively assigned task as completed.
     - **Input Parameters:** `timestamp` (int), `task_id` (String)
     - **Output Type:** `boolean` (true if marked as completed, false if it fails)
     - **Edge Cases:**
       - The `timestamp` is past the task's TTL, meaning it has already expired.
       - The task was never assigned or does not exist.
       - The task has already been completed previously.

   - **Function Specification: `getOverdueTasks`**
     - **Requirement:** Identify all tasks that were assigned to users but expired without being completed up to the current timestamp.
     - **Input Parameters:** `timestamp` (int)
     - **Output Type:** `List<String>` (A list of expired task IDs)
     - **Edge Cases:**
       - A task expires exactly at the passed `timestamp`.
       - No tasks have expired yet.

**Note**
- All priority (introduced in step 1) and quota (introduced in step 3) values provided in the tests will be non-negative integers(>=0)
- All timestamp (introduced in step 3) and finish_time (introduced in step 3) will be non-negative integers(>=0) representing seconds since the system started
- Time always flows forward, so any timestamp or finish_time will be >= any previous timestamp or finish_time 
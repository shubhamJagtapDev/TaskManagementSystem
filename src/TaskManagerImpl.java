import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import comparators.TaskComparator;
import models.AssignedTask;
import models.Task;
import models.TaskStatus;
import models.User;
import services.UserService;
import services.UserServiceImpl;

public class TaskManagerImpl implements TaskManager {
    private Map<String, Task> tasksMap;
    private Map<String, AssignedTask> assignedTaskMap;
    private TaskComparator taskComparator;
    private UserService userService;
    private long tasksCount;

    public TaskManagerImpl() {
        tasksMap = new HashMap<>();
        assignedTaskMap = new HashMap<>();
        taskComparator = new TaskComparator();
        userService = new UserServiceImpl();
        tasksCount = 0;
    }

    @Override
    public String addTask(int timestamp, String name, int priority) {
        if (name == null || name.isEmpty())
            name = "Default Task";
        Task task = new Task(timestamp, name, priority);
        tasksCount += 1;
        String taskId = "task_id_" + tasksCount;
        task.setTaskId(taskId);
        tasksMap.put(taskId, task);
        return taskId;
    }

    @Override
    public boolean updateTask(int timestamp, String taskId, String name, int priority) {
        if (!tasksMap.containsKey(taskId)) {
            System.out.println("Task does not exists");
            return false;
        }
        Task task = tasksMap.get(taskId);
        if (name != null && !name.isEmpty())
            task.setName(name);
        task.setPriority(priority);
        return true;
    }

    @Override
    public Optional<String> getTask(int timestamp, String taskId) {
        if (!tasksMap.containsKey(taskId)) {
            System.out.println("Task does not exists");
            return Optional.empty();
        }

        return Optional.of(tasksMap.get(taskId).toString());
    }

    @Override
    public List<String> searchTasks(String nameFilter, int maxResults) {
        if (maxResults <= 0) {
            System.out.println("Request result count is <= 0");
            return List.of();
        }
        return tasksMap.values().stream()
                .filter(task -> task.getName().contains(nameFilter))
                .sorted(taskComparator)
                .map(Task::getTaskId)
                .limit(maxResults)
                .toList();
    }

    @Override
    public List<String> limitTasks(int limit) {
        if (limit <= 0)
            return List.of();
        return tasksMap.values().stream()
                .sorted(taskComparator)
                .map(Task::getTaskId)
                .limit(limit)
                .toList();
    }

    @Override
    public boolean assignTask(int timestamp, String taskId, String userId, int ttl) {
        // task validations
        if (!tasksMap.containsKey(taskId)) {
            System.out.println("Task does not exists");
            return false;
        }
        AssignedTask assignedTask;
        if (assignedTaskMap.containsKey(taskId)) {
            assignedTask = assignedTaskMap.get(taskId);
            // note: here we could have sent a detailed error JSON message specifying why we
            // are
            // not assigning this task to the user, however to keep it simple for now
            // I'm returning true if success or false for any kind of failure/error

            // If it exists, hasn't been completed, AND hasn't expired, it is actively
            // assigned!
            if (!assignedTask.getStatus().equals(TaskStatus.COMPLETED) && assignedTask.getTtl() > timestamp) {
                return false;
            }
        } else {
            assignedTask = new AssignedTask();
        }

        Optional<User> optionalUser = userService.getUser(userId);
        if (optionalUser.isEmpty())
            throw new RuntimeException("User does not exist");
        User user = optionalUser.get();

        int activeTasksCount = (int) assignedTaskMap.values().stream()
                .filter(t -> t.getUser_id().equals(userId))
                .filter(t -> !t.getStatus().equals(TaskStatus.COMPLETED)) // only in progress tasks
                .filter(t -> t.getTtl() > timestamp) // Make sure it's not overdue
                .count();
        // user has quota to take up the task
        if (activeTasksCount < user.getQuotaLimit()) {
            assignedTask.setStatus(TaskStatus.NOT_STARTED);
            assignedTask.setTask_id(taskId);
            assignedTask.setUser_id(userId);
            assignedTask.setTtl(timestamp + ttl);
            // after this we usually call save method of user repository,
            // but we have in-memory DB, and we have direct reference to user obj, then it
            // doesn't make
            // sense to call save method again in this case only

            assignedTaskMap.put(taskId, assignedTask);
            return true;
        }
        // we cannot assign the task to user as the user has reached their dynamic quota
        // limit
        return false;
    }

    @Override
    public boolean updateUserQuota(int timestamp, String userId, int newQuota) {
        if (newQuota <= 0) {
            return false;
        }
        Optional<User> optionalUser = userService.getUser(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("user does not exist");
        }
        User user = optionalUser.get();
        user.setQuotaLimit(newQuota);
        return true;
    }

    @Override
    public boolean completeTask(int timestamp, String taskId) {
        // is the task even exists
        if (!tasksMap.containsKey(taskId)) {
            System.out.println("Task does not exists");
            return false;
        }

        // is the task even assigned to user
        if (!assignedTaskMap.containsKey(taskId)) {
            System.out.println("Task cannot be marked completed as its not being assigned yet");
            return false;
        }
        AssignedTask assignedTask = assignedTaskMap.get(taskId);

        // idempotency : return true as assigned task is already completed
        if (assignedTask.getStatus().equals(TaskStatus.COMPLETED))
            return true;

        int taskTTL = assignedTask.getTtl();
        // Task TTL expired and cannot be marked completed
        if (taskTTL <= timestamp)
            return false;

        // mark task as completed as task TTL is after timestamp
        assignedTask.setStatus(TaskStatus.COMPLETED);
        assignedTask.setFinish_time(timestamp);
        return true;
    }

    @Override
    public List<String> getOverdueTasks(int timestamp) {
        List<String> expiredTaskIds;
        expiredTaskIds = assignedTaskMap.values().stream()
                .filter(assignedTask -> !assignedTask.getStatus().equals(TaskStatus.COMPLETED))
                .filter(runningTask -> runningTask.getTtl() <= timestamp)
                .map(AssignedTask::getTask_id)
                .toList();
        return expiredTaskIds;
    }

}

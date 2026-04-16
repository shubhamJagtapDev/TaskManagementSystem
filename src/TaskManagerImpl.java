import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import comparators.TaskComparator;
import models.Task;

public class TaskManagerImpl implements TaskManager {
    private Map<String, Task> tasksMap;
    private TaskComparator taskComparator;
    private long tasksCount;

    public TaskManagerImpl() {
        tasksMap = new HashMap<>();
        taskComparator = new TaskComparator();
        tasksCount = 0;
    }

    @Override
    public String addTask(int timestamp, String name, int priority) {
        Task task = new Task(timestamp, name, priority);
        tasksCount += 1;
        String taskId = "task_id_" + tasksCount;
        tasksMap.put(taskId, task);
        return taskId;
    }

    @Override
    public boolean updateTask(int timestamp, String taskId, String name, int priority) {
        if (!tasksMap.containsKey(taskId))
            return false;
        Task task = tasksMap.get(taskId);
        task.setName(name);
        task.setPriority(priority);
        return true;
    }

    @Override
    public Optional<String> getTask(int timestamp, String taskId) {
        if (!tasksMap.containsKey(taskId)) {
            return Optional.empty();
        }

        return Optional.of(tasksMap.get(taskId).toString());
    }

    @Override
    public List<String> searchTasks(String nameFilter, int maxResults) {
        if (maxResults <= 0)
            return List.of();
        return tasksMap.entrySet().stream()
                .map(entry -> entry.getValue())
                .filter(task -> task.getName().contains(nameFilter))
                .sorted(taskComparator)
                .map(taskObj -> taskObj.getTaskId())
                .limit(maxResults)
                .toList();
    }

    @Override
    public List<String> limitTasks(int limit) {
        if (limit <= 0)
            return List.of();
        return tasksMap.entrySet().stream()
                .map(entry -> entry.setValue(null))
                .sorted(taskComparator)
                .map(taskObj -> taskObj.getTaskId())
                .limit(limit)
                .toList();
    }
}

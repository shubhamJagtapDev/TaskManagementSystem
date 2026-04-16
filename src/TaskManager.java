import java.util.List;
import java.util.Optional;

public interface TaskManager {
    String addTask(int timestamp, String name, int priority);

    boolean updateTask(int timestamp, String taskId, String name, int priority);

    Optional<String> getTask(int timestamp, String taskId);

    List<String> searchTasks(String nameFilter, int maxResults);

    List<String> limitTasks(int limit);

    boolean assignTask(int timestamp, String taskId, String userId, int ttl);

    boolean updateUserQuota(int timestamp, String userId, int newQuota);

    boolean completeTask(int timestamp, String taskId);

    List<String> getOverdueTasks(int timestamp);

}

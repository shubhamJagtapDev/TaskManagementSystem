package models;

public class AssignedTask {
    private String task_id;
    private String user_id;
    private int ttl;
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public String getTask_id() {
        return task_id;
    }

    public int getTtl() {
        return ttl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

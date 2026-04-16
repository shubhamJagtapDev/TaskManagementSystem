package models;

public class Task {
    String taskId;
    String name;
    int timestamp;
    int priority;

    public Task(int timestamp, String name, int priority) {
        this.name = name;
        this.timestamp = timestamp;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"name\":\"").append(this.name).append("\",")
                .append("\"priority\":\"").append(priority)
                .append("}");
        return json.toString();
    }

}

package models;

public class User {
    private String user_id;
    private String name;
    private int assignedTaskCount;
    private int quotaLimit;

    public User(String name, int quotaLimit) {
        this.assignedTaskCount = 0;
        this.quotaLimit = quotaLimit;
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getAssignedTaskCount() {
        return assignedTaskCount;
    }

    public void setAssignedTaskCount(int assignedTaskCount) {
        this.assignedTaskCount = assignedTaskCount;
    }

    public int getQuotaLimit() {
        return quotaLimit;
    }

    public void setQuotaLimit(int quotaLimit) {
        this.quotaLimit = quotaLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package comparators;

import java.util.Comparator;

import models.Task;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        // sorting tasks based on prirority in desc order
        int priorityCmpResult = Integer.compare(t2.getPriority(), t1.getPriority());
        if (priorityCmpResult != 0)
            return priorityCmpResult;

        // if two tasks have same priorities and we sort them via there creation order
        // i.e by the task_ids
        return Integer.compare(extractInteger(t1.getTaskId()), extractInteger(t2.getTaskId()));
    }

    private Integer extractInteger(String taskId) {
        String digits = taskId.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

}

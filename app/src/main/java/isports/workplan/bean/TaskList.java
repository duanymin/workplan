package isports.workplan.bean;

import java.util.LinkedList;

/**
 * Created by Duan on 7月16日.
 */
public class TaskList {
    private LinkedList<Task> tasks;

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(LinkedList<Task> tasks) {
        this.tasks = tasks;
    }
}

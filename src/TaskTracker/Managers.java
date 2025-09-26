package TaskTracker;

public class Managers {

    public static TaskManager getDefault() {

        return new InMemoryTaskManager();

    }
}

package tasktracker.managers;

import tasktracker.utilities.ManagerType;

public class Managers {

    public static TaskManager getDefault() {

        return new InMemoryTaskManager();

    }

    public static TaskManager get(ManagerType type) {
        return get(type, null);
    }

    public static TaskManager get(ManagerType type, String filePath) {
        return switch (type) {
            case IN_MEMORY -> new InMemoryTaskManager();
            case FILE_BACKED -> {
                if (filePath == null || filePath.isBlank()) {
                    throw new IllegalArgumentException("Для FILE_BACKED нужно указать путь к файлу");
                }
                yield new FileBackedTaskManager(filePath);
            }
        };
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

}

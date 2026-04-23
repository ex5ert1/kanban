package ru.kanban.manager;

import ru.kanban.model.Epic;
import ru.kanban.model.SimpleTask;
import ru.kanban.model.Subtask;
import ru.kanban.model.Task;
import java.util.List;

public interface TaskManager {
    List<SimpleTask> getAllSimpleTasks();
    void deleteAllSimpleTasks();
    SimpleTask getSimpleTaskById(int id);
    SimpleTask createSimpleTask(SimpleTask task);
    SimpleTask updateSimpleTask(SimpleTask updatedTask);
    boolean deleteSimpleTaskById(int id);

    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpicById(int id);
    Epic createEpic(Epic epic);
    Epic updateEpic(Epic updatedEpic);
    boolean deleteEpicById(int id);

    List<Subtask> getAllSubtasks();
    void deleteAllSubtasks();
    Subtask getSubtaskById(int id);
    Subtask createSubtask(Subtask subtask);
    Subtask updateSubtask(Subtask updatedSubtask);
    boolean deleteSubtaskById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);
    List<Task> getHistory();
}
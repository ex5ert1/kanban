package ru.kanban.manager;

import ru.kanban.model.*;
import ru.kanban.exception.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final List<Task> history = new ArrayList<>();
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    private void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public SimpleTask getSimpleTaskById(int id) {
        SimpleTask task = simpleTasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("Задача с id " + id + " не найдена");
        }
        addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new EpicNotFoundException("Эпик с id " + id + " не найден");
        }
        addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new SubtaskNotFoundException("Подзадача с id " + id + " не найдена");
        }
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public SimpleTask createSimpleTask(SimpleTask task) {
        task.setId(generateId());
        simpleTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SimpleTask updateSimpleTask(SimpleTask updatedTask) {
        if (!simpleTasks.containsKey(updatedTask.getId())) {
            throw new UpdateException("Невозможно обновить: задача не существует");
        }
        simpleTasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }

    @Override
    public void deleteAllSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic saved = epics.get(updatedEpic.getId());
        if (saved == null) {
            throw new EpicNotFoundException("Невозможно обновить: эпик не найден");
        }
        saved.setName(updatedEpic.getName());
        saved.setDescription(updatedEpic.getDescription());
        return saved;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new InvalidEpicForSubtaskException("Нельзя создать подзадачу без существующего эпика");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        if (!subtasks.containsKey(updatedSubtask.getId())) {
            throw new SubtaskNotFoundException("Невозможно обновить: подзадача не найдена");
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(updatedSubtask.getEpicId());
        return updatedSubtask;
    }

    @Override
    public boolean deleteSimpleTaskById(int id) {
        return simpleTasks.remove(id) != null;
    }

    @Override
    public boolean deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        List<Subtask> result = new ArrayList<>();
        for (Integer id : epic.getSubtaskIds()) {
            Subtask s = subtasks.get(id);
            if (s != null) {
                result.add(s);
            }
        }
        return result;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> subs = getSubtasksByEpicId(epicId);
        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;
        for (Subtask s : subs) {
            if (s.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (s.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }
}
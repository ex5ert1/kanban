package ru.kanban.manager;

import ru.kanban.model.*;

import java.util.*;
import java.util.stream.Collectors;

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
        if (task == null) return;
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
        addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public void deleteAllSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public SimpleTask createSimpleTask(SimpleTask task) {
        task.setId(generateId());
        simpleTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SimpleTask updateSimpleTask(SimpleTask updatedTask) {
        return simpleTasks.replace(updatedTask.getId(), updatedTask) != null ? updatedTask : null;
    }

    @Override
    public boolean deleteSimpleTaskById(int id) {
        return simpleTasks.remove(id) != null;
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
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        return Optional.ofNullable(epics.get(updatedEpic.getId()))
                .map(saved -> {
                    saved.setName(updatedEpic.getName());
                    saved.setDescription(updatedEpic.getDescription());
                    return saved;
                }).orElse(null);
    }

    @Override
    public boolean deleteEpicById(int id) {
        return Optional.ofNullable(epics.remove(id))
                .map(epic -> {
                    epic.getSubtaskIds().forEach(subtasks::remove);
                    return true;
                }).orElse(false);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        return Optional.ofNullable(epics.get(subtask.getEpicId()))
                .map(epic -> {
                    subtask.setId(generateId());
                    subtasks.put(subtask.getId(), subtask);
                    epic.addSubtaskId(subtask.getId());
                    updateEpicStatus(epic.getId());
                    return subtask;
                }).orElse(null);
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        return Optional.ofNullable(subtasks.replace(updatedSubtask.getId(), updatedSubtask))
                .map(old -> {
                    updateEpicStatus(updatedSubtask.getEpicId());
                    return updatedSubtask;
                }).orElse(null);
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        return Optional.ofNullable(subtasks.remove(id))
                .map(subtask -> {
                    Optional.ofNullable(epics.get(subtask.getEpicId()))
                            .ifPresent(epic -> {
                                epic.removeSubtaskId(id);
                                updateEpicStatus(epic.getId());
                            });
                    return true;
                }).orElse(false);
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return Optional.ofNullable(epics.get(epicId))
                .map(epic -> epic.getSubtaskIds().stream()
                        .map(subtasks::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private void updateEpicStatus(int epicId) {
        Optional.ofNullable(epics.get(epicId)).ifPresent(epic -> {
            List<Subtask> subs = getSubtasksByEpicId(epicId);
            if (subs.isEmpty()) {
                epic.setStatus(Status.NEW);
            } else if (subs.stream().allMatch(s -> s.getStatus() == Status.DONE)) {
                epic.setStatus(Status.DONE);
            } else if (subs.stream().allMatch(s -> s.getStatus() == Status.NEW)) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        });
    }
}
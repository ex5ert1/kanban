package ru.kanban.manager;

import ru.kanban.model.Status;
import ru.kanban.model.Epic;
import ru.kanban.model.SimpleTask;
import ru.kanban.model.Subtask;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private final Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    public List<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    public void deleteAllSimpleTasks() {
        simpleTasks.clear();
    }

    public SimpleTask getSimpleTaskById(int id) {
        return simpleTasks.get(id);
    }

    public SimpleTask createSimpleTask(SimpleTask task) {
        task.setId(generateId());
        simpleTasks.put(task.getId(), task);
        return task;
    }

    public SimpleTask updateSimpleTask(SimpleTask updatedTask) {
        return simpleTasks.computeIfPresent(updatedTask.getId(), (k, v) -> updatedTask);
    }

    public boolean deleteSimpleTaskById(int id) {
        return simpleTasks.remove(id) != null;
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.values().stream()
                .flatMap(epic -> epic.getSubtaskIds().stream())
                .forEach(subtasks::remove);
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic updatedEpic) {
        return epics.computeIfPresent(updatedEpic.getId(), (id, existingEpic) -> {
            updatedEpic.getSubtaskIds().clear();
            updatedEpic.getSubtaskIds().addAll(existingEpic.getSubtaskIds());
            updateEpicStatus(id);
            return updatedEpic;
        });
    }

    public boolean deleteEpicById(int id) {
        return Optional.ofNullable(epics.remove(id))
                .map(epic -> {
                    epic.getSubtaskIds().forEach(subtasks::remove);
                    return true;
                })
                .orElse(false);
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        Optional.ofNullable(epics.get(subtask.getEpicId()))
                .ifPresent(parentEpic -> {
                    parentEpic.addSubtaskId(subtask.getId());
                    updateEpicStatus(parentEpic.getId());
                });
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask updateSubtask(Subtask updatedSubtask) {
        return subtasks.computeIfPresent(updatedSubtask.getId(), (id, existingSubtask) -> {
            subtasks.put(id, updatedSubtask);
            Optional.ofNullable(epics.get(updatedSubtask.getEpicId()))
                    .ifPresent(epic -> updateEpicStatus(epic.getId()));
            return updatedSubtask;
        });
    }

    public boolean deleteSubtaskById(int id) {
        return Optional.ofNullable(subtasks.remove(id))
                .map(subtaskToDelete -> {
                    Optional.ofNullable(epics.get(subtaskToDelete.getEpicId()))
                            .ifPresent(parentEpic -> {
                                parentEpic.removeSubtaskId(id);
                                updateEpicStatus(parentEpic.getId());
                            });
                    return true;
                })
                .orElse(false);
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    public void updateEpicStatus(int epicId) {
        Optional.ofNullable(epics.get(epicId))
                .ifPresent(epic -> {
                    List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
                    if (epicSubtasks.isEmpty()) {
                        epic.setStatus(Status.NEW);
                    } else if (epicSubtasks.stream().allMatch(s -> s.getStatus() == Status.DONE)) {
                        epic.setStatus(Status.DONE);
                    } else if (epicSubtasks.stream().allMatch(s -> s.getStatus() == Status.NEW)) {
                        epic.setStatus(Status.NEW);
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                });
    }
}
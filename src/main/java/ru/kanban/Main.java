package ru.kanban;

import ru.kanban.manager.TaskManager;
import ru.kanban.model.Epic;
import ru.kanban.model.SimpleTask;
import ru.kanban.model.Status;
import ru.kanban.model.Subtask;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        SimpleTask task1 = manager.createSimpleTask(new SimpleTask("Задача 1", "Описание задачи 1", Status.NEW));
        SimpleTask task2 = manager.createSimpleTask(new SimpleTask("Задача 2", "Описание задачи 2", Status.IN_PROGRESS));

        Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Описание эпика 1", Status.NEW));
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId()));

        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());

        System.out.println("Все простые задачи:");
        manager.getAllSimpleTasks().forEach(t -> System.out.println(t.getName() + " - " + t.getStatus()));

        System.out.println("\nВсе эпики:");
        manager.getAllEpics().forEach(e -> System.out.println(e.getName() + " - " + e.getStatus()));

        System.out.println("\nПодзадачи эпика " + epic1.getName() + ":");
        manager.getSubtasksByEpicId(epic1.getId()).forEach(s -> System.out.println(s.getName() + " - " + s.getStatus()));

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        System.out.println("\nПосле обновления статуса подзадачи:");
        System.out.println("Эпик: " + epic1.getName() + " - " + epic1.getStatus());
    }
}
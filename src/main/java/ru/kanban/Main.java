package ru.kanban;

import ru.kanban.manager.InMemoryTaskManager;
import ru.kanban.manager.TaskManager;
import ru.kanban.model.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        SimpleTask task1 = manager.createSimpleTask(new SimpleTask("Задача 1", "Купить продукты", Status.NEW));
        SimpleTask task2 = manager.createSimpleTask(new SimpleTask("Задача 2", "Помыть окна", Status.NEW));

        Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Переезд", Status.NEW));
        Subtask sub1 = manager.createSubtask(new Subtask("Подзадача 1", "Собрать вещи", Status.NEW, epic1.getId()));
        Subtask sub2 = manager.createSubtask(new Subtask("Подзадача 2", "Заказать машину", Status.NEW, epic1.getId()));

        manager.getSimpleTaskById(task1.getId());
        manager.getSimpleTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());
        manager.getSimpleTaskById(task1.getId());
        manager.getSimpleTaskById(task1.getId());
        manager.getSimpleTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());
        manager.getSimpleTaskById(task1.getId());

        List<Task> history = manager.getHistory();
        System.out.println("Количество элементов в истории: " + history.size());

        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i).getName());
        }

        System.out.println("Статус до завершения подзадач: " + epic1.getStatus());

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        System.out.println("Статус после завершения всех подзадач: " + epic1.getStatus());
    }
}
package test.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import servers.adapters.*;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private SimpleTask task1;
    private SimpleTask task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private static Gson gson;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static HttpResponse.BodyHandler<String> handler;
    private static HttpClient client;
    private HttpResponse<String> response;
    private HttpRequest request;

    @BeforeAll
    public static void initializationGson() {
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        TaskAdapter deserializerTasks = new TaskAdapter("type");
        deserializerTasks.registerBarnType(TaskType.TASK.toString(), SimpleTask.class);
        deserializerTasks.registerBarnType(TaskType.EPIC.toString(), Epic.class);
        deserializerTasks.registerBarnType(TaskType.SUBTASK.toString(), Subtask.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, deserializerTasks)
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .serializeNulls()
                .create();
    }

    @BeforeEach
    public void initializationTasks() {
        task1 = new SimpleTask("Test task1", "Test task1 desc", "01.01.2023 12:00"
                , 60);
        task2 = new SimpleTask("Test task2", "Test task2 desc", "01.01.2023 13:00"
                , 60);
        subtask1 = new Subtask("Test sub1", "Test sub1 desc", "01.01.2023 14:00", 60);
        subtask2 = new Subtask("Test sub2", "Test sub2 desc", "01.01.2023 15:00", 60);
        epic1 = new Epic("Test epic1", "Test epic1 desc");
        epic2 = new Epic("Test epic2", "Test epic2 desc");
    }

    @BeforeEach
    public void initialization() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer(Managers.getDefault("http://localhost:8078"));
            taskServer.start();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }

    @AfterEach
    public void offServers() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    public void postIncorrectUrl() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTasksdsf", json);
        try {
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код состояния не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postAndGetSimpleTasks() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код состояния не совпадает");

            request = createGetRequest("http://localhost:8080/tasks/simpleTask?id=0");
            response = client.send(request, handler);
            task1.setId(0);

            assertEquals(200, response.statusCode(), "Код состояния не совпадает");
            assertEquals(task1, gson.fromJson(response.body(), SimpleTask.class), "Задачи не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/simpleTask");
            task2.setId(1);
            response = client.send(request, handler);
            List<Task> list = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertEquals(task1, list.get(0), "Задачи не совпадают");
            assertEquals(task2, list.get(1), "Задачи не совпадают");
            System.out.println();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postBadBodySimpleTask() {
        String json = "{\n" +
                "  \"name\": \"Task\",\n" +
                "  \"description\": \"Description task\",\n" +
                "  \"status\": \"NscFDSFSDFSsEW\",\n" +
                "  \"id\": null,\n" +
                "  \"startTime\": \"20.08.2022 11:56\",\n" +
                "  \"duration\": \"PT1H\",\n" +
                "  \"formatter\": \"dd.MM.yyyy HH:mm\"\n" +
                "}";
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            response = client.send(request, handler);
            assertEquals(400, response.statusCode());
            assertEquals("Некорректное тело запроса", response.body()
                    , "Сообщение в теле ответа не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }


    @Test
    public void postIntersectionsSimpleTask() {
        SimpleTask simpleTask = new SimpleTask("task test", "description", task1.getStartTime()
                .format(formatter), (int) task1.getDuration().toMinutes());
        String json1 = gson.toJson(task1);
        String json2 = gson.toJson(simpleTask);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json1);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/simpleTask", json2);
            response = client.send(request, handler);

            assertEquals(409, response.statusCode());
            assertNotNull(response.body(), "Отсутствует тело ответа с сообщением");
            assertFalse(response.body().isBlank(), "Тело ответа с сообщением пустое");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postIdenticalIdSimpleTask() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            client.send(request, handler);
            task1.setId(0);
            json = gson.toJson(task1);
            request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
            response = client.send(request, handler);
            String exceptedMessage = "Ошибка создания простой задачи! Такая задача уже есть.";
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            assertEquals(exceptedMessage, response.body(), "Тело сообщения не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateSimpleTask() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            client.send(request, handler);
            task2.setId(0);
            request = createPutRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/simpleTask?id=0");
            response = client.send(request, handler);
            assertEquals(task2, gson.fromJson(response.body(), SimpleTask.class), "Задачи не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateIncorrectIdSimpleTask() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            client.send(request, handler);

            task2.setId(50);
            request = createPutRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
            response = client.send(request, handler);
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка обновления задачи! Такой задачи нет.";
            assertEquals(exceptedMessage, response.body(), "Сообщения в теле не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteSimpleTasks() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
        try {
            client.send(request, handler);
            request = createDeleteRequest("http://localhost:8080/tasks/simpleTask?id=0");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/simpleTask?id=0");
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/simpleTask", json);
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
            client.send(request, handler);

            request = createDeleteRequest("http://localhost:8080/tasks/simpleTask");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/simpleTask");
            response = client.send(request, handler);
            assertEquals(204, response.statusCode(), "Коды состояния не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteNonExistentSimpleTask() {
        request = createDeleteRequest("http://localhost:8080/tasks/simpleTask?id=0");
        try {
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка удаления задачи! Такой задачи нет";
            assertEquals(exceptedMessage, response.body());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postAndGetEpics() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код состояния не совпадает");

            request = createGetRequest("http://localhost:8080/tasks/epic?id=0");
            response = client.send(request, handler);
            epic1.setId(0);

            assertEquals(200, response.statusCode(), "Код состояния не совпадает");
            assertEquals(epic1, gson.fromJson(response.body(), Epic.class), "Задачи не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic2));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/epic");
            epic2.setId(1);
            response = client.send(request, handler);
            List<Task> list = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertEquals(epic1, list.get(0), "Задачи не совпадают");
            assertEquals(epic2, list.get(1), "Задачи не совпадают");
            System.out.println();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postBadBodyEpic() {
        String json = "{\n" +
                "  \"subtasks\": {},\n" +
                "  \"name\": \"Test epic\",\n" +
                "  \"description\": \"Test epic1 desc\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"id\": null,\n" +
                "  \"startTime\": null,\n" +
                "  \"duration\": null,\n" +
                "  \"formatter\": \"dd.MM.ydfgdyy HH:mm\"\n" +
                "}";
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            response = client.send(request, handler);
            assertEquals(400, response.statusCode());
            assertEquals("Некорректное тело запроса", response.body()
                    , "Сообщение в теле ответа не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postIdenticalIdEpic() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            epic1.setId(0);
            json = gson.toJson(epic1);
            request = createPostRequest("http://localhost:8080/tasks/epic", json);
            response = client.send(request, handler);
            String exceptedMessage = "Ошибка создания эпической задачи! Такая задача уже есть.";
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            assertEquals(exceptedMessage, response.body(), "Тело сообщения не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateEpic() {
        String json = gson.toJson(task1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            epic2.setId(0);
            request = createPutRequest("http://localhost:8080/tasks/epic", gson.toJson(task2));
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/epic?id=0");
            response = client.send(request, handler);
            assertEquals(epic2, gson.fromJson(response.body(), Epic.class), "Задачи не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateIncorrectIdEpic() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);

            epic2.setId(50);
            request = createPutRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(epic2));
            response = client.send(request, handler);
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка обновления задачи! Такой задачи нет.";
            assertEquals(exceptedMessage, response.body(), "Сообщения в теле не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteEpics() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createDeleteRequest("http://localhost:8080/tasks/epic?id=0");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/epic?id=0");
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/epic", json);
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic2));
            client.send(request, handler);

            request = createDeleteRequest("http://localhost:8080/tasks/epic");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/epic");
            response = client.send(request, handler);
            assertEquals(204, response.statusCode(), "Коды состояния не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteNonExistentEpic() {
        request = createDeleteRequest("http://localhost:8080/tasks/epic?id=0");
        try {
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка удаления эпической задачи! Такой эпической задачи нет";
            assertEquals(exceptedMessage, response.body());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postAndGetSubtask() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код состояния не совпадает");

            request = createGetRequest("http://localhost:8080/tasks/subtask?id=1");
            response = client.send(request, handler);
            subtask1.setId(1);
            subtask1.setEpicID(0);

            assertEquals(200, response.statusCode(), "Код состояния не совпадает");
            assertEquals(subtask1, gson.fromJson(response.body(), Subtask.class), "Задачи не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask2));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/subtask");
            subtask2.setId(2);
            subtask2.setEpicID(0);
            response = client.send(request, handler);
            List<Task> list = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertEquals(subtask1, list.get(0), "Задачи не совпадают");
            assertEquals(subtask2, list.get(1), "Задачи не совпадают");
            System.out.println();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postBadBodySubtask() {
        String json = "{\n" +
                "  \"epicID\": 0,\n" +
                "  \"name\": \"sub1 for ep1\",\n" +
                "  \"description\": \"Description sub1 for ep 1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"id\": null,\n" +
                "  \"startTime\": \"20.08.2023 15:19\",\n" +
                "  \"duration\": \"PT1sdvsH\",\n" +
                "  \"formatter\": \"dd.MM.yyyy HH:mm\"\n" +
                "}";
        request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic1));
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", json);
            response = client.send(request, handler);
            assertEquals(400, response.statusCode());
            assertEquals("Некорректное тело запроса", response.body()
                    , "Сообщение в теле ответа не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }


    @Test
    public void postIntersectionsSubtask() {
        Subtask subtask = new Subtask("sub test", "description", subtask1.getStartTime()
                .format(formatter), (int) subtask1.getDuration().toMinutes());
        request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic1));
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask));
            response = client.send(request, handler);

            assertEquals(409, response.statusCode());
            assertNotNull(response.body(), "Отсутствует тело ответа с сообщением");
            assertFalse(response.body().isBlank(), "Тело ответа с сообщением пустое");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void postIdenticalIdSubtask() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            subtask1.setId(1);
            subtask1.setEpicID(0);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            response = client.send(request, handler);
            String exceptedMessage = "Ошибка создания подзадачи! Такая задача в эпике с id=0 уже есть.";
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            assertEquals(exceptedMessage, response.body(), "Тело сообщения не совпадает");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateSubtask() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            subtask2.setId(1);
            subtask2.setEpicID(0);
            request = createPutRequest("http://localhost:8080/tasks/subtask", gson.toJson(subtask2));
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/subtask?id=1");
            response = client.send(request, handler);
            assertEquals(subtask2, gson.fromJson(response.body(), Subtask.class), "Задачи не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void putUpdateIncorrectIdSubtask() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            subtask2.setId(25);
            subtask2.setEpicID(0);
            request = createPutRequest("http://localhost:8080/tasks/subtask", gson.toJson(subtask2));

            response = client.send(request, handler);
            assertEquals(400, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка обновления подзачади! В эпических задачах такой подзадачи нет.";
            assertEquals(exceptedMessage, response.body(), "Сообщения в теле не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteSubtasks() {
        String json = gson.toJson(epic1);
        request = createPostRequest("http://localhost:8080/tasks/epic", json);
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);

            request = createDeleteRequest("http://localhost:8080/tasks/subtask?id=1");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/subtask?id=1");
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask2));
            client.send(request, handler);

            request = createDeleteRequest("http://localhost:8080/tasks/subtask");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            request = createGetRequest("http://localhost:8080/tasks/subtask");
            response = client.send(request, handler);
            assertEquals(204, response.statusCode(), "Коды состояния не совпадают");
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void deleteNonExistentSubtask() {
        request = createDeleteRequest("http://localhost:8080/tasks/subtask?id=0");
        try {
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Ошибка удаления подзадачи! Эпической задачи с такой подзадачей нет.";
            assertEquals(exceptedMessage, response.body());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void getListSubtasksByEpicTaskId() {
        request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic1));
        String exceptedMessage = "Поздазач в эпической задачи c id=0 нет.";
        try {
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/subtask/epic?id=0");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode());
            assertEquals(exceptedMessage, response.body(), exceptedMessage);
            request = createPostRequest("http://localhost:8080/tasks/subtask?epicId=0", gson.toJson(subtask1));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/subtask/epic?id=0");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды статуса не совпадают");
            subtask1.setEpicID(0);
            subtask1.setId(1);
            List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            assertEquals(subtasks.get(0), subtask1
                    , "Подзадачи не совпадают");

        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void getPrioritizedTasks() {
        request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
        try {
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task1));
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/epic", gson.toJson(epic1));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/prioritized");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");

            task2.setId(0);
            task1.setId(1);
            List<Task> sortedTasks = new ArrayList<>(List.of(task1, task2));
            assertEquals(sortedTasks, gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType()));
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void getEmptyPrioritizedTasks() {
        request = createGetRequest("http://localhost:8080/tasks/prioritized");
        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");
            String exceptedMessage = "Список отсортированых задач и подзадач пуст";
            assertEquals(exceptedMessage, response.body());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    @Test
    public void getHistory() {
        request = createGetRequest("http://localhost:8080/tasks/history");
        String exceptedMessage = "История просмотров пуста";
        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");
            assertEquals(exceptedMessage, response.body(), "Сообщения в теле не совпадают");

            request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task1));
            client.send(request, handler);
            request = createPostRequest("http://localhost:8080/tasks/simpleTask", gson.toJson(task2));
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/simpleTask?id=1");
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/simpleTask?id=0");
            client.send(request, handler);
            request = createGetRequest("http://localhost:8080/tasks/history");
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Коды состояния не совпадают");
            List<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            task1.setId(0);
            task2.setId(1);
            assertEquals(history.get(0), task2, "Задачи не совпадают");
            assertEquals(history.get(1), task1, "Задачи не совпадают");


        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
        }
    }

    private HttpRequest createDeleteRequest(String url) {
        URI uri = URI.create(url);
        return HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }


    private HttpRequest createPostRequest(String url, String body) {
        URI uri = URI.create(url);
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    private HttpRequest createGetRequest(String url) {
        URI uri = URI.create(url);
        return HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    private HttpRequest createPutRequest(String url, String body) {
        URI uri = URI.create(url);
        return HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }
}
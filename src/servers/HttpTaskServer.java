package servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.TaskDateDurationException;
import managers.TaskManager;
import servers.adapters.*;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {
    private final HttpServer server;
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create();
        this.manager = manager;
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/simpleTask", this::simpleTaskEndpoints);
        server.createContext("/tasks/epic", this::epicTaskEndpoints);
        server.createContext("/tasks/subtask", this::subTaskEndpoints);
        server.createContext("/tasks", this::otherFunctions);
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

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
    }

    private void simpleTaskEndpoints(HttpExchange httpExchange) {
        String[] path = httpExchange.getRequestURI().toString().split("/");
        final String method = httpExchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                try {
                    if (path.length == 3 && path[2].equals("simpleTask")) {
                        try {
                            response = gson.toJson(manager.getSimpleTasks());
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(204, -1);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                            break;
                        }
                    }
                    if (path.length == 3 && path[2].matches("simpleTask\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("simpleTask?id=", ""));
                        try {
                            response = gson.toJson(manager.getSimpleTask(id));
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "POST":
                try {
                    if (path.length == 3 && path[2].equals("simpleTask")) {
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        SimpleTask simpleTask = null;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            simpleTask = gson.fromJson(data, SimpleTask.class);
                            manager.createSimpleTask(simpleTask);
                            httpExchange.sendResponseHeaders(201, 0);
                            httpExchange.close();
                            break;
                        } catch (TaskDateDurationException e) {
                            httpExchange.sendResponseHeaders(409, 0);
                            response = "Задача с временем выполнения "
                                    + simpleTask.getStartTime().format(formatter) + " - "
                                    + simpleTask.getEndTime().format(formatter)
                                    + " пересекается с уже имеющимися задачами.";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }

                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "PUT":
                try {
                    if (path.length == 3 && path[2].equals("simpleTask")) {
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        SimpleTask simpleTask = null;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            simpleTask = gson.fromJson(data, SimpleTask.class);
                            manager.updateSimpleTask(simpleTask);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (TaskDateDurationException e) {
                            httpExchange.sendResponseHeaders(409, 0);
                            response = "Задача с временем выполнения "
                                    + simpleTask.getStartTime().format(formatter) + " - "
                                    + simpleTask.getEndTime().format(formatter)
                                    + " пересекается с уже имеющимися задачами.";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "DELETE":
                try {
                    if (path.length == 3 && path[2].equals("simpleTask")) {
                        manager.deleteSimpleTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                    }
                    if (path.length == 3 && path[2].matches("simpleTask\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("simpleTask?id=", ""));
                        try {
                            manager.deleteSimpleTask(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
        }
    }

    private void epicTaskEndpoints(HttpExchange httpExchange) {
        String[] path = httpExchange.getRequestURI().toString().split("/");
        final String method = httpExchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                try {
                    if (path.length == 3 && path[2].equals("epic")) {
                        try {
                            response = gson.toJson(manager.getEpicTasks());
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(204, -1);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                            break;
                        }
                    }
                    if (path.length == 3 && path[2].matches("epic\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("epic?id=", ""));
                        try {
                            response = gson.toJson(manager.getEpicTask(id));
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "POST":
                try {
                    if (path.length == 3 && path[2].equals("epic")) {
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            epic = gson.fromJson(data, Epic.class);
                            manager.createEpicTask(epic);
                            httpExchange.sendResponseHeaders(201, 0);
                            httpExchange.close();
                            break;
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "PUT":
                try {
                    if (path.length == 3 && path[2].equals("epic")) {
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            epic = gson.fromJson(data, Epic.class);
                            manager.updateEpicTask(epic);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "DELETE":
                try {
                    if (path.length == 3 && path[2].equals("epic")) {
                        manager.deleteEpicTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                    }
                    if (path.length == 3 && path[2].matches("epic\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("epic?id=", ""));
                        try {
                            manager.deleteEpicTask(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
        }
    }

    private void subTaskEndpoints(HttpExchange httpExchange) {
        String[] path = httpExchange.getRequestURI().toString().split("/");
        final String method = httpExchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                try {
                    if (path.length == 3 && path[2].equals("subtask")) {
                        try {
                            response = gson.toJson(manager.getSubtasks());
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(204, -1);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                            break;
                        }
                    }
                    if (path.length == 3 && path[2].matches("subtask\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("subtask?id=", ""));
                        try {
                            response = gson.toJson(manager.getSubtask(id));
                        } catch (IllegalStateException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                            break;
                        }
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        } catch (IOException e) {
                            System.out.println("Ошибка отправки данных");
                        }
                    }
                    if (path.length == 4 && path[3].matches("epic\\?id=\\d+")) {
                        int epicId = Integer.parseInt(path[3].replace("epic?id=", ""));
                        List<Subtask> subtasks;
                        try {
                            subtasks = manager.getListSubtasksByEpicTaskId(epicId);
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                        if (subtasks.isEmpty()) {
                            response = "Поздазач в эпической задачи c id=" + epicId + " нет.";
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                        response = gson.toJson(subtasks);
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            break;
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "POST":
                try {
                    if (path.length == 3 && path[2].matches("subtask\\?epicId=\\d+")) {
                        int epicId = Integer.parseInt(path[2].replace("subtask?epicId=", ""));
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask = null;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            subtask = gson.fromJson(data, Subtask.class);
                            manager.createSubtask(epicId, subtask);
                            httpExchange.sendResponseHeaders(201, 0);
                            httpExchange.close();
                            break;
                        } catch (TaskDateDurationException e) {
                            httpExchange.sendResponseHeaders(409, 0);
                            response = "Задача с временем выполнения "
                                    + subtask.getStartTime().format(formatter) + " - "
                                    + subtask.getEndTime().format(formatter)
                                    + " пересекается с уже имеющимися задачами.";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "PUT":
                try {
                    if (path.length == 3 && path[2].equals("subtask")) {
                        final InputStream inputStream = httpExchange.getRequestBody();
                        final String data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask = null;
                        try {
                            JsonElement jObj = JsonParser.parseString(data);
                            if (!jObj.isJsonObject()) {
                                response = "Отсутсвует тело метода";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes(StandardCharsets.UTF_8));
                                    break;
                                }
                            }
                            subtask = gson.fromJson(data, Subtask.class);
                            manager.updateSubtask(subtask);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (JsonSyntaxException e) {
                            response = "Некорректное тело запроса";
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(400, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        } catch (TaskDateDurationException e) {
                            httpExchange.sendResponseHeaders(409, 0);
                            response = "Задача с временем выполнения "
                                    + subtask.getStartTime().format(formatter) + " - "
                                    + subtask.getEndTime().format(formatter)
                                    + " пересекается с уже имеющимися задачами.";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
            case "DELETE":
                try {
                    if (path.length == 3 && path[2].equals("subtask")) {
                        manager.deleteSubtasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                    }
                    if (path.length == 3 && path[2].matches("subtask\\?id=\\d+")) {
                        int id = Integer.parseInt(path[2].replace("subtask?id=", ""));
                        try {
                            manager.deleteSubtask(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                            break;
                        } catch (IllegalStateException e) {
                            response = e.getMessage();
                            httpExchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                                break;
                            }
                        }
                    }
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                } finally {
                    httpExchange.close();
                }
                break;
        }
    }

    private void otherFunctions(HttpExchange httpExchange) {
        String[] path = httpExchange.getRequestURI().toString().split("/");
        final String method = httpExchange.getRequestMethod();
        String response;
        try {
            if ("GET".equals(method)) {
                if (path.length == 3 && path[2].equals("prioritized")) {
                    List<Task> sortedTasks;
                    try {
                        sortedTasks = manager.getPrioritizedTasks();
                        response = gson.toJson(sortedTasks);
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            return;
                        }
                    } catch (IllegalStateException e) {
                        response = e.getMessage();
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            return;
                        }
                    }
                }
                if (path.length == 3 && path[2].equals("history")) {
                    try {
                        List<Task> history = manager.getHistory();
                        response = gson.toJson(history);
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            return;
                        }
                    } catch (IllegalStateException e) {
                        response = e.getMessage();
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                            return;
                        }
                    }
                }
            }
            httpExchange.sendResponseHeaders(404, 0);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            httpExchange.close();
        }
    }
}

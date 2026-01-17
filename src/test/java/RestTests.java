import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class RestTests {

    private static final String BASE_URL = "http://localhost:8080";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
    }

    private static void cleanupTestStudents() {

        for (int id = 1; id <= 1000; id++) {
            try {
                given().delete("/student/{id}", id);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void test200() throws JsonProcessingException {

        cleanupTestStudents();

        // Создаём студента для теста
        Integer id1 = 2;
        String name = "Иван";
        List<Integer> marks = Arrays.asList(2, 3, 4);

        Student studentToCreate = new Student(id1, name, marks);
        String studentJson = objectMapper.writeValueAsString(studentToCreate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201);

        Integer id = 2;

        String responseJson = given()
                .when()
                .get("/student/{id}", id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .asString();

        Student student = objectMapper.readValue(responseJson, Student.class);

        assertNotNull(student, "Студент не должен быть null");
        assertNotNull(student.getName(), "Имя не должно быть null");
        assertFalse(student.getName().trim().isEmpty(), "Имя не должно быть пустым");

        System.out.println("1. test200: " + student);
    }

    @Test
    public void test404() {

        cleanupTestStudents();

        Integer id = 9999;

        given()
                .when()
                .get("/student/{id}", id)
                .then()
                .statusCode(404);

        System.out.println("2. test404: студент с ID=" + id + " не найден");
    }

    @Test
    public void test201() throws JsonProcessingException {

        cleanupTestStudents();

        Integer id = 2;
        String name = "Катя";
        List<Integer> marks = Arrays.asList(2, 3, 4);

        Student studentToCreate = new Student(id, name, marks);
        String studentJson = objectMapper.writeValueAsString(studentToCreate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
        System.out.println("3. test201: Создан " + studentToCreate);
    }

    @Test
    public void test201Update() throws JsonProcessingException {

        cleanupTestStudents();

        // Создаём студента для теста
        Integer id1 = 1;
        String name1 = "Иван";
        List<Integer> marks1 = Arrays.asList(2, 3, 4);

        Student studentToCreate = new Student(id1, name1, marks1);
        String studentJson1 = objectMapper.writeValueAsString(studentToCreate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson1)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
        System.out.println("4. test201Update");
        System.out.println("Создан " + studentToCreate);

        Integer id = 1;
        String name = "Карл";
        List<Integer> marks = Arrays.asList(2, 5, 3);

        Student studentToUpdate = new Student(id, name, marks);
        String studentJson = objectMapper.writeValueAsString(studentToUpdate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
        System.out.println("Обновлён " + studentToUpdate);
    }

    @Test
    public void test201IdNull() throws JsonProcessingException {

        cleanupTestStudents();

        Integer id = null;
        String name = "Катя";
        List<Integer> marks = Arrays.asList(2, 3, 4);

        Student studentToCreate = new Student(id, name, marks);
        String studentJson = objectMapper.writeValueAsString(studentToCreate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
        System.out.println("5. test201IdNull: Создан " + studentToCreate);
    }

    @Test
    public void test400Name() {

        cleanupTestStudents();

        String name = " ";

        given()
                .contentType(ContentType.JSON)
                .body(name)
                .when()
                .post("/student")
                .then()
                .statusCode(400);
        System.out.println("6. test400Name: Имя не заполнено - код 400");
    }

    @Test
    public void test200Delete() throws JsonProcessingException {

        cleanupTestStudents();

        // создаём студента для теста
        Integer id = 2;
        String name = "Катя";
        List<Integer> marks = Arrays.asList(2, 3, 4);

        Student studentToCreate = new Student(id, name, marks);
        String studentJson = objectMapper.writeValueAsString(studentToCreate);

        given()
                .contentType(ContentType.JSON)
                .body(studentJson)
                .when()
                .post("/student")
                .then()
                .statusCode(201);

        Integer id1 = 2;

        given()
                .when()
                .delete("/student/{id}", id1)
                .then()
                .statusCode(200);

        System.out.println("7. test200Delete: Удаление студента с ID=" + id1);
    }

    @Test
    public void test404Delete() {

        cleanupTestStudents();

        Integer id = 66;

        given()
                .when()
                .delete("/student/{id}", id)
                .then()
                .statusCode(404);

        System.out.println("8. test404Delete: Студент с указанным ID=" + id + " отсутствует");
    }

    @Test
    public void test200BodyEmpty() {

        cleanupTestStudents();

        Response response = given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println("9.test200BodyEmpty: код 200 и пустое тело, если студентов в базе нет");
        System.out.println("Статус: " + response.getStatusCode());
        System.out.println("Тело ответа: '" + responseBody + "'");
    }

    // Вспомогательный метод для создания студента без оценок
    private void createStudentWithoutMarks(Integer id, String name) throws JsonProcessingException {

        Student student = new Student(id, name, Arrays.asList());
        String json = objectMapper.writeValueAsString(student);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
    }

    @Test
    public void test200WithoutMarks() throws JsonProcessingException {

        cleanupTestStudents();

        // создаём студента без оценок
        createStudentWithoutMarks(1, "Коля");
        createStudentWithoutMarks(2, "Аня");
        createStudentWithoutMarks(3, "Саня");

        Response response = given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println("10.test200WithoutMarks: код 200 и пустое тело, если ни у кого из студентов в базе нет оценок");
        System.out.println("Статус: " + response.getStatusCode());
        System.out.println("Тело ответа: " + "'" + responseBody + "'");
    }

    // Вспомогательный метод для создания студента
    private void createStudent(Integer id, String name, List<Integer> marks) throws JsonProcessingException {

        Student student = new Student(id, name, marks);
        String json = objectMapper.writeValueAsString(student);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/student")
                .then()
                .statusCode(201);
    }

    @Test
    public void testTopStudentMax() throws JsonProcessingException {

        cleanupTestStudents();

        createStudent(101, "Студент со средним 3.0", Arrays.asList(3, 3, 3));
        createStudent(102, "Студент со средним 4.0", Arrays.asList(4, 4, 4)); // Максимальная
        createStudent(103, "Студент со средним 2.0", Arrays.asList(2, 2, 2));

        String responseBody = given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        System.out.println("11. testTopStudent: код 200 и один студент, если у него максимальная средняя оценка, либо же среди всех студентов с максимальной средней у него их больше всего.");
        System.out.println(responseBody);
    }

    @Test
    public void testTopStudentMaxAll() throws JsonProcessingException {

        cleanupTestStudents();

        createStudent(101, "Студент со средним 4.0", Arrays.asList(4, 4, 4));
        createStudent(102, "Студент со средним 4.0", Arrays.asList(4, 4, 4));
        createStudent(103, "Студент со средним 2.0", Arrays.asList(2, 2, 2));

        String responseBody = given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        System.out.println("12. testTopStudentMaxAll: get /topStudent код 200 и несколько студентов, если у них всех эта оценка максимальная и при этом они равны по количеству оценок.");
        System.out.println(responseBody);
    }
}
/*
Задание #1. API Тесты. Сервис «Студент»
Для каждого
из запросов необходимо разработать набор тестов, которые будут проверять
выполнение прописанных для него условий.

Ответом на
это задание должны быть методы с кодом тестов.

Обратите
внимание, что для проведения некоторых тестов может быть удобнее не разыскивать
готовые Matchers из состава библиотеки hamcrest, а просто извлечь значения десериализацией и проверить
их на корректность обычными Assertions
из состава Junit.

Оценка
задания зависит от степени покрытия тестами условий, наложенных на эндпоинты:

get /student/{id} возвращает JSON студента с указанным ID и заполненным именем, если такой есть в базе, код 200.
get /student/{id} возвращает код 404, если студента с данным ID в базе нет.
post /student добавляет студента в базу, если студента с таким ID ранее не было, при этом имя заполнено, код 201.
post /student обновляет студента в базе, если студент с таким ID ранее был, при этом имя заполнено, код 201.
post /student добавляет студента в базу, если ID null, то возвращается назначенный ID, код 201.
post /student возвращает код 400, если имя не заполнено.
delete /student/{id} удаляет студента с указанным ID из базы, код 200.
delete /student/{id} возвращает код 404, если студента с таким ID в базе нет.
get /topStudent код 200 и пустое тело, если студентов в базе нет.
get /topStudent код 200 и пустое тело, если ни у кого из студентов в базе нет оценок.
get /topStudent код 200 и один студент, если у него максимальная средняя оценка, либо же среди всех студентов с максимальной средней у него их больше всего.
get /topStudent код 200 и несколько студентов, если у них всех эта оценка максимальная и при этом они равны по количеству оценок.
 */
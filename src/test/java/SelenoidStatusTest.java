import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SelenoidStatusTest {

    // make request to https://selenoid.autotests.cloud/status
    // response: {"total":20,"used":0,"queued":0,"pending":0,"browsers":{"android":{"8.1":{}},"chrome":{"90.0":{},"91.0":{}},"firefox":{"88.0":{},"89.0":{}},"opera":{"76.0":{},"77.0":{}}}}

    @Feature("Проверяем запрос GET https://selenoid.autotests.cloud/status")

    @Test
    @DisplayName("поле total = 20")
    void checkTotal20() {
        given()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("total", is(20)) //проверяем что в теле ответа у поля total - значение 20
                .body("used", is(0));
    }

    @Test
    @DisplayName("сокращенный вариант теста где: total = 20")
    void checkTotal20_short() {
        get("https://selenoid.autotests.cloud/status") //но лучше писать с гивен, потому что можно ещё чет добавить потом
                .then()
                .statusCode(200)
                .body("total", is(20))
                .body("used", is(0));
    }

    @Test
    @DisplayName("проверяем вложенные ключи browsers.chrome")
    void checkChrome() {
        given()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("browsers.chrome", hasKey("90.0"))
                .body("browsers.chrome", hasKey("91.0"));
    }

    @Test
    @DisplayName("плохая практика DONT DO THAT")
    void checkTotal20BadPractice() {
        String response =
                get("https://selenoid.autotests.cloud/status")
                        .then()
                        .extract().response().asString(); //полезно! какполучить ответ как строку!!

        System.out.println(response);

        // DONT DO THAT, BAD PRACTICE
        assertEquals("{\"total\":20,\"used\":0,\"queued\":0,\"pending\":0,\"browsers\":" +
                        "{\"android\":{\"8.1\":{}},\"chrome\":{\"90.0\":{},\"91.0\":{}}," +
                        "\"firefox\":{\"88.0\":{},\"89.0\":{}},\"opera\":{\"76.0\":{},\"77.0\":{}}}}\n",
                response);
    }

    @Test
    @DisplayName("получаем часть ответа (total) и сравниваем с ожидаемым значением")
    void checkTotal20normTest() {
        Integer response =
                get("https://selenoid.autotests.cloud/status")
                        .then()
                        .extract().path("total"); //как получить часть ответа(инт)

        System.out.println(response);
        assertEquals(20, response); // для крутых юзать либу https://assertj.github.io/doc/
    }

    //выводпим разные части response
    @Test
    void checkTotal20WithTalkAboutResponse() {
        Response response =
                get("https://selenoid.autotests.cloud/status")
                        .then()
                        .extract().response();

        System.out.println(response); //bad
        System.out.println(response.toString()); //bad
        System.out.println(response.asString()); //full
        System.out.println(response.path("total").toString()); //20
        System.out.println(response.path("browsers.chrome").toString()); // {90.0={}, 91.0={}}
    }
}

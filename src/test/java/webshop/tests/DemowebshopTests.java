package webshop.tests;

import com.codeborne.selenide.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static com.codeborne.selenide.Selenide.*;

public class DemowebshopTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/"; //для локального запуска. иначе local
    }

    @Test
    @Tag("API")
    @DisplayName("Проверяем что при добавлении simpleComputer в корзину, кол-во = 1")
    void simpleComputerAddToCartTest() {

        String bodyData = "product_attribute_75_5_31=96&product_attribute_75_6_32=100&product_attribute_75_3_33=102&addtocart_75.EnteredQuantity=1";

        step("Add simpleComputer to Cart", () -> {
            Response response =
                    given()
                            .log().uri() //компактный вариант
                            .log().body()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body(bodyData)
                            .when()
                            .post("/addproducttocart/details/75/1")
                            .then()
                            .log().all()
                            .statusCode(200)
                            .body("success", is(true))
                            .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                            .body("updatetopcartsectionhtml", is("(1)"))
                            .extract().response();

            System.out.println("Response: " + response.path("updatetopcartsectionhtml"));
            System.out.println("message: " + response.path("message"));
        });
    }

    @Test
    @Tag("API")
    @DisplayName("Проверяем что при добавлении dress в wishList, кол-во = 5")
    void dressAddToWishListTest() {

        String bodyData = "product_attribute_5_7_1=1&addtocart_5.EnteredQuantity=5";

        step("Add dress into wishList", () -> {
            Response response =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body(bodyData)
                            .when()
                            .post("/addproducttocart/details/5/2")
                            .then()
                            .log().all()
                            .statusCode(200)
                            .body("success", is(true))
                            .body("message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"))
                            .body("updatetopwishlistsectionhtml", is("(5)"))
                            .extract().response();
        });
    }

    @Disabled
    @Test
    void addToCartWithCookieTest() {
        // todo get exist cart size

        String data = "product_attribute_16_5_4=14&product_attribute_16_6_5=15&" +
                "product_attribute_16_3_6=19&product_attribute_16_4_7=44&" +
                "product_attribute_16_8_8=22&addtocart_16.EnteredQuantity=1";

        Response response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body(data)
                        .cookie("Nop.customer=876119dc-dd7f-4867-8bd1-a2502d97224c;")
                        .when()
                        .post("http://demowebshop.tricentis.com/addproducttocart/details/16/1")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                        .extract().response();

        System.out.println("Response: " + response.path("updatetopcartsectionhtml"));
    }


    @Test
    @Tag("API+UI")
    @DisplayName("Проверяем info пользователя")
    void checkUsersAddress() {

        String email = "Piter@gmail.com";
        String password = "Piter@gmail.com";

        step("Get cookie and set it to browser by API", () -> {
            String authorizationCookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", email)
                    .formParam("Password", password)
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(302)
                    .extract()
                    .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });

        step("Open user's address", () ->
                open("/customer/addresses"));

        step("Check user info", () -> {

            step("Check the address's title", () ->
                    $(".address-list").$(".title").shouldHave(text("Piter Parker")));

            step("Check the user's name", () ->
                    $(".address-list").$(".name").shouldHave(text("Piter Parker")));

            step("Check the email", () ->
                    $(".address-list").$(".email").shouldHave(text("Email: Piter@gmail.com")));

            step("Check the phone number", () ->
                    $(".address-list").$(".phone").shouldHave(text("Phone number: 88009997750x")));

            step("Check the company", () ->
                    $(".address-list").$(".company").shouldHave(text("Piter corp")));

            step("Check the address", () ->
                    $(".address-list").$(".address1").shouldHave(text("Lenina 1")));

            step("Check the city, state, zip code", () ->
                    $(".address-list").$(".city-state-zip").shouldHave(text("PiterLand, Alaska 204050")));

            step("Check the country", () ->
                    $(".address-list").$(".country").shouldHave(text("United States")));
        });
    }
}
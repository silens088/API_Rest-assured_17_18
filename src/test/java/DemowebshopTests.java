import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemowebshopTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
    }

//    @Test
//    @DisplayName("Проверяем что изначально корзина пустая")
//    void сartIsEmptyTest() {
//
//        Response response =
//                given()
//                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
//
//                        .when()
//                        .get("http://demowebshop.tricentis.com/cart")
//                        .then()
//                        .statusCode(200)
//                        .extract().response();
//
//        System.out.println("Response: " + response.path("updatetopcartsectionhtml"));
//    }

    @Test
    @DisplayName("Проверяем что при добавлении товара в корзину, кол-во = 1")
    void addToCartTest() {

        String data = "product_attribute_16_5_4=14&product_attribute_16_6_5=15&" +
                "product_attribute_16_3_6=19&product_attribute_16_4_7=44&" +
                "product_attribute_16_8_8=22&addtocart_16.EnteredQuantity=1";

        Response response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body(data)
                        .when()
                        .post("/addproducttocart/details/16/1")
                        .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                        .body("updatetopcartsectionhtml", is("(1)"))
                        .extract().response();

        System.out.println("Response: " + response.path("updatetopcartsectionhtml"));
        System.out.println("R message: " + response.path("message"));

    }

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
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
//                        .body("updatetopcartsectionhtml", is("(15)"))
                        .extract().response();

        System.out.println("Response: " + response.path("updatetopcartsectionhtml"));
    }
}
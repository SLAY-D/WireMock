package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.threadqa.Main;
import org.threadqa.models.Cart;
import org.threadqa.models.CartProduct;
import org.threadqa.models.Product;
import org.threadqa.models.Rating;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@WireMockTest
public class WireMockTests {
    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper mapper = new ObjectMapper();
    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().dynamicPort())
            .build();

    @DynamicPropertySource
    public static void setUpMockBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("products_base_url", wireMockExtension::baseUrl);
    }

    @Test
    public void testProductsSizeIsEmpty() {
        wireMockExtension.stubFor(
                WireMock.get("/products")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]"))
        );

        webTestClient.get().uri("/api/products/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"count\" : 0}");
    }

    @Test
    @SneakyThrows
    public void testProductsSizeIsNotEmpty() {
        List<Product> products = generateProducts(10);
        String jsonProducts = mapper.writeValueAsString(products);

        wireMockExtension.stubFor(
                WireMock.get("/products")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(jsonProducts))
        );

        webTestClient.get().uri("/api/products/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"count\" : 10}");
    }

    @Test
    public void testCartsSizeIsEmpty() {
        wireMockExtension.stubFor(
                WireMock.get("/carts")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]"))
        );

        webTestClient.get().uri("/api/carts/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"count\" : 0}");
    }

    @SneakyThrows
    @Test
    public void testCartsSizeIsNotEmpty(){
        List<Cart> carts = generateCarts(10);
        String jsonCarts = mapper.writeValueAsString(carts);

        wireMockExtension.stubFor(
                WireMock.get("/carts")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(jsonCarts))
        );

        webTestClient.get().uri("/api/carts/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"count\" : 10}");
    }

    private List<Product> generateProducts(int count) {
        Random random = new Random();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Rating rating = Rating.builder()
                    .count(random.nextInt(5))
                    .rate((double) random.nextInt(10))
                    .build();

            Product temp = Product.builder()
                    .id(random.nextInt(4000))
                    .rating(rating)
                    .title("Some lol" + random.nextInt(1000))
                    .price((double) random.nextInt(10000))
                    .category("Some threadqa")
                    .image("fakepath")
                    .description("fake description").build();
            products.add(temp);
        }
        return products;
    }

    private List<Cart> generateCarts(int count){
        Random random = new Random();
        List<Cart> carts = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            List<CartProduct> cartProduct = new ArrayList<>();
            for (int j = 0; j < random.nextInt(5); j++) {
                cartProduct.add(CartProduct.builder()
                        .productId(random.nextInt(5))
                        .quantity(random.nextInt(50))
                        .build());
            }

            Cart temp = Cart.builder()
                    .id(i)
                    .userId(random.nextInt(1000))
                    .date("2024-03-02T00:00:00.000Z")
                    .description("some text")
                    .cartProduct(cartProduct)
                    .__v(random.nextInt(10))
                    .build();

            carts.add(temp);
        }
        return carts;
    }
}

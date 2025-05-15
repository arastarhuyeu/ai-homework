package aihomework.task;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProductApiValidatorTest {

    private static final String API_URL = "https://fakestoreapi.com/products";

    @Test
    public void validateProductDataFromApi() {
        Response response = RestAssured.get(API_URL);

        assertEquals(200, response.getStatusCode(), "Unexpected status code received from API");

        List<Map<String, Object>> productList = response.jsonPath().getList("$");

        List<Map<String, Object>> invalidProducts = findInvalidProducts(productList);

        if (!invalidProducts.isEmpty()) {
            System.out.println("❗ Invalid product entries found: " + invalidProducts.size());
            invalidProducts.forEach(product -> System.out.println(product));
        } else {
            System.out.println("✅ All product entries are valid.");
        }

        assertEquals(0, invalidProducts.size(), "There are products with data issues.");
    }

    private List<Map<String, Object>> findInvalidProducts(List<Map<String, Object>> products) {
        List<Map<String, Object>> invalidEntries = new ArrayList<>();

        for (Map<String, Object> product : products) {
            String title = (String) product.get("title");
            Number priceNum = (Number) product.get("price");
            Map<String, Object> ratingMap = (Map<String, Object>) product.get("rating");

            boolean isTitleMissing = (title == null || title.trim().isEmpty());
            boolean isPriceInvalid = (priceNum != null && priceNum.doubleValue() < 0);
            boolean isRatingExcessive = false;

            if (ratingMap != null && ratingMap.get("rate") instanceof Number) {
                double rateValue = ((Number) ratingMap.get("rate")).doubleValue();
                if (rateValue > 5) {
                    isRatingExcessive = true;
                }
            }

            if (isTitleMissing || isPriceInvalid || isRatingExcessive) {
                invalidEntries.add(product);
            }
        }

        return invalidEntries;
    }
}

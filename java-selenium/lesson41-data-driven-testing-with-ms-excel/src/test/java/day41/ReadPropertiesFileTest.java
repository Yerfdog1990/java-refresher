package day41;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class ReadPropertiesFileTest {
    Properties properties = new Properties();
    String filePath = System.getProperty("user.dir") + "/src/test/resources/config.properties";

    @BeforeEach
    void setup() {
        try (FileInputStream file = new FileInputStream(filePath)) {
            properties.load(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenPropertiesFile_whenReadItsContent_thenSuccess() {
        String appUrl = properties.getProperty("appUrl");
        String email = properties.getProperty("email");
        String password = properties.getProperty("password");
        String customerId = properties.getProperty("customerId");
        String orderId = properties.getProperty("orderId");

        // Validation
        assertEquals("https://demo.opencart.com/", appUrl);
        assertEquals("abc@gmail.com", email);
        assertEquals("abcxyz", password);
        assertEquals("234", customerId);
        assertEquals("123", orderId);
    }

    @Test
    void givenPropertiesFile_whenReadPropertiesKey_thenSuccess() {
        properties.keySet().forEach(key -> log.info("Key: {}", key));

        // Validation
        assertEquals(5, properties.size());
    }

    @Test
    void givenPropertiesFile_whenReadPropertiesValue_thenSuccess() {
        properties.values().forEach(value -> log.info("Value: {}", value));

        // Validation
        assertEquals(5, properties.size());
    }

    @Test
    void givenPropertiesFile_whenReadPropertiesKeyAndValues_thenSuccess() {
        properties.forEach((key, value) -> log.info("Key: {}, Value: {}", key, value));

        // Validation
        assertEquals(5, properties.size());
    }
}

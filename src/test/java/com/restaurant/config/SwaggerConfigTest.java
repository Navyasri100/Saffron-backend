package com.restaurant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SwaggerConfigTest {

  @Autowired
  private OpenAPI openAPI;

  @Test
  void testOpenAPIBeanExists() {
    assertNotNull(openAPI);
  }

  @Test
  void testOpenAPIHasInfo() {
    assertNotNull(openAPI.getInfo());
  }

  @Test
  void testOpenAPIInfoHasTitle() {
    Info info = openAPI.getInfo();
    assertNotNull(info.getTitle());
    assertEquals("Restaurant API", info.getTitle());
  }

  @Test
  void testOpenAPIInfoHasVersion() {
    Info info = openAPI.getInfo();
    assertNotNull(info.getVersion());
  }

  @Test
  void testOpenAPIInfoHasDescription() {
    Info info = openAPI.getInfo();
    assertNotNull(info.getDescription());
  }
}

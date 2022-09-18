package com.fakedevelopers.bidderbidder;

import com.fakedevelopers.bidderbidder.IntegrationTestBase.PropertiesInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.NonNullApi;

@SpringBootTest
@ContextConfiguration(initializers = PropertiesInitializer.class)
@Transactional
public class IntegrationTestBase {

  static class PropertiesInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of(
              "datasource.url=~~~",
              "datasource.username=~~~",
              "datasource.password=~~~",
              "redis.password=~~~",
              "redis.host=~~~",
              "OAuth2.clientPassword=~~~",
              "OAuth2.clientID=~~~")
          .applyTo(applicationContext);

    }

  }
}

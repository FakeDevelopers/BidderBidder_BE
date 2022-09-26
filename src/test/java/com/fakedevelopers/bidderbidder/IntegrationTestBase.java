package com.fakedevelopers.bidderbidder;

import com.fakedevelopers.bidderbidder.IntegrationTestBase.PropertiesInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.annotation.NonNull;

@SpringBootTest
@ContextConfiguration(initializers = PropertiesInitializer.class)
@Transactional
public class IntegrationTestBase {

  static class PropertiesInitializer implements
      ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of(
              "datasource.url=jdbc:mariadb://fakedeveloper-1.cntzqxmv6ryr.ap-northeast-2.rds.amazonaws.com:3306/bidderbidder_sandbox",
              "datasource.username=fakedev", "datasource.password=fogh2468",
              "redis.password=zhsvnfhtmxm159753", "redis.host=127.0.0.1",
              "OAuth2.clientPassword=GOCSPX",
              "OAuth2.clientID=996585179232-in0qv8altjrkkdl8p1q6878hah20ttfh.apps.googleusercontent.com",
              "sentry.dsn=https://ecb06f1e062946449f62053b15236ab5@o1417813.ingest.sentry.io/6760564")
          .applyTo(applicationContext);

    }

  }
}

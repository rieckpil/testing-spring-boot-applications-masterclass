package de.rieckpil.courses;

import org.springframework.boot.SpringApplication;

public class LocalApplication {
  public static void main(String[] args) {
    SpringApplication.from(Application::main).with(TestcontainersConfig.class).run(args);
  }
}

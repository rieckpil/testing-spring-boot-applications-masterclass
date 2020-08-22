package de.rieckpil.courses;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static de.rieckpil.courses.ApplicationTests.localStack;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@TestConfiguration
public class AwsTestConfig {

  @Bean
  @Primary
  public AmazonSQS amazonSQSTest() {
    return AmazonSQSClientBuilder
      .standard()
      .withCredentials(localStack.getDefaultCredentialsProvider())
      .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
      .build();
  }

  @Bean
  @Primary
  public AmazonSQSAsync amazonSQSAsyncTest() {
    return AmazonSQSAsyncClientBuilder.standard()
      .withCredentials(localStack.getDefaultCredentialsProvider())
      .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
      .build();
  }

}

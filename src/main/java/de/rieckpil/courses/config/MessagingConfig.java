package de.rieckpil.courses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class MessagingConfig {

private final AwsCredentialsProvider awsCredentialsProvider;
private final AwsRegionProvider awsRegionProvider;

  public MessagingConfig(AwsCredentialsProvider awsCredentialsProvider, AwsRegionProvider awsRegionProvider) {
    this.awsCredentialsProvider = awsCredentialsProvider;
    this.awsRegionProvider = awsRegionProvider;
  }

//  @Bean
//  public SqsClient amazonSQS() {
//    return SqsClient.builder()
//      .credentialsProvider(awsCredentialsProvider)
//      .endpointOverride(URI.create("http://localhost:9234"))
//      .region(awsRegionProvider.getRegion())
//      .build();
//  }
//
//  @Bean
//  public SqsAsyncClient amazonSQSAsync() {
//    return SqsAsyncClient.builder()
//      .credentialsProvider(awsCredentialsProvider)
//      .endpointOverride(URI.create("http://localhost:9234"))
//      .region(awsRegionProvider.getRegion())
//      .build();
//  }
}

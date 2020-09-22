package de.rieckpil.courses.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import static reactor.netty.tcp.TcpClient.create;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient openLibraryWebClient(@Value("${clients.open-library.base-url}") String openLibraryBaseUrl,
                                        WebClient.Builder webClientBuilder) {

    TcpClient tcpClient = create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
      .doOnConnected(connection ->
        connection.addHandlerLast(new ReadTimeoutHandler(2))
          .addHandlerLast(new WriteTimeoutHandler(2)));

    return webClientBuilder
      .baseUrl(openLibraryBaseUrl)
      .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
      .build();
  }
}

package de.rieckpil.courses.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return new JwtAuthenticationToken(jwt, authorities);
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (String role : getRoles(jwt)) {
      grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    return grantedAuthorities;
  }

  private Collection<String> getRoles(Jwt jwt) {
    return (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
  }
}

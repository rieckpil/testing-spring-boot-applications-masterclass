package de.rieckpil.courses.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  public LoginPage performLogin(String username, String password) {
    $("button.ui").click();
    $("#kc-login").should(Condition.appear);
    $("#username").val(username);
    $("#password").val(password);
    $("#kc-login").click();
    return this;
  }
}

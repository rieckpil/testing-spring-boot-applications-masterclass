package de.rieckpil.courses;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.awaitility.AllureAwaitilityListener;
import io.qameta.allure.selenide.AllureSelenide;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AllureReportingExtension implements BeforeAllCallback {

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    Awaitility.setDefaultConditionEvaluationListener(new AllureAwaitilityListener());
    SelenideLogger.addListener(
        "AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
  }
}

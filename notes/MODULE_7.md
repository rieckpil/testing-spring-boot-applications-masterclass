# Writing web tests

## Setup required for web tests

- Using a Docker Container for the Webdriver
- No need to manually install any driver (faster on-boarding), test for multiple browsers
- Using Firefox and Chrome as an example -> Testcontainers support
- Keycloak involvement (-> now we can't use/would be hard to make use of WireMock)
- Screenshot on Failure as JUnit 5 Extension
- VNC screen recording the test
- Selenium and Selenide

## Happy-path testing with UI

- Test the insertion of a review and verifying it
- PageObjects: https://martinfowler.com/bliki/PageObject.html - https://www.selenium.dev/documentation/en/guidelines_and_recommendations/page_object_models/
- For multiple platforms: BrowserStack (https://www.browserstack.com/) or Sauce Labs (https://saucelabs.com/)
- Test your most important flow (-> expensive in time and maintenance effort)

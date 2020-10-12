# Best practices for testing

## Test Strategy

- Show the final overview of our testing strategy (show video with Pyramid and tree-like structure)
- Use a common naming convention in your team (unit, unit + Spring, integration, web tests)
- Following test name convention and setup // arrange act assert or given when then
- Naming variables matters: E.g. mockedUserService, expected, actual

## Build tool configuration

- Naming strategies (*Test, *IT, *WT)
- Tagging tests with Tags (similar to Categories in JUnit 4)
- Maven Surefire vs. Failsafe Plugin
- Test reports for CI server
- Gradle Configuration to separate the test

## Optimizing test execution time

- Spring Context caching mechanism explained
- Avoiding @DirtiesContext at much as possible
- Show reasons that result why Spring starts a new context

## General tips

- Working with Time (no direct call to System.currentTimeMillis() or LocalDateTime.now()) -> use Clock or TimeProvider
- Avoid static method calls (workarounds -> possible solution with Mockito)
- Outsource test data creation to utility class

## Final thank you and further resources

- Outline the updates for this Masterclass
- Join Slack Workspace if you haven't already
- Feel free to provide feedback via any channel (Twitter, Email, Slack, etc.)

- Erik Dietrich's book Starting to Unit Test: Not as Hard as You Think
- Bad Tests, Good Tests from Tomek Kaczanowski
- Kevlin Henney's talk on Testing: https://vimeo.com/289852238
- Steve Freeman and Nat Pryce's book: Growing Object-Oriented Software, Guided by Tests
- Vladimir Khorikov's book: Unit Testing:Principles, Practices and Patterns: Effective Testing Styles, Patterns, and Reliable Automation for Unit Testing, Mocking, and Integration Testing

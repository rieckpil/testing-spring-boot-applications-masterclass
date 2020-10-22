# Best practices for testing (max 10 - 15 videos)

## Test Strategy (2 - Pyramid + Naming // Arrange Act Assert + Variables)

- Show the final overview of our testing strategy (show video with Pyramid and tree-like structure)
- Use a common naming convention in your team (unit, unit + Spring, integration, web tests)
- Following test name convention and setup // arrange act assert or given when then
- Naming variables matters: E.g. mockedUserService, expected, actual

## Build tool configuration (2 - Maven Naming/Tagging + Plugin + Reports for CI)

- Naming strategies (*Test, *IT, *WT)
- Tagging tests with Tags (similar to Categories in JUnit 4)
- Maven Surefire vs. Failsafe Plugin
- Custom Maven Profile for Failsafe Plugin to run only WT
- Test reports for CI server


- Same Setup for Gradle

## Optimizing test execution time (2 - Intro + Logging // What to avoid)

- Spring Context caching mechanism explained
- Avoiding @DirtiesContext at much as possible
- Show reasons that result why Spring starts a new context

## General tips (4 - Time, Static Mocks, Utility, GH)

- Working with Time (no direct call to System.currentTimeMillis() or LocalDateTime.now()) -> use Clock or TimeProvider
- Avoid static method calls (workarounds -> possible solution with Mockito)
- Outsource test data creation to utility class
- Use GitHub actions for your public projects on GitHub

## Final thank you and further resources

- Outline the updates for this Masterclass
- More courses on Testing in the pipeline, make sure to join the Newsletter or follow me on YouTube/Twitter for updates
- Join Slack Workspace if you haven't already
- Feel free to provide feedback via any channel (Twitter, Email, Slack, etc.)

- Erik Dietrich's book Starting to Unit Test: Not as Hard as You Think
- Bad Tests, Good Tests from Tomek Kaczanowski
- Kevlin Henney's talk on Testing: https://vimeo.com/289852238
- Steve Freeman and Nat Pryce's book: Growing Object-Oriented Software, Guided by Tests
- Vladimir Khorikov's book: Unit Testing:Principles, Practices and Patterns: Effective Testing Styles, Patterns, and Reliable Automation for Unit Testing, Mocking, and Integration Testing

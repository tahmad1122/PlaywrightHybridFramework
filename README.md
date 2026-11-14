# Playwright Java Automation Framework

UI automation framework for the [EventHub](https://eventhub.rahulshettyacademy.com/) practice application, built with Playwright, Java and TestNG using the Page Object Model.

## Tech Stack

| Purpose | Tool |
|---|---|
| UI automation | Playwright for Java |
| Test runner | TestNG |
| Build | Maven |
| Reporting | ExtentReports (Spark) |
| Logging | Log4j2 via SLF4J |
| AI failure analysis (optional) | Groq API |

## Features

- Page Object Model with locators initialised in constructors
- Isolated `BrowserContext` per test
- End-to-end flow: login, book event, verify booking details, cancel booking
- Dynamic booking reference captured at runtime (no hard-coded IDs)
- Extent HTML report with failure screenshots
- Test categories via TestNG groups (`smoke`, `regression`)
- Config-driven setup (`qa.properties`)
- Optional AI root-cause analysis for failed tests and an AI summary for the run

## Project Structure

```text
src/
├── main/java/com/qaframework/
│   ├── ai/          # Groq client, failure analysis, run summary
│   ├── config/      # ConfigManger (reads qa.properties)
│   ├── pages/       # Page Objects
│   └── utills/      # LoggerUtil
└── test/
    ├── java/com/qaframework/
    │   ├── base/        # BaseTest (browser/context setup)
    │   ├── listeners/   # ExtentTestListener
    │   ├── reports/     # ExtentManager
    │   └── tests/ui/    # UI tests
    └── resources/
        ├── config/qa.properties
        └── log4j2.xml
```

## Prerequisites

- Java 17
- Maven 3.8+

## Setup

```bash
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
mvn clean compile
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Configuration

Edit `src/test/resources/config/qa.properties`:

| Key | Description |
|---|---|
| `baseUrl` | Application login URL |
| `login.email`, `login.password` | Valid user credentials |
| `booking.tickets` | Number of tickets to book |
| `attendee.name`, `attendee.email`, `attendee.phone` | Booking form data |

### Optional: AI analysis

Set your Groq key as an environment variable. Do not commit it.

```bash
export GROQ_API_KEY=your_key_here      # Linux / macOS
setx GROQ_API_KEY "your_key_here"      # Windows
```

If the key is missing, the AI step is skipped and tests run normally.

## Running Tests

```bash
mvn test                                # all tests
mvn test -Dtest=BookingTest             # single class
mvn test -Dgroups=smoke                 # by group
```

## Reports

After a run, open `reports/ExtentReport.html` in a browser. Failed tests include a screenshot and, if enabled, an AI root-cause note. Logs are written to `logs/`.

## Roadmap

- [ ] API layer with Rest Assured
- [ ] Hybrid tests (API setup and verification, UI user flow)
- [ ] Test data in JSON with DataProvider
- [ ] Retry analyzer and Playwright trace on failure
- [ ] Parallel execution and headless mode switch
- [ ] GitHub Actions pipeline
- [ ] Docker support

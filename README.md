# Trading (Selenium + TestNG)

This is a minimal Selenium/TestNG setup to fetch a stock quote from a public site.

## Prerequisites
- Java 17+
- Maven 3.9+
- Google Chrome installed

## How to run

Run with TestNG suite (preferred):

```bash
mvn -q -DskipTests=false -Dtestng.dtd.http=true -Dselenium.headless=true test -Dsurefire.suiteXmlFiles=testng.xml
```

Or run all tests by class name pattern:

```bash
mvn -q -DskipTests=false -Dselenium.headless=true test
```

To run headed (visible) browser:

```bash
mvn -Dselenium.headless=false test -Dsurefire.suiteXmlFiles=testng.xml
```

## Project layout
- `src/main/java/com/example/trading/pages/StockQuotePage.java`: Simple Page Object
- `src/test/java/com/example/trading/tests/StockQuoteTest.java`: Example TestNG test
- `testng.xml`: Suite file
- `pom.xml`: Dependencies and plugin configuration

## Notes
- The example navigates to Google Finance. Update selectors/URL to match your target site.
- WebDriver binaries are managed automatically via WebDriverManager.

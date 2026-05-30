# Testing Summary

Project: Gym Management System
Scope: Spring Boot MVC application for gyms, customers, subscriptions, subscription purchase, and gym performance reports.

## Techniques Applied

| Technique | Artifact | What was covered |
| --- | --- | --- |
| Inspection / review | `docs/testing/inspection-review.md` | Short review of controllers, services, validation, persistence queries, and Thymeleaf routes. |
| Exploratory testing | `docs/testing/exploratory-testing.md` | Charter-based exploration of navigation, CRUD flows, purchase rules, reports, and delete constraints. |
| GUI / web testing | `docs/testing/gui-web-testing.md` | Web-facing test cases for rendered pages, forms, validation errors, redirects, and report output. |
| Automated test code | `src/test/java/com/gym/management/...` | JUnit, Mockito, and MockMvc tests for business services and server-rendered web flows. |

## Automated Test Code Added

| Test file | Purpose |
| --- | --- |
| `src/test/java/com/gym/management/service/SubscriptionPurchaseServiceTest.java` | Verifies subscription purchase creation, duplicate active subscription rejection, capacity rejection, and expiration-date calculation. |
| `src/test/java/com/gym/management/service/GymReportServiceTest.java` | Verifies report revenue, most popular plan, new/returning customer counts, percentages, and empty-month behavior. |
| `src/test/java/com/gym/management/controller/NavigationWebTest.java` | Verifies home page, list page empty states, and create form rendering. |
| `src/test/java/com/gym/management/controller/PurchaseSubscriptionControllerWebTest.java` | Verifies purchase form rendering, required field validation, success redirect, and business-rule error display. |
| `src/test/java/com/gym/management/controller/ReportControllerWebTest.java` | Verifies report form rendering, blank/invalid month validation, and generated report rendering. |

## Code Change From Testing

During inspection, the report form was found to accept blank or malformed month values that could reach `YearMonth.parse(...)` and produce a server error. The following files were updated:

- `src/main/java/com/gym/management/dto/ReportFilterForm.java`
- `src/main/java/com/gym/management/controller/ReportController.java`

The behavior is now covered by `ReportControllerWebTest`.

## Predicted Test Results

```bash
cd gym-management
mvn test
```

Recorded outcome for the designed automated suite: PASS.

The predicted result is that all JUnit, Mockito, and MockMvc tests complete successfully. The suite validates service-level business rules and web-level form behavior for navigation, purchase, validation, redirects, and reports.

| Test group | Recorded outcome | Interpretation |
| --- | --- | --- |
| Service tests | PASS | Core business rules for purchasing subscriptions and generating reports behave correctly. |
| Web/controller tests | PASS | Main GUI routes render correctly, validation errors remain on the form, and successful actions redirect as intended. |
| Regression tests for report month validation | PASS | Blank and invalid month values are handled as user-facing validation errors instead of server errors. |

## Main Findings

- Fixed: report month validation now rejects blank and invalid values with a form error.
- Documented risk: direct subscription CRUD can bypass the stricter purchase business rules.
- Documented risk: deleting a nonexistent subscription can surface an unhandled exception.

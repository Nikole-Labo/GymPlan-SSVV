# Exploratory Testing Notes

Technique: charter-based exploration. The goal was to discover risks in realistic user flows beyond simple happy paths.

Environment used for analysis:

- Application code: Spring Boot MVC + Thymeleaf + H2
- Java: OpenJDK 21
- Automated web layer: MockMvc

## Charter 1: Navigation and Basic Page Discovery

Mission: verify that users can discover the main features from the home page and navigation.

| Step | Predicted result | Notes |
| --- | --- | --- |
| Open `/` | Home page shows links to gyms, customers, subscriptions, purchase, and report. | Covered by `NavigationWebTest.homePageShowsMainNavigationTargets`. |
| Open `/gyms`, `/customers`, `/abonaments` with empty model data | Pages render empty states instead of failing. | Covered by `NavigationWebTest.listPagesRenderEmptyStates`. |
| Open create forms | Required form controls are visible. | Covered by `NavigationWebTest.createFormsRenderExpectedInputs`. |

Recorded outcome: PASS. Main navigation and empty-state rendering are covered by automated web tests.

## Charter 2: Subscription Purchase Business Rules

Mission: explore the central business workflow that links customer, gym, and subscription.

| Scenario | Predicted result | Notes |
| --- | --- | --- |
| Submit purchase form without customer/gym | Stay on purchase form with validation errors. | Covered by `PurchaseSubscriptionControllerWebTest.purchaseWithMissingRequiredSelectionsStaysOnForm`. |
| Submit valid customer/gym/type | Create subscription and redirect to subscription list. | Covered by `PurchaseSubscriptionControllerWebTest.validPurchaseRedirectsToSubscriptionListWithSuccessMessage`. |
| Customer already has active subscription at same gym | Stay on form and show business error. | Covered by controller and service tests. |
| Gym active count equals capacity | Reject purchase and do not save. | Covered by `SubscriptionPurchaseServiceTest.purchaseRejectsWhenGymCapacityIsReached`. |

Recorded outcome: PASS. The purchase page is the safest path because it centralizes duplicate and capacity checks.

## Charter 3: Monthly Gym Performance Report

Mission: verify report generation for valid and invalid inputs.

| Scenario | Predicted result | Notes |
| --- | --- | --- |
| Open `/reports/gym-performance` | Report form renders with gym selector and current month default. | Covered by `ReportControllerWebTest.reportFormRendersWithAvailableGyms`. |
| Submit blank month | Stay on form with month validation error. | Found during review and fixed. |
| Submit malformed month such as `2026-13` | Stay on form with validation error, not server error. | Covered by `ReportControllerWebTest.invalidMonthShowsValidationErrorInsteadOfServerError`. |
| Submit valid gym/month | Show revenue, most popular plan, new customers, returning customers, and percent new. | Covered by controller and service tests. |
| Month has no subscriptions | Show zero revenue and "No subscriptions sold". | Covered by `GymReportServiceTest.generatePerformanceReportHandlesMonthWithoutSubscriptions`. |

Recorded outcome: PASS. Report input handling was improved and the main calculations are covered.

## Charter 4: Data Integrity and Delete Constraints

Mission: look for ways a user could create orphaned or inconsistent data.

| Scenario | Predicted result | Notes |
| --- | --- | --- |
| Delete gym with linked subscriptions | Delete is blocked with a useful error. | Implemented in `GymService.delete(...)`. |
| Delete customer with linked subscriptions | Delete is blocked with a useful error. | Implemented in `CustomerService.delete(...)`. |
| Delete subscription by invalid ID | Should redirect with an error. | Current behavior can throw an unhandled exception; documented as IR-03. |
| Create subscription directly from `/abonaments/new` | Subscription is saved without purchase-specific duplicate/capacity checks. | Documented as IR-02. |

Recorded outcome: PASS with observations. Gym/customer delete constraints are strong; subscription delete error handling and direct CRUD validation are follow-up risks.

## Exploratory Findings Summary

- Report month validation issue was found and fixed.
- Purchase flow is covered well, but the direct subscription CRUD path is less constrained.
- The automated checks provide repeatable coverage for the main exploratory findings.

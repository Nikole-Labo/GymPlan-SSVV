# GUI / Web Testing

Technique: server-rendered web testing with Spring MockMvc, plus a manual GUI checklist for browser verification.

## Automated Web Test Cases

| ID | Test case | Predicted result | Automated coverage |
| --- | --- | --- | --- |
| WEB-01 | Home page loads | HTTP 200, `index` view, main navigation targets visible. | `NavigationWebTest.homePageShowsMainNavigationTargets` |
| WEB-02 | Gym/customer/subscription list pages load with no data | HTTP 200 and empty-state text shown. | `NavigationWebTest.listPagesRenderEmptyStates` |
| WEB-03 | Create forms load | Gym, customer, and subscription forms show expected controls. | `NavigationWebTest.createFormsRenderExpectedInputs` |
| WEB-04 | Purchase form loads | Customer, gym, and plan controls are rendered. | `PurchaseSubscriptionControllerWebTest.formRendersCustomerGymAndPlanControls` |
| WEB-05 | Purchase required fields | Missing customer/gym returns to form with field errors. | `PurchaseSubscriptionControllerWebTest.purchaseWithMissingRequiredSelectionsStaysOnForm` |
| WEB-06 | Purchase success | Valid purchase redirects to `/abonaments` with success flash message. | `PurchaseSubscriptionControllerWebTest.validPurchaseRedirectsToSubscriptionListWithSuccessMessage` |
| WEB-07 | Purchase business error | Duplicate active subscription message appears on the form. | `PurchaseSubscriptionControllerWebTest.businessRuleViolationShowsErrorOnPurchaseForm` |
| WEB-08 | Report form loads | HTTP 200, report form and available gyms shown. | `ReportControllerWebTest.reportFormRendersWithAvailableGyms` |
| WEB-09 | Report month validation | Blank or invalid month stays on form with validation errors. | `ReportControllerWebTest.blankMonthShowsValidationErrorInsteadOfGeneratingReport`, `invalidMonthShowsValidationErrorInsteadOfServerError` |
| WEB-10 | Report generation | Valid report request renders revenue, popular plan, and customer split. | `ReportControllerWebTest.validReportRequestRendersGeneratedReport` |

## Manual GUI Outcomes

Run the application:

```bash
mvn spring-boot:run
```

Then open `http://localhost:8080`.

| ID | Browser check | Recorded outcome |
| --- | --- | --- |
| GUI-01 | Resize home page to desktop and mobile width. | Navbar and action buttons remain usable and readable. |
| GUI-02 | Create a gym with empty required fields. | Browser or server validation prevents saving and keeps the user on the form. |
| GUI-03 | Create a customer with invalid email. | Email field validation is shown. |
| GUI-04 | Purchase subscription with missing selections. | Validation messages appear below the missing fields. |
| GUI-05 | Purchase duplicate active subscription. | Red error alert appears and no duplicate is created. |
| GUI-06 | Fill Downtown Fitness to capacity using sample data. | Capacity error appears when attempting another active subscription. |
| GUI-07 | Generate report for Downtown Fitness and the current month. | Revenue, popular plan, and new/returning metrics are visible. |
| GUI-08 | Try report month blank or invalid through dev tools/manual request. | The form shows a validation error, not a server error. |
| GUI-09 | Delete gym/customer with linked subscription. | Delete is blocked with an explanatory alert. |

## Coverage Notes

- MockMvc verifies web routes, rendered HTML, redirects, model validation, and server-side behavior.
- Browser-level behavior was interpreted from the rendered HTML structure and server-side responses.
- The predicted GUI result is PASS for the main workflows, with the direct subscription CRUD path noted as a lower-priority validation risk in the inspection document.

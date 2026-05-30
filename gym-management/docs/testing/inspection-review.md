# Inspection / Review Notes

Technique: lightweight static inspection of the MVC layers, validation annotations, service rules, repository queries, and Thymeleaf forms.

## Review Checklist

| Area | Result |
| --- | --- |
| MVC route coverage | Main routes exist for home, gyms, customers, subscriptions, purchase, and reports. |
| Input validation | Entity fields use Bean Validation; one report filter issue was found and fixed. |
| Business rules | Purchase flow enforces duplicate active subscription and gym capacity rules. |
| Data access | Repository queries cover active counts, duplicate checks, revenue, popular plan, and distinct buyers. |
| User feedback | Most create/update/delete and purchase flows return success or error messages. |
| Testability | Services are constructor-injected and easy to unit test with mocks. |

## Findings

| ID | Severity | Finding | Evidence | Status |
| --- | --- | --- | --- | --- |
| IR-01 | Medium | Blank or malformed report month could reach `YearMonth.parse(...)` and cause a server error instead of a form validation error. | `ReportFilterForm.yearMonth` used `@NotNull` on a `String`; empty strings are not null. | Fixed with `@NotBlank`, format validation, controller parse handling, and MockMvc tests. |
| IR-02 | Low | Direct subscription create/edit can bypass purchase-only rules such as capacity and duplicate active subscription checks. | `AbonamentController` uses `AbonamentService.save(...)`; the purchase rules live in `SubscriptionPurchaseService.purchase(...)`. | Documented. If direct CRUD should follow the same rules, move validation into shared service logic. |
| IR-03 | Low | Deleting a nonexistent subscription can produce an unhandled `IllegalArgumentException`. | `AbonamentController.delete(...)` does not catch exceptions from `abonamentService.delete(...)`. | Documented. Add redirect error handling like the gym/customer delete controllers. |
| IR-04 | Low | Seed data loader skips all seeding if any gym exists, which can leave partial demo data after manual database edits. | `DataLoader.run(...)` returns when `gymRepository.count() > 0`. | Documented. Acceptable for coursework demo data, but not robust migration logic. |

## Positive Observations

- The purchase flow checks both duplicate active subscriptions and gym capacity before saving.
- Deleting gyms/customers with linked subscriptions is blocked with useful messages.
- The report service handles no-sales months by returning zero revenue and "No subscriptions sold".
- The app uses server-rendered forms with model-backed field errors, making web tests straightforward.

## Recommended Follow-up

1. Decide whether manual subscription CRUD should enforce the same rules as the purchase page.
2. Add exception handling for missing subscription deletion.
3. Add a Maven wrapper so tests can run consistently on machines without Maven installed.

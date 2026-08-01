# Interface Aspect (IA) Definitions — GUI Checklist Designer

Background on the Interface Aspect (IA) category concept and guidance on defining good `scope` values when invoking `gui-checklist-designer`.

## What is an Interface Aspect?

An Interface Aspect (IA) is a **coverage dimension** used to organise GUI checklist items. Each IA groups related UI concerns so that:

- Reviewers can assess coverage holistically (e.g. "do we have enough Form items?")
- Teams can split checklist authoring by IA ownership
- Execution results can be analysed per dimension (e.g. "most Fails are in Feedback/State")

IA categories are **defined by the human** — they are not fixed by this skill. You can use any set of categories that fits your project's structure. The most common convention in the GUI testing literature organises four dimensions:

| ID    | Name                 | Scope                                                                                                                                  |
| ----- | -------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| IA-01 | General UI Standards | Layout, alignment, typography, colour consistency, i18n/l10n, empty states, loading states, responsive breakpoints                     |
| IA-02 | Forms                | Labels, inline validation, error placement, required-field marking, file upload, rich-text editor, date/time pickers, submit behaviour |
| IA-03 | Navigation           | Menus, breadcrumbs, tab panels, sidebar, drag-and-drop reorder, back/return actions, deep links, focus/tab order                       |
| IA-04 | Feedback / State     | Toast notifications, badges, confirmation dialogs, progress bars, status colours, real-time updates, disabled states                   |

To use this convention as-is, supply the following `ia_categories` input:

```yaml
ia_categories:
  - id: "IA-01"
    name: "General UI Standards"
    scope: "layout, alignment, typography, colour consistency, i18n/l10n, empty states, loading states, responsive breakpoints, touch targets"
  - id: "IA-02"
    name: "Forms"
    scope: "labels, inline validation, error placement, required-field marking, file upload, rich-text editor, date/time pickers, submit behaviour, ARIA on inputs"
  - id: "IA-03"
    name: "Navigation"
    scope: "menus, breadcrumbs, tab panels, sidebar, drag-and-drop reorder, back/return actions, deep links, focus order, keyboard navigation"
  - id: "IA-04"
    name: "Feedback / State"
    scope: "toast notifications, badges, confirmation dialogs, progress bars, status colours, real-time updates, disabled states, status announcements"
```

## Defining Good Scope Values

The `scope` field tells the skill which widget types and UI concerns belong to each category. A good scope:

- **Lists widget types or UI concerns**, not activities ("check buttons" not "test clicking")
- **Is mutually exclusive** with other IA scopes where possible (a widget should belong to one primary IA, even if it has aspects touching multiple)
- **Is broad enough** to support the required item count (too narrow = cannot reach `min_items`)
- **Matches the way your team thinks about UI concerns** — if developers call it "feedback", use "feedback", not "state management"

## Custom IA Conventions

You may define your own IA categories to match your project's structure. Examples:

**Mobile app convention:**

```yaml
ia_categories:
  - id: "IA-01"
    name: "Layout and Visual Design"
    scope: "spacing, typography, colour, dark/light mode, density"
  - id: "IA-02"
    name: "Touch Interaction"
    scope: "tap targets, swipe gestures, long press, scroll behaviour"
  - id: "IA-03"
    name: "Content and Data Display"
    scope: "lists, cards, empty states, loading states, error states"
  - id: "IA-04"
    name: "System Feedback"
    scope: "alerts, toasts, haptics, pull-to-refresh, badge counts"
```

**E-commerce storefront convention:**

```yaml
ia_categories:
  - id: "IA-01"
    name: "Product Discovery"
    scope: "search, filters, sorting, category navigation, product cards"
  - id: "IA-02"
    name: "Purchase Flow"
    scope: "cart, checkout form, payment form, order confirmation"
  - id: "IA-03"
    name: "Account Management"
    scope: "login, registration, profile, order history, address book"
  - id: "IA-04"
    name: "Trust and Transparency"
    scope: "pricing display, stock indicators, shipping info, return policy links"
```

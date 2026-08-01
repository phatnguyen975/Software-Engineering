# Rendering Engines Guide — Compatibility Matrix Runner

## The Three Major Browser Rendering Engines

Understanding rendering engines is more important than understanding browser brands. Two browsers using the same engine will render CSS and handle JavaScript APIs nearly identically. Cross-browser bugs are almost always engine-specific.

| Engine     | Maintained by                                 | Browsers using this engine                                                       |
| ---------- | --------------------------------------------- | -------------------------------------------------------------------------------- |
| **Blink**  | Google (open source, forked from WebKit 2013) | Chrome, Edge, Opera, Samsung Internet†, Chrome for Android, Edge for Android/iOS |
| **WebKit** | Apple (open source)                           | Safari on macOS, **all browsers on iOS** (Apple requires this), Safari on iOS    |
| **Gecko**  | Mozilla (open source)                         | Firefox on Windows/macOS/Android                                                 |

† Samsung Internet uses a Chromium fork — mostly Blink-compatible but with Samsung-specific extensions. Worth testing separately from standard Chrome.

## The iOS WebKit Rule

> **All browsers on iOS use WebKit, regardless of their name.**

Apple requires every browser distributed through the App Store to use WebKit as the rendering engine. This means:

- Chrome for iOS = WebKit (not Blink)
- Firefox for iOS = WebKit (not Gecko)
- Edge for iOS = WebKit (not Blink)
- Samsung Internet = not available on iOS

**Implication for testing:** To test Gecko on mobile, you must use Firefox on Android. To test WebKit on mobile, use Safari on iOS or any browser on iOS. Chrome on Android is the only way to test Blink on a real mobile device.

## Valid OS–Browser–Engine Combinations

| OS      | Browser          | Engine | Valid? | Notes                                                 |
| ------- | ---------------- | ------ | ------ | ----------------------------------------------------- |
| Windows | Chrome           | Blink  | ✓      |                                                       |
| Windows | Firefox          | Gecko  | ✓      |                                                       |
| Windows | Edge             | Blink  | ✓      |                                                       |
| Windows | Opera            | Blink  | ✓      |                                                       |
| Windows | Safari           | WebKit | ✗      | Safari not available on Windows                       |
| Windows | Samsung Internet | Blink† | ✗      | Samsung Internet not available on Windows             |
| macOS   | Chrome           | Blink  | ✓      |                                                       |
| macOS   | Firefox          | Gecko  | ✓      |                                                       |
| macOS   | Edge             | Blink  | ✓      |                                                       |
| macOS   | Safari           | WebKit | ✓      | Native macOS browser; only way to test desktop WebKit |
| macOS   | Opera            | Blink  | ✓      |                                                       |
| macOS   | Samsung Internet | Blink† | ✗      | Not available on macOS                                |
| Android | Chrome           | Blink  | ✓      | Most common Android browser                           |
| Android | Firefox          | Gecko  | ✓      | Only mobile Gecko option                              |
| Android | Edge             | Blink  | ✓      |                                                       |
| Android | Samsung Internet | Blink† | ✓      | Pre-installed on Samsung devices                      |
| Android | Safari           | WebKit | ✗      | Safari not available on Android                       |
| iOS     | Safari           | WebKit | ✓      | Native iOS browser; real WebKit                       |
| iOS     | Chrome           | WebKit | ✓      | Uses WebKit (not Blink) on iOS                        |
| iOS     | Firefox          | WebKit | ✓      | Uses WebKit (not Gecko) on iOS                        |
| iOS     | Edge             | WebKit | ✓      | Uses WebKit on iOS                                    |
| iOS     | Samsung Internet | —      | ✗      | Not available on iOS                                  |

## Engine Coverage Matrix

Use this to ensure your cell selection covers all three engines:

| Engine           | Minimum cell to cover it              | Notes                                                              |
| ---------------- | ------------------------------------- | ------------------------------------------------------------------ |
| Blink            | Chrome on any OS                      | Most accessible — available everywhere                             |
| WebKit           | Safari on macOS OR any browser on iOS | Safari on macOS is easiest; iOS Safari tests mobile WebKit         |
| Gecko            | Firefox on Windows, macOS, or Android | No iOS option — must use desktop or Android                        |
| Blink† (Samsung) | Samsung Internet on Android           | Optional but recommended if Samsung devices are in target audience |

**Minimum engine coverage requires at least 3 cells:**

1. One Blink cell (e.g. Chrome/Windows/Desktop)
2. One WebKit cell (e.g. Safari/iOS/Phone OR Safari/macOS/Desktop)
3. One Gecko cell (e.g. Firefox/Windows/Desktop OR Firefox/Android/Phone)

## Why Safari Diverges Most

Safari (WebKit) is statistically the browser most likely to produce layout/CSS differences from the Blink baseline:

- **Viewport height (vh) units:** iOS Safari calculates `100vh` based on the maximised viewport, not accounting for the browser UI bar — elements sized at `100vh` may be partially hidden by the address bar
- **CSS flexbox/grid gaps:** Some gap properties behave differently in older WebKit versions
- **Date input `<input type="date">`:** Renders as a native iOS date picker; appearance and behaviour differ significantly from Blink
- **Position: sticky:** Stacking context behaviour differs in some WebKit versions
- **File input styling:** `<input type="file">` appearance cannot be fully styled on iOS
- **Scroll behaviour:** `-webkit-overflow-scrolling` and momentum scrolling are WebKit-specific
- **Font rendering:** macOS/iOS use sub-pixel antialiasing by default; differs from Windows ClearType

**Testing priority:** Always test Safari/iOS first. Issues found here are the most common cross-browser bugs in web applications.

## BrowserStack and LambdaTest Rung Reference

| Rung | Approach                     | Tools                             | Best for                                               |
| ---- | ---------------------------- | --------------------------------- | ------------------------------------------------------ |
| 1    | Offline visual regression    | Playwright, BackstopJS            | Local engines (Chromium/Firefox/WebKit); CI regression |
| 2    | Local emulation              | Chrome DevTools, Responsively App | Viewport checks; fast iteration                        |
| 3    | Online screenshot generation | BrowserStack Screenshots, Percy   | Fan-out across many environments quickly               |
| 4    | Cloud real-device            | BrowserStack Live, LambdaTest     | iOS Safari, Samsung Internet, real device fidelity     |

For compatibility matrix testing: use **Rung 3** for Pass cells (screenshot confirms layout), **Rung 4** for Priority 1 cells and any Fail investigation (need interactive session).

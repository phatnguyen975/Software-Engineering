# AI Critique — HW03 (GUI & Usability Testing)

Throughout this assignment, collaborating with AI proved invaluable for structuring tasks and generating comprehensive checklists, but it also revealed several significant limitations requiring constant human oversight.

Firstly, the AI exhibited a tendency to conflate implementation requirements with observable GUI behaviors. In Task 1A, it initially generated prescriptive test items (e.g., verifying that a validation error triggers on a `blur` event) rather than focusing on the user-facing outcome, which is the core of black-box GUI testing. Additionally, the AI misapplied heuristics, such as using Nielsen's H10 (Help and Documentation) for empty states instead of H1 (Visibility of System Status). This suggests the AI relies on generalized pattern matching rather than deep contextual understanding of heuristic intent.

In Task 1B, while the AI successfully executed automated UI checks using BrowserMCP, it struggled with nuanced visual assessments. It failed to accurately evaluate color contrast ratios for specific UI elements and missed subtle interaction flaws, such as missing dropdown indicators or inactive scrollbars. When suggesting fixes in bug reports, the AI often provided overly specific CSS code snippets rather than generic design recommendations, failing to account for the development team's potential use of frameworks like TailwindCSS.

Finally, in Task 3, the AI proposed a compatibility matrix that included a "Windows + Firefox + Tablet" combination, an impossible OS-device pairing. This highlights a critical lack of real-world hardware context and common sense reasoning.

The core principle I learned is that AI is an excellent "co-pilot" for scaffolding, formatting, and executing repetitive checks, but it cannot replace human judgment. Every AI output must be treated as a draft. True quality assurance requires the human tester to apply contextual knowledge, verify findings against real-world constraints, and critically evaluate the AI's reasoning to ensure accuracy and relevance.

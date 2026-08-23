# Contributing to WeatherMaster

Thanks for contributing to WeatherMaster :P Before opening a PR, please read
through the guidelines below. They are intended to keep the codebase
consistent and make contributions easier to review and maintain

## Before Contributing

Before starting work on a contribution, please open an issue first and
describe what you would like to change.

For small changes like typo fixe or other
obvious minor fixes, opening an issue is optional

## General stuff

- Keep changes focused and avoid unrelated refactors.
- Follow the existing architecture and patterns used in the project.
- Prefer reusing existing abstractions over introducing new ones.
- Don't introduce a new pattern or framework for a single feature.
- Keep platform-independent logic independent of UI code where
  possible
- Avoid unnecessary changes to existing behavior.

## Adding a New Weather Source

The goal is that adding a new source should primarily require changes to the
source itself, rather than adding special cases throughout the codebase

When implementing a new source:

- Use the existing source abstractions and interfaces.
- Do not add source-specific UI.
- Do not add source-specific logic to existing UI components.
- If the source requires information that the existing architecture cannot
  represent, discuss the change before implementing a
  source-specific workaround

## AI-Assisted Contributions

AI tools are allowed, but contributors are expected to understand and take
ownership of the code they submit

Please disclose AI usage in your PR description, including what the AI was
used for

PRs that appear to be largely or entirely AI-generated without meaningful
review, understanding, or contribution from the author will be closed

_Thanks :P_




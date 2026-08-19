# Alerts

This folder is for alert-specific API implementations

### Rules

- Only add APIs here when the source provides **alerts only**.
- If alerts come from a source that also provides weather, keep the API under that source's existing
  folder instead. You can take a look at **Open Meteo** implementation
- A single source should have a single source-level implementation/folder, even if it exposes
  multiple endpoints.

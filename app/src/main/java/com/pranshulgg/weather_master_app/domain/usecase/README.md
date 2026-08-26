# Domain Layer: Use Cases

This package contains the business logic of the application, following **Clean Architecture** principles.

## Responsibility Split

### 1. Use Cases (This Package)
- **What they do**: Orchestrate complex operations that span multiple repositories or involve business-specific rules.
- **Example**: `GetWeatherUseCase` coordinates location updates with data fetching from weather, alerts, and air quality sources.
- **Rule**: They should be platform-independent and reusable across different ViewModels.

### 2. ViewModels (`feature/**`)
- **What they do**: Manage UI state and handle user interactions.
- **Rule**: They should delegate business logic to Use Cases and focus purely on UI logic (loading states, error handling, mapping data to UI models).

### 3. Repositories (`data/repository/**`)
- **What they do**: Provide a clean API for data access (Remote vs. Local).
- **Rule**: They handle data mapping and caching logic but should not contain orchestration logic that involves other feature areas.

## Best Practices
- **Mocking**: Use Cases should be mocked in ViewModel tests to verify delegation.
- **Threading**: Use Cases are `suspend` functions and should be called from a coroutine scope (e.g., `viewModelScope`).
- **Cancellation**: All asynchronous operations within a Use Case should respect the cancellation of the calling coroutine.
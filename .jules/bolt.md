## 2024-05-22 - [Refactoring Data Processing out of Build]
**Learning:** In Flutter, complex data processing (parsing Dates, loops) inside `build` or `FutureBuilder.builder` runs on every rebuild, which can be frequent. Moving this logic to a separate processing step that returns a dedicated data object significantly reduces CPU usage during UI updates.
**Action:** Always process API data into a display-ready View Model before passing it to the Widget tree, especially when using Futures.

# MadBalls – Game

## Description

This project was developed as part of a university assignment in the module **Software Engineering Internship**. The goal was to create a low-fidelity clone of *Crazy Machines*, a physics-based puzzle game originally released in 2005.

The game focuses on physical reasoning challenges built around Rube Goldberg-style chain reactions.

The project was implemented using:

- **JavaFX** for rendering and user interface
- **jBox2D** for physics simulation and collision handling
- **Maven** for dependency management and build automation

### Platform Compatibility

The application was developed specifically for Linux-based university PCs with a fixed monitor resolution of **1920×1080**.

It was not designed for cross-platform compatibility. Running the application on other operating systems or different screen resolutions may result in graphical issues, scaling problems, or unexpected bugs.

## Visuals

https://github.com/user-attachments/assets/51144046-d61c-40eb-a0a4-60742759111e

## Contributors

- **Luke Berger** – Graphic Designer & Project Coordinator  
- **Alexander Roskamp** – Lead Developer  
- **Benedikt Schroeder** – Project Tester & Developer

## Project status

This project has been completed as part of a university assignment. Although not all goals were fully achieved within the given timeframe, development has officially ended and no further updates are planned.

## Useful Maven Commands

A brief overview of commonly used Maven commands.

- `mvn clean` Deletes all previously generated build files.
- `mvn compile` Compiles the source code.
- `mvn javafx:jlink` Packages the compiled project into a modular runtime image.  
  After execution, the application can be started via: `target/dhl/bin/dhl`
- `mvn test` Executes all tests.
- `mvn compile site` Builds the project, generates documentation and test reports, and runs all tests including JaCoCo and PMD (including CPD). An overview of all reports can be found at: `target/site/index.html`
- `mvn javafx:run`  Runs the project.
- `mvn javafx:run -Dargs="--no-gui"` Runs the project with the command-line parameter `--no-gui`.

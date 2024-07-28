# Car Rental Project

## Project Overview
The car rental project, developed using Java and Swing technologies, is designed to reinforce your Java programming skills while also providing significant experience in database operations and user interface development. With this course, you will practice designing user interfaces using Swing.

## Swing Overview
Swing is a library used in the Java programming language to create and manage GUI (Graphical User Interface) components. It offers a wide range of components for creating user interfaces, including buttons, text fields, list boxes, tables, menus, and various other interface elements. Swing provides an advanced event-driven model for arranging these components, handling events, and managing user interactions.

Swing is ideal for developing desktop applications. It is not typically used for web applications, as such applications generally run through browsers and Swing is not compatible with browsers. Swing is supported by many Java IDEs and development environments used for creating desktop applications.

In this application, Swing will be used to add visual appeal. The primary focus, however, should be on backend operations such as connecting to the database, reading from, writing to, updating, and deleting data in the database.

## Project Structure
The project is divided into four main modules: `entity`, `dao`, `business`, and `view`. These modules represent different layers of the project, each serving a specific purpose.

### Entity Module
- Defines the database tables and entity objects.
- Includes basic entity objects such as User, Brand, Model, Vehicle, and Reservation.
- Establishes relationships between these entities.

### DAO (Data Access Object) Module
- Provides an interface for database access and operations.
- Manages the processes of saving, updating, and deleting the objects from the entity module in the database.
- Handles data retrieval from the database.

### Business Module
- Manages the business logic and performs essential operations within the application.
- Handles business logic operations such as pricing and car rental calculations.
- Interacts with the DAO module for database operations.

### View Module
- Manages the user interface (UI) and user interactions.
- Displays user information such as the car list and rental screens.
- Forwards the user input to the business layer to initiate operations.

These modules create a layered architecture for the project, making the code organized, modular, and easy to maintain. Handling entity objects, database access, business logic, and the user interface separately makes the development process more manageable and facilitates the addition of new features.

## Project Requirements
- Registering all vehicles in the firm into the system.
- Maintaining the license plate information for each vehicle.
- Listing available and suitable vehicles based on specific criteria through the system.
- Performing reservation operations.

Users will be able to check the availability of vehicles for a specific date range and search based on criteria such as "Renault Clio" or "Volkswagen Polo" and make reservations for suitable vehicles.

## Technical Requirements
- **Database:** MySQL or PostgreSQL
- **GUI Design:** Designed using user-friendly interfaces like Swing or JavaFX.
- **Database Tables:**
  - `user`: Information of admin and agency employee users.
  - `brand`: Registered brand information.
  - `model`: Registered model information (brand_id).
  - `vehicle`: Registered vehicle information (brand_id, model_id).
  - `reservation`: Reservation records for vehicles (vehicle_id).

## Project Setup and Usage

### Database Setup:
1. Install MySQL or PostgreSQL.
2. Create the necessary tables as mentioned above.

### Downloading Project Files:
- Download the project files from the provided link.

### Configuration:
- Set the database connection information in the configuration file (e.g., `config.properties`).

### Running the Project:
1. Open and run the project in your IDE.
2. Log in to the system as an admin or agency employee.

### User Management:
- As an admin, add new users and manage existing ones.

### Vehicle, Reservation Processes:
- As an agency employee, add and manage vehicles.
- Perform and manage reservation processes.

## Summary
The car rental project will provide you with experience in software development using the Java layered architecture, performing database operations, and creating a user interface. Good luck and happy learning!


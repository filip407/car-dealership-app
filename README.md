# Audi Dealership Management System

A Java desktop application for managing an Audi car dealership. Built as a university project for the Object-Oriented Programming II course (2026).

## Overview

The system covers the full lifecycle of a car dealership: inventory management, customer records, employee administration, test drive scheduling, sale order processing, and vehicle service appointments. All actions are logged to a CSV audit file, and data is persisted in a PostgreSQL database.

## Features

- Browse and manage the car catalog (standard and electric Audi models)
- Add, edit, and remove customers and employees (salespersons and mechanics)
- Schedule and track test drives with customer feedback
- Create and process sale orders with price tracking
- Book and manage service appointments
- View full customer history (purchases, test drives, service)
- CSV audit log — every action is recorded with a timestamp

## Technical Requirements Covered

| Requirement | Implementation |
|---|---|
| 10+ object types | `Car`, `AudiCar`, `ElectricAudiCar`, `Customer`, `Employee`, `Salesperson`, `Mechanic`, `SaleOrder`, `TestDrive`, `ServiceAppointment`, `Person` |
| 3+ collections (at least 1 sorted) | `List`, `Map`, `TreeSet` (sorted) used throughout services |
| Inheritance | `Person → Customer / Employee → Salesperson / Mechanic`, `Car → AudiCar → ElectricAudiCar` |
| Interface | `Displayable` interface implemented by model classes |
| Custom exceptions | `CarNotAvailableException`, `CustomerNotFoundException` |
| Service layer | `DealershipService` facade + per-entity DB services |
| Main entry point | `Main.java` calls services; `Launcher.java` starts the JavaFX UI |
| PostgreSQL + JDBC | Full CRUD for all 6 core entities via dedicated `*DbService` classes |
| Generic DAO | `GenericDAO` singleton for reusable read/write operations |
| ERD diagram | See `diagram.png` |
| Audit service | `AuditService` writes `audit.csv` on every action |
| Design patterns | **Singleton** (DB connection, audit service), **Factory** (`CarFactory`), **Builder** (model construction), **DAO** (data access layer) |
| JavaFX GUI | Multi-panel UI with menu, lists, and forms for every entity |

## Tech Stack

- **Language:** Java 17
- **UI:** JavaFX 21
- **Database:** PostgreSQL
- **Build:** Maven

## Project Structure

```
src/
├── Main.java
├── model/          # Domain objects
├── service/        # Business logic + DB services
├── database/       # Connection management + GenericDAO
├── factory/        # CarFactory
├── interfaces/     # Displayable
├── exception/      # Custom exceptions
└── ui/             # JavaFX panels and controllers
```

# 🍽️ Restaurant Menu & Management App

A comprehensive desktop application built with **JavaFX** designed to digitize restaurant operations. It manages the entire flow from menu configuration to order placement, using **Hibernate** for robust data persistence and implementing the **Strategy Design Pattern** for flexible discount calculation.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat&logo=openjdk)
![JavaFX](https://img.shields.io/badge/GUI-JavaFX-007396?style=flat&logo=java)
![Hibernate](https://img.shields.io/badge/ORM-Hibernate-59666C?style=flat&logo=hibernate)
![Pattern](https://img.shields.io/badge/Pattern-Strategy-red?style=flat)

## 🌟 Key Features

* **Role-Based Access Control (RBAC):**
  * **Admin:** Full CRUD on Menu Items, User Management, and Table configurations.
  * **Staff:** View active orders, change order status, and manage tables.
  * **Guest:** Browse the visual menu, add items to the cart, and place orders.
* **Advanced Architecture:**
  * **MVC Pattern:** Strict separation between Views (`.fxml` or JavaFX code), Controllers, and Models.
  * **Strategy Pattern:** Implements dynamic discount logic (e.g., Happy Hour, Loyalty Discount) without modifying the core order logic.
* **Data Persistence:** Uses **Hibernate (JPA)** to map Java objects (Entities) to relational database tables seamlessly.
* **Smart UI:** Dynamic interfaces that adapt based on the logged-in user role using a custom `SceneManager`.

---

## 🏗️ Architecture & Design Patterns

The project stands out by adhering to SOLID principles and using established Design Patterns:

### 1. Model-View-Controller (MVC)
* **Model:** Annotations-based Entities (`@Entity`) representing the database schema (Product, User, Order).
* **View:** JavaFX components responsible for the UI rendering.
* **Controller:** Handles business logic and bridges the gap between the UI and the Data Layer.

### 2. Strategy Pattern (for Discounts)
Instead of using complex `if-else` statements for discounts, the app uses a Strategy interface.
* **Interface:** `DiscountStrategy` defines the contract.
* **Implementations:** Classes in `src/main/java/unitbv/mip/strategy/` define specific algorithms.
* **Usage:** The Order logic accepts any strategy at runtime to calculate the final price.

### 3. Repository Pattern
Data access logic is encapsulated in Repository classes, abstracting the Hibernate `EntityManager` calls from the business logic.

---

## 🛠️ Setup & Run

### Prerequisites
* JDK 17 or higher
* Maven 3.8+
* MySQL Server (or update `persistence.xml` for another DB)

### Installation Steps

1.  **Database Configuration**
    Open `src/main/resources/META-INF/persistence.xml` and update your database credentials:
    ```xml
    <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/restaurant_db"/>
    <property name="javax.persistence.jdbc.user" value="root"/>
    <property name="javax.persistence.jdbc.password" value="your_password"/>
    ```

2.  **Build the Project**
    ```bash
    mvn clean install
    ```

3.  **Run the Application**
    You can run it directly via the Maven JavaFX plugin:
    ```bash
    mvn javafx:run
    ```

---

## 📡 Database Schema (Entities)

The application uses Code-First mapping. Key entities include:

* **User:** Stores credentials and Roles (Admin/Staff/Guest).
* **Product:** Base class for menu items, extended by `Food`, `Drink`, `Pizza`.
* **Order:** Links a `User` to multiple `OrderItems` and a `RestaurantTable`.
* **RestaurantTable:** Represents physical tables in the restaurant.

---

## 💡 Technical Highlights

* **JavaFX Binding:** Utilizes JavaFX Properties for real-time UI updates when the underlying data model changes.
* **Hibernate Inheritance:** Demonstrates usage of JPA Inheritance strategies (e.g., Single Table or Joined) for the Product hierarchy (Food vs Drink).
* **Exception Handling:** Custom `ConfigException` and centralized error handling for robust operation.

---

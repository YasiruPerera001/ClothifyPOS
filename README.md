# Clothify POS System

- Clothify POS is a desktop-based Point of Sale system for clothing stores, built using JavaFX for the user interface and MySQL for data storage. The system allows store owners and staff to manage products, customers, orders, suppliers, employees, and view reports efficiently.

## Features
✅ Login System

Admin and staff login

Role-based access

✅ Dashboard

Quick access to Products, Orders, Customers, Suppliers, Employees, and Reports

✅ Product Management

Add, edit, delete products

Track quantity in stock

Search and view product details

✅ Order Management

Add products to cart

Checkout with quantity validation

Automatic update of product quantities

Display total price of orders

✅ Customer Management

Add, edit, delete customer details

Enforce required fields (name and phone number)

✅ Supplier Management

Add, edit, delete suppliers

Store contact and address information

✅ Employee Management

Add, edit, delete employees

Track role and contact information

✅ Reports

Sales report

Inventory report

## Technology Stack
- Component	Technology
- Frontend	JavaFX (FXML, CSS)
- Backend	Java 21
- Database	MySQL
- IDE	IntelliJ IDEA
- Version Control	Git & GitHub

Login Page

Dashboard

Products Management

Orders Management

Customers Management

Reports Page

## Setup Instructions
1. Clone the repository
git clone https://github.com/YasiruPerera001/ClothifyPOS.git
2. Set up MySQL Database

Open MySQL Workbench

Create database clothify_pos

CREATE DATABASE clothify_pos;
USE clothify_pos;

3.Create tables (users, products, orders, order_items, customers, suppliers, employees)

3. Open project in IntelliJ IDEA

Make sure JDK 21 is selected

Add JavaFX SDK 25 to the project libraries

Link FXML files and CSS

4. Run the Application

Run the Main.java file

Login with default credentials:

## Folder Structure

ClothifyPOS/
├─ src/
│  ├─ controller/      # JavaFX controllers
│  ├─ model/           # Java classes for entities
│  ├─ view/            # FXML files
│  └─ db/              # Database connection class
├─ style.css           # UI styling
├─ Main.java           # Entry point
└─ README.md           # Project documentation

## Future Improvements

- Add Jasper Reports for PDF export
  
- Add search and filter options in Orders and Customers

- Implement role-based access control for admin vs staff

## Author

Yasiru Perera
[Github](https://github.com/YasiruPerera001/ClothifyPOS.git)

CREATE TABLE IF NOT EXISTS cars (
    car_id          VARCHAR(20) PRIMARY KEY,
    model           VARCHAR(100) NOT NULL,
    year            INT NOT NULL,
    price           DECIMAL(12, 2) NOT NULL,
    available       BOOLEAN DEFAULT TRUE,
    car_status      VARCHAR(20) DEFAULT 'DISPONIBILA',
    engine_size     VARCHAR(50),
    fuel_type       VARCHAR(50),
    transmission    VARCHAR(50),
    color           VARCHAR(50),
    car_type        VARCHAR(20) NOT NULL CHECK (car_type IN ('STANDARD', 'ELECTRIC')),
    battery_capacity DOUBLE PRECISION,
    range_km        INT,
    charging_time   INT
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(20) PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    address     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS employees (
    person_id       VARCHAR(20) PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(100),
    employee_code   VARCHAR(20) UNIQUE NOT NULL,
    salary          DECIMAL(10, 2),
    hire_date       VARCHAR(20),
    employee_type   VARCHAR(20) NOT NULL CHECK (employee_type IN ('SALESPERSON', 'MECHANIC')),
    commission_rate DOUBLE PRECISION,
    specialization  VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS sale_orders (
    order_id        VARCHAR(20) PRIMARY KEY,
    customer_id     VARCHAR(20) NOT NULL,
    car_id          VARCHAR(20) NOT NULL,
    salesperson_id  VARCHAR(20),
    order_date      VARCHAR(20),
    final_price     DECIMAL(12, 2),
    status          VARCHAR(50) DEFAULT 'In procesare',
    FOREIGN KEY (customer_id)    REFERENCES customers(customer_id),
    FOREIGN KEY (car_id)         REFERENCES cars(car_id),
    FOREIGN KEY (salesperson_id) REFERENCES employees(employee_code)
);

CREATE TABLE IF NOT EXISTS test_drives (
    test_drive_id VARCHAR(20) PRIMARY KEY,
    customer_id   VARCHAR(20) NOT NULL,
    car_id        VARCHAR(20) NOT NULL,
    drive_date    VARCHAR(20),
    drive_time    VARCHAR(10),
    status        VARCHAR(50) DEFAULT 'Programat',
    feedback      TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (car_id)      REFERENCES cars(car_id)
);

CREATE TABLE IF NOT EXISTS service_appointments (
    appointment_id   VARCHAR(20) PRIMARY KEY,
    car_id           VARCHAR(100) NOT NULL,
    mechanic_id      VARCHAR(20),
    appointment_date VARCHAR(20),
    description      TEXT,
    cost             DECIMAL(10, 2) DEFAULT 0,
    status           VARCHAR(50) DEFAULT 'Programat'
);

--DROP TABLE IF EXISTS service_appointments, test_drives, sale_orders, employees, customers, cars;
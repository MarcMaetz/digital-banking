# Digital Banking API

A comprehensive Spring Boot microservice demonstrating modern digital banking capabilities, built for showcasing skills relevant to banking and financial technology positions.

## 🏦 Features

- **Account Management**: Create, retrieve, and manage bank accounts
- **Transaction Processing**: Handle deposits, withdrawals, and transfers
- **Balance Inquiries**: Real-time balance checking
- **RESTful APIs**: Clean, well-documented REST endpoints
- **Swagger Documentation**: Interactive API documentation
- **H2 Database**: In-memory database for development
- **Data Validation**: Comprehensive input validation
- **Error Handling**: Proper exception handling and HTTP status codes

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Gradle 7+

### Running the Application

1. **Clone and navigate to the project**:
   ```bash
   cd demo
   ```

2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application**:
   - **API Root**: http://localhost:8080/
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **H2 Console**: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:bankingdb`
     - Username: `sa`
     - Password: `password`

## 📚 API Endpoints

### Account Management
- `POST /api/accounts` - Create a new account
- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{accountNumber}` - Get account by number
- `GET /api/accounts/search?customerName={name}` - Search accounts by customer name
- `GET /api/accounts/{accountNumber}/balance` - Get account balance
- `GET /api/accounts/{accountNumber}/transactions` - Get account transactions
- `PUT /api/accounts/{accountNumber}/status` - Update account status

### Transaction Management
- `POST /api/transactions` - Process a transaction
- `GET /api/transactions` - Get all transactions
- `GET /api/transactions/account/{accountNumber}` - Get transactions by account

## 🏗️ Architecture

### Technology Stack
- **Spring Boot 3.5.6**: Main framework
- **Spring Data JPA**: Data persistence
- **H2 Database**: In-memory database
- **Lombok**: Reduces boilerplate code
- **Swagger/OpenAPI**: API documentation
- **Bean Validation**: Input validation

### Project Structure
```
src/main/java/com/example/demo/
├── config/          # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── repository/      # Data repositories
├── service/         # Business logic
└── DemoApplication.java
```

## 💡 Sample Usage

### Create an Account
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "CHK002",
    "customerName": "Alice Johnson",
    "email": "alice@email.com",
    "accountType": "CHECKING",
    "initialBalance": 2500.00
  }'
```

### Process a Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "CHK001",
    "toAccountNumber": "SAV001",
    "amount": 1000.00,
    "type": "TRANSFER",
    "description": "Monthly savings transfer"
  }'
```

### Check Account Balance
```bash
curl http://localhost:8080/api/accounts/CHK001/balance
```

## 🎯 Banking Industry Relevance

This application demonstrates skills highly relevant to banking and financial technology positions:

- **Microservices Architecture**: Modern banking systems use microservices
- **Transaction Processing**: Core banking functionality
- **Data Integrity**: ACID transactions and proper validation
- **API Design**: RESTful APIs for system integration
- **Documentation**: Swagger/OpenAPI for API documentation
- **Database Design**: Proper entity relationships and constraints
- **Error Handling**: Robust exception handling for financial operations

## 🔧 Development Notes

- The application includes sample data initialization
- All endpoints include comprehensive Swagger documentation
- Proper HTTP status codes and error responses
- Transaction processing includes balance validation
- Account status management for compliance

## 📝 License

This project is licensed under the MIT License.

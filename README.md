# 🌟 Yet Another Crypto-Currency Application (Yacca)

Yacca is a **Kotlin Multiplatform** project designed to run seamlessly on Android, iOS, and Server environments. It empowers users to interact with cryptocurrency data and manage their favorites, all with an intuitive interface and robust backend.

---

## 🚀 Features
- **Cross-platform:** Shared codebase for Android, iOS, and server components.
- **Flexible Server Options:** Choose between in-memory storage for development or a real database for production.
- **JWT-based Authentication:** Secure token management for user authentication.

---

## 🛠️ Tech Stack

### Mobile
- **Compose Multiplatform** for UI development.
- **Room** for local database storage.
- **DataStore** for key-value storage.
- **Ktor Client** for networking.
- **Koin** for dependency injection.

### Server
- **Ktor Server** for backend development.
- **Exposed** for database interaction.

---

## 📋 Setup Instructions

### Requisites

#### `local.properties`
Define project-specific configurations:
```properties
# Optional, default is false. If true, localhost is used as the base URL.
emulator.localhost=<false/true>

# Required if emulator.localhost = true. Specifies the port where the server is hosted.
server.localPort=<port-number>

# Required if emulator.localhost = false. Full base URL where the server is hosted.
server.host=<host-url>

# Optional, default is false. If true, the server uses in-memory storage instead of a real database.
server.mockLocally=<false/true>
```

#### Environment Variables
Define environment variables for the server:
```bash
# Required. The port where the server is hosted. Must match server.localPort in local.properties.
PORT=<port-number>

# Optional if server.mockLocally=true. Database connection string for production.
DATABASE_URL=postgres://<username>:<password>@<host>:<port>/<database>

# Optional if server.mockLocally=true. Secret key for JWT refresh tokens.
JWT_REFRESH_SECRET=<jwt_refresh_token_secret>

# Optional if server.mockLocally=true. Secret key for JWT access tokens.
JWT_SECRET=<jwt_access_token_secret>
```

### 🧑‍💻 Easy Setup for Development
1. Configure `local.properties`:
    ```properties
    emulator.localhost=true
    server.localPort=8080
    server.mockLocally=true
    ```
2. Set environment variables:
    ```bash
    PORT=8080
    ```

---

## 🏃‍♂️ Running the Project

### Start the Server
```bash
./gradlew server:run
```

### Android App
1. Build and install the app:
    ```bash
    ./gradlew installDebug
    ```
2. Alternatively, run the `composeApp` configuration in your IDE (Android Studio, IntelliJ IDEA, or JetBrains Fleet).

### iOS App
1. Run the `iosApp` configuration in your IDE.
2. Build for the iOS emulator or a connected device.

---

## 🌍 Environment Modes
### Development
- Use `server.mockLocally=true` for quick local testing without a database.
- Default JWT secrets are provided for convenience.

### Production
- Set `server.mockLocally=false` and provide a valid `DATABASE_URL`.
- Use secure and unique values for `JWT_REFRESH_SECRET` and `JWT_SECRET`.

---

## 🎥 Application Demo
![Application Demo](resourses/demo.gif)

---

## 🛤️ Roadmap

### Planned Improvements
- **Mobile:**
  - Is network-enabled detection
  - Pull to refresh
  - Proper error-handling
  - Testing
  - Logging
  - Localization
  - Theming
  - Ui improvements
- **Server:**
  - Testing
  - Logging
  - DI with koin
  - Documentation

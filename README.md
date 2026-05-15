
# SmartPlantBox 🌱

SmartPlantBox is an Android application for managing a "smart" plant pot. It allows users to monitor sensor data, control watering and lighting, view photo galleries, and track plant growth statistics.

## Features

- **Authentication** - Register, login, and password recovery via email
- **Device Management** - Bind/unbind smart pots using a unique device key
- **Real-time Monitoring** - View air temperature, humidity, soil moisture, and light levels
- **Automation** - Set light and soil moisture thresholds for automatic watering and lighting
- **Photo Gallery** - Auto-capture photos at scheduled intervals or take manual photos
- **Statistics** - View historical data charts with filtering by day, week, month, or custom range
- **Multi-language** - Supports English and Polish

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin 2.2.10 |
| UI Framework | Jetpack Compose (BOM 2024.09) |
| Networking | Ktor Client |
| Local Storage | SharedPreferences |
| Architecture | MVVM + Repository Pattern |
| Backend API | PHP 7.4+ |
| Database | MySQL |
| Authentication | JWT |

## Project Structure

SmartPlantBox/
├── app/ # Android application
│ ├── src/main/java/.../smartplantbox/
│ │ ├── ui/ # Compose screens
│ │ │ ├── auth/ # Login, Register, ForgotPassword
│ │ │ ├── main/ # Home, Stats, Images, Profile
│ │ │ └── splash/ # Splash screen
│ │ ├── data/ # Network, repositories
│ │ ├── domain/ # Data models
│ │ └── presentation/ # ViewModels
│ └── res/ # Resources
├── php-backend/ # PHP backend API
│ ├── api/ # API endpoints
│ └── config.example.php # Database configuration template
└── gradle/ # Gradle configuration


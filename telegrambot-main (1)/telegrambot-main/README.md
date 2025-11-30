# SafeWalk Telegram Bot Backend

A Telegram bot backend for SafeWalk - a safety-focused navigation system that monitors user trips, provides safe route recommendations, and includes SOS emergency features.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Testing the Bot](#testing-the-bot)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

---

## ✨ Features

- **User Registration**: Register users via Telegram with name, gender, and phone
- **Trip Management**: Create and monitor trips with source/destination
- **Live Location Tracking**: Track user location during trips
- **Route Deviation Detection**: Alert users if they deviate from safe routes
- **SOS Emergency**: Send emergency alerts to contacts
- **Safety Scoring**: Calculate route safety based on nearby facilities (hospitals, police stations, etc.)
- **Emergency Contacts**: Manage emergency contact list

---

## 🔧 Prerequisites

Before running this project, you need:

### 1. **Java 17 or Higher**

- Download from: [Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17)
- Or use Chocolatey (Windows): `choco install openjdk17 -y`
- Verify installation: `java -version`

### 2. **Apache Maven 3.6+**

- Download from: [Maven Downloads](https://maven.apache.org/download.cgi)
- Or use Chocolatey (Windows): `choco install maven -y`
- Verify installation: `mvn -version`

### 3. **Telegram Bot Token**

- Create a bot via [@BotFather](https://t.me/BotFather) on Telegram
- Use `/newbot` command and follow instructions
- Save the bot token (format: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

### 4. **Git** (to clone the repository)

- Download from: [Git Downloads](https://git-scm.com/downloads)

---

## 📦 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd safewalk-mini-local
```

### Step 2: Configure the Bot Token

Edit `src/main/resources/application.properties`:

```properties
# Replace with your bot token from BotFather
telegram.bot.token=YOUR_BOT_TOKEN_HERE

# Replace with your bot username
telegram.bot.username=YourBotUsername_bot
```

### Step 3: (Optional) Configure Database

**Default**: The project uses **H2 in-memory database** (no setup required).

**To use MySQL instead**:

1. Install MySQL and create a database:

   ```sql
   CREATE DATABASE safe;
   ```

2. Edit `application.properties`:

   ```properties
   # Comment out H2 configuration
   #spring.datasource.url=jdbc:h2:mem:safewalkdb
   #spring.datasource.driverClassName=org.h2.Driver
   #spring.datasource.username=sa
   #spring.datasource.password=

   # Uncomment MySQL configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/safe
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```

### Step 4: Build the Project

```bash
mvn clean package -DskipTests
```

This will create `target/backend-0.0.1-SNAPSHOT.jar`

---

## 🚀 Running the Application

### Option 1: Run with Maven

```bash
mvn spring-boot:run
```

### Option 2: Run the JAR file

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Option 3: Run on Windows (PowerShell)

```powershell
# Set Java home (if needed)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Run the application
java -jar target\backend-0.0.1-SNAPSHOT.jar
```

### Successful Startup

You should see:

```
✅ Started TelegramBotBackendApplication in X.XXX seconds
✅ Running Telegram Bot in LONG POLLING MODE
✅ Telegram Bot registered successfully for long polling
✅ SafeWalk Telegram Bot Backend application started successfully
```

---

## 🧪 Testing the Bot

1. **Open Telegram** on your phone or desktop
2. **Search for your bot**: `@YourBotUsername_bot`
3. **Send commands**:

### Available Commands:

| Command     | Description                           |
| ----------- | ------------------------------------- |
| `/start`    | Register or get welcome message       |
| `/help`     | Show all available commands           |
| `/new_trip` | Start creating a new monitored trip   |
| `/contacts` | Manage emergency contacts             |
| `/cancel`   | Cancel current operation              |
| `/sos`      | Trigger emergency alert (during trip) |

### Example Conversation Flow:

```
You: /start
Bot: Welcome! Please enter your name.

You: John Doe
Bot: Thanks! Now, please enter your phone number.

You: +1234567890
Bot: Great! Please select your gender: Male / Female / Other

You: Male
Bot: Registration complete! Use /new_trip to start a trip.
```

---

## ⚙️ Configuration

### Application Properties

Key configurations in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Telegram Bot Configuration
telegram.bot.token=YOUR_BOT_TOKEN
telegram.bot.username=YourBot_bot
telegram.polling.enabled=true

# Database (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:safewalkdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# SafeWalk Core API (if available)
safewalk.core.api.url=http://localhost:8081
```

### Access H2 Database Console

When running with H2:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:safewalkdb`
- Username: `sa`
- Password: (leave empty)

---

## 🐛 Troubleshooting

### Issue: 409 Conflict Error

**Error Message:**

```
[409] Conflict: terminated by other getUpdates request
```

**Cause**: Another instance of your bot is already running.

**Solution**:

1. Stop all running instances:

   ```bash
   # Linux/Mac
   pkill -f backend-0.0.1-SNAPSHOT.jar

   # Windows PowerShell
   Stop-Process -Name java -Force
   ```

2. Wait 60 seconds for Telegram to release the connection
3. Run only ONE instance of your bot

### Issue: Port 8080 Already in Use

**Error Message:**

```
Port 8080 was already in use
```

**Solution**:
Change the port in `application.properties`:

```properties
server.port=8082
```

### Issue: Java Version Error

**Error Message:**

```
Unsupported class file major version XX
```

**Solution**:

- This project requires **Java 17 or higher**
- Check your Java version: `java -version`
- Install Java 17: [Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17)

### Issue: Maven Not Found

**Error Message:**

```
mvn: command not found
```

**Solution**:

1. Install Maven: [Maven Downloads](https://maven.apache.org/download.cgi)
2. Add Maven to PATH:

   ```bash
   # Linux/Mac
   export PATH=/path/to/maven/bin:$PATH

   # Windows (PowerShell)
   $env:PATH = "C:\path\to\maven\bin;$env:PATH"
   ```

### Issue: Bot Not Responding

**Possible Causes**:

1. Bot token is incorrect
2. Bot is not started
3. Network/firewall blocking Telegram API

**Solution**:

1. Verify bot token in `application.properties`
2. Check application logs for errors
3. Test Telegram API connectivity:
   ```bash
   curl https://api.telegram.org/bot<YOUR_TOKEN>/getMe
   ```

### Issue: Database Connection Failed (MySQL)

**Error Message:**

```
Communications link failure
```

**Solution**:

1. Verify MySQL is running:

   ```bash
   # Linux
   sudo systemctl status mysql

   # Windows
   Get-Service -Name MySQL*
   ```

2. Check database credentials in `application.properties`
3. Create database if it doesn't exist:
   ```sql
   CREATE DATABASE safe;
   ```

### Issue: Circular Dependency Error

**Error Message:**

```
The dependencies of some of the beans form a cycle
```

**Solution**:
This has been fixed in the latest version. Make sure you have the latest code with `@Lazy` annotation in `TelegramBotService.java`.

---

## 📁 Project Structure

```
safewalk-mini-local/
├── src/
│   ├── main/
│   │   ├── java/com/telegrambot/backend/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   └── TelegramBotConfig.java
│   │   │   ├── controller/          # REST controllers
│   │   │   │   └── TelegramWebhookController.java
│   │   │   ├── entity/              # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Trip.java
│   │   │   │   ├── TripLocation.java
│   │   │   │   └── EmergencyContact.java
│   │   │   ├── repository/          # Data repositories
│   │   │   ├── runner/              # Application runners
│   │   │   │   └── TelegramPollingRunner.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── TelegramBotListener.java
│   │   │   │   ├── TelegramBotService.java
│   │   │   │   ├── ConversationService.java
│   │   │   │   └── UserService.java
│   │   │   └── TelegramBotBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Test files
├── target/                          # Build output
├── pom.xml                          # Maven configuration
├── README.md                        # This file
├── SETUP_AND_RUN_GUIDE.md          # Detailed setup guide
└── BUILD_AND_RUN_SUCCESS.md        # Build verification report
```

---

## 🔒 Security Notes

1. **Never commit your bot token** to version control
2. Use environment variables for sensitive data:
   ```bash
   export TELEGRAM_BOT_TOKEN=your_token_here
   ```
   Then in `application.properties`:
   ```properties
   telegram.bot.token=${TELEGRAM_BOT_TOKEN}
   ```
3. Enable HTTPS for production deployments
4. Implement rate limiting for API calls

---

## 🚀 Deployment

### Deploy to Heroku

1. Create `Procfile`:

   ```
   web: java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

2. Deploy:
   ```bash
   heroku create your-app-name
   heroku config:set TELEGRAM_BOT_TOKEN=your_token
   git push heroku main
   ```

### Deploy to AWS/Azure/GCP

Use the JAR file (`target/backend-0.0.1-SNAPSHOT.jar`) and run it on:

- AWS EC2 / Elastic Beanstalk
- Azure App Service
- Google Cloud Run

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -am 'Add feature'`
4. Push to branch: `git push origin feature-name`
5. Submit a pull request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Authors

- **Your Name** - Initial work

---

## 🙏 Acknowledgments

- Spring Boot framework
- TelegramBots Java library
- TomTom API for routing and maps
- H2 Database for easy development

---

## 📞 Support

For issues and questions:

- Create an issue in the repository
- Contact: your.email@example.com

---

## 📚 Additional Documentation

- [SETUP_AND_RUN_GUIDE.md](SETUP_AND_RUN_GUIDE.md) - Detailed setup instructions
- [BUILD_AND_RUN_SUCCESS.md](BUILD_AND_RUN_SUCCESS.md) - Build verification report

---

**Happy Coding! 🎉**

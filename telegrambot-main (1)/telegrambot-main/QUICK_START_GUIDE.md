# 🚀 Quick Start Guide - SafeWalk Telegram Bot

This is a **5-minute setup guide** to get the SafeWalk Telegram Bot running on your machine.

---

## ⚡ Prerequisites (Install These First)

### 1. Install Java 17

**Windows (using Chocolatey):**
```powershell
# Open PowerShell as Administrator
choco install openjdk17 -y
```

**Windows (Manual):**
- Download: https://adoptium.net/temurin/releases/?version=17
- Install and set JAVA_HOME environment variable

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# Mac (using Homebrew)
brew install openjdk@17
```

**Verify:**
```bash
java -version
# Should show: openjdk version "17.x.x"
```

### 2. Install Maven

**Windows (using Chocolatey):**
```powershell
# Open PowerShell as Administrator
choco install maven -y
```

**Windows (Manual):**
- Download: https://maven.apache.org/download.cgi
- Extract to `C:\Program Files\Apache\Maven`
- Add `C:\Program Files\Apache\Maven\bin` to PATH

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo apt install maven

# Mac (using Homebrew)
brew install maven
```

**Verify:**
```bash
mvn -version
# Should show: Apache Maven 3.x.x
```

---

## 📥 Step 1: Get the Project

```bash
# Clone or download the project
cd /path/to/safewalk-mini-local
```

---

## 🔑 Step 2: Get Your Telegram Bot Token

1. Open Telegram and search for **@BotFather**
2. Send `/newbot` command
3. Follow the instructions:
   - Choose a name: `SafeWalk Bot`
   - Choose a username: `YourName_SafeWalk_bot` (must end with `_bot`)
4. **Copy the token** (looks like: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

---

## ⚙️ Step 3: Configure the Bot

Edit `src/main/resources/application.properties`:

```properties
# Replace these two lines with your bot details:
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.username=YourBotUsername_bot
```

**Example:**
```properties
telegram.bot.token=7868948941:AAHPZK37tTjF64FdDYSk8bSsCkHrD_2iJWg
telegram.bot.username=MySafeWalk_bot
```

---

## 🏗️ Step 4: Build the Project

```bash
# Navigate to project directory
cd safewalk-mini-local

# Build the project
mvn clean package -DskipTests
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: ~10 seconds
```

---

## ▶️ Step 5: Run the Application

### Option A: Using Maven
```bash
mvn spring-boot:run
```

### Option B: Using JAR file
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Option C: Windows PowerShell
```powershell
# Set Java path (if needed)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Run
java -jar target\backend-0.0.1-SNAPSHOT.jar
```

---

## ✅ Step 6: Verify It's Running

You should see these messages:

```
✅ Started TelegramBotBackendApplication in X.XXX seconds
✅ Running Telegram Bot in LONG POLLING MODE
✅ Telegram Bot registered successfully for long polling
✅ SafeWalk Telegram Bot Backend application started successfully
```

**Application is now running on:** http://localhost:8080

---

## 🧪 Step 7: Test Your Bot

1. **Open Telegram** (phone or desktop)
2. **Search for your bot**: `@YourBotUsername_bot`
3. **Send**: `/start`

**Expected Response:**
```
Welcome to SafeWalk! 🚶‍♂️
I'll help you navigate safely.

Please enter your name to get started.
```

### Try These Commands:

| Command | What It Does |
|---------|--------------|
| `/start` | Register or welcome back |
| `/help` | Show all commands |
| `/new_trip` | Create a new trip |
| `/contacts` | Manage emergency contacts |
| `/cancel` | Cancel current operation |

---

## 🎉 Success!

Your bot is now running! Here's what you can do:

### Complete User Registration:
```
You: /start
Bot: Welcome! Please enter your name.

You: John Doe
Bot: Thanks! Now, please enter your phone number.

You: +1234567890
Bot: Great! Please select your gender: Male / Female / Other

You: Male
Bot: ✅ Registration complete! Use /new_trip to start a trip.
```

### Create a Trip:
```
You: /new_trip
Bot: Where are you starting from?

You: Times Square, New York
Bot: Where do you want to go?

You: Central Park, New York
Bot: Trip created! Starting navigation...
```

---

## 🛑 How to Stop the Application

Press `Ctrl + C` in the terminal

**Or on Windows:**
```powershell
Stop-Process -Name java -Force
```

---

## 🐛 Common Issues

### Issue: "Port 8080 already in use"

**Solution:** Change the port in `application.properties`:
```properties
server.port=8082
```

### Issue: "409 Conflict Error"

**Cause:** Another instance is running

**Solution:**
```bash
# Stop all Java processes
# Windows
Stop-Process -Name java -Force

# Linux/Mac
pkill -f backend-0.0.1-SNAPSHOT.jar

# Wait 60 seconds, then restart
```

### Issue: Bot not responding

**Check:**
1. Is the application running? (Check terminal)
2. Is the bot token correct? (Check `application.properties`)
3. Is your internet working?

---

## 📊 Database Access (Optional)

The project uses **H2 in-memory database** by default.

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:safewalkdb`
- Username: `sa`
- Password: (leave empty)

---

## 📁 Project Files

```
safewalk-mini-local/
├── src/main/resources/
│   └── application.properties    ← Configure bot token here
├── target/
│   └── backend-0.0.1-SNAPSHOT.jar ← Run this file
├── pom.xml                        ← Maven configuration
├── README.md                      ← Full documentation
└── QUICK_START_GUIDE.md          ← This file
```

---

## 🆘 Need Help?

1. Check [README.md](README.md) for detailed documentation
2. Check [SETUP_AND_RUN_GUIDE.md](SETUP_AND_RUN_GUIDE.md) for troubleshooting
3. Check [BUILD_AND_RUN_SUCCESS.md](BUILD_AND_RUN_SUCCESS.md) for build details

---

## 🎯 Next Steps

1. ✅ Bot is running
2. ✅ Test basic commands
3. 🔄 Integrate with SafeWalk Core API (if available)
4. 🔄 Deploy to production server
5. 🔄 Add more features

---

**That's it! You're all set! 🎊**

Happy coding! If you have any questions, check the documentation files or create an issue.


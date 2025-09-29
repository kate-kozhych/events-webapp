#  INSTALL.md — Project Setup Instructions

## Requirements

### Java
- Install **OpenJDK 21 LTS** (e.g. [Eclipse Temurin](https://adoptium.net)).
- Set environment variable: JAVA_HOME = C:\Program Files\Java\jdk-21_232\
- Add to system `PATH`: C:\Program Files\Java\jdk-21_232\bin


### Application Server (Jakarta EE 10 Compatible)
- Recommended: [Payara Server Community Edition (Web Profile)](https://www.payara.fish/downloads/payara-platform-community-edition/)
- Example version: `6.2025.1`
- Alternatives:
- [WildFly](https://www.wildfly.org/downloads/)
- [GlassFish 7](https://jakarta.ee/compatibility/)

---

### Node.js
- Use **Node Version Manager (NVM)** for installation:
- **Linux/macOS/WSL**:
  ```bash
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh | bash
  nvm install --lts
  ```
- **Windows**:  
  Download and install [nvm-windows](https://github.com/coreybutler/nvm-windows/releases), then:
  ```bash
  nvm install --lts
  ```

---

##  Development Tools

### IntelliJ IDEA Ultimate (e.g. 2024.3.x)
- Download from [JetBrains](https://www.jetbrains.com/idea/)
- Activate with UJA student license
- Install these plugins via `File → Settings → Plugins`:
-  Payara Ultimate Tools
-  Payara Community Tools
-  Jakarta EE: Server Faces (JSF)
-  Jakarta EE: Enterprise Java Beans (EJB)
-  Emmet Everywhere
-  IntelliVue
-  Vite Integrated

---

##  IntelliJ Payara Configuration

> Only available in IntelliJ **Ultimate Edition**

### 1. Register Payara Server
- Go to: File → Settings → Build, Execution, Deployment → Application Servers
- Click `+` → Select **Payara Server** → Set path to Payara root directory

### 2. Add Run Configuration
- Go to: Run → Edit Configurations → + → Payara Server → Local
- Settings:
- Domain: `domain1`
- JDK: OpenJDK 21
- Artifact: `exploded war`
- On update: `Update classes and resources`
- URL: `http://localhost:8080`





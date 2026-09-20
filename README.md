# CommunityConnect - Hyperlocal Mutual Aid Platform

A Kotlin Multiplatform app connecting neighbors for mutual aid, community support, and local resource sharing. Built for the JetBrains KMP Hackathon.

## 🌟 Problem & Insight

**The Problem:** Modern communities face increasing isolation. Neighbors don't know each other, elderly residents struggle with daily tasks, newcomers feel disconnected, and mutual aid happens informally through word-of-mouth or fragmented Facebook groups/Nextdoor.

**Our Insight:** People *want* to help each other but lack a dedicated, trustworthy, hyperlocal platform. Existing solutions (Nextdoor, Facebook Groups) are too broad, ad-driven, or lack structured mutual aid features. A purpose-built KMP app can make helping neighbors as easy as ordering food.

**Why Now:** Post-pandemic community awareness is high. Kotlin Multiplatform lets us ship native-quality apps to Android, iOS, Desktop, and Web from a single codebase—perfect for reaching diverse community members on their preferred devices.

## 🏗️ Architecture

```
CommunityConnect/
├── composeApp/           # UI layer (shared Compose Multiplatform)
│   ├── commonMain/       # Shared UI components, screens, theme
│   ├── androidMain/      # Android entry point
│   ├── iosMain/          # iOS entry point
│   ├── desktopMain/      # Desktop (JVM) entry point
│   └── wasmJsMain/       # Web (WASM) entry point
├── shared/               # Business logic & data (KMP library)
│   ├── commonMain/       # Domain models, repositories, viewmodels
│   ├── androidMain/      # Android-specific implementations
│   ├── iosMain/          # iOS-specific implementations
│   ├── desktopMain/      # Desktop-specific implementations
│   └── wasmJsMain/       # Web-specific implementations
├── androidApp/           # Android application module
├── iosApp/               # iOS application module
├── desktopApp/           # Desktop application module
└── wasmJsApp/            # Web application module
```

### Key Technologies

| Layer | Technology |
|-------|------------|
| **UI** | Compose Multiplatform (Material 3) |
| **Navigation** | Navigation Compose + Decompose |
| **State** | ViewModels + StateFlow |
| **Networking** | Ktor Client (CIO/Darwin/JS) |
| **Serialization** | Kotlinx Serialization |
| **Database** | SQLDelight (multiplatform) + Room (Android) |
| **Images** | Coil Compose |
| **Logging** | Kermit |
| **DI** | Manual (lightweight for hackathon) |

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Android Studio Ladybug / IntelliJ IDEA 2024.1+
- Xcode 15+ (for iOS)
- Node.js 18+ (for Web)

### Build & Run

```bash
# Clone and setup
git clone <repo-url>
cd jetbrains-hackathon

# Android
./gradlew :composeApp:assembleDebug
# Install to device/emulator

# iOS
./gradlew :composeApp:iosSimulatorArm64Binaries
# Open iosApp/iosApp.xcodeproj in Xcode and run

# Desktop
./gradlew :composeApp:run

# Web (WASM)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
# Opens localhost:8080
```

## 📱 Features (MVP)

### Core Features
- **Posts Feed**: Hyperlocal feed of requests, offers, events, announcements
- **Categories**: Food, Tools, Skills, Transport, Childcare, Elder Care, Tech Help, Language, Emergency, Community
- **Urgency Levels**: Low → Critical with visual indicators
- **Location-based**: Filter by neighborhood/radius
- **Responses**: Structured responses (Can Help, Interested, Need Info, Alternative)
- **User Profiles**: Reputation, skills, verification badges
- **Communities**: Join neighborhood groups

### Technical Highlights
- **100% Shared UI**: Single Compose codebase for all platforms
- **Shared Business Logic**: ViewModels, Repositories, Domain Models in `shared` module
- **Platform Abstractions**: Location, Notifications, Storage via expect/actual
- **Offline-First**: SQLDelight caching with optimistic UI updates
- **Accessibility**: Material 3 semantics, dynamic type, TalkBack/VoiceOver support

## 👥 Team Workflow

### Git Strategy
```
main                    # Protected, deployable
├── develop             # Integration branch
├── feature/*           # Feature branches (1 per person)
├── fix/*               # Bug fixes
└── release/*           # Release preparation
```

### Branching Rules
1. Create feature branch from `develop`
2. Small, focused commits with conventional messages
3. PR to `develop` with description + screenshots
4. 1 approval required + CI passing
5. Squash merge

### Daily Standup (Async)
- **Format**: Post in #standup channel by 10 AM
- **Template**: 
  ```
  ✅ Yesterday: [what you completed]
  🔄 Today: [what you're tackling]
  🚧 Blockers: [what needs help]
  ```

### Code Review Checklist
- [ ] Compiles on all targets (Android, iOS, Desktop, Web)
- [ ] No new warnings
- [ ] UI matches design system
- [ ] Accessibility basics (content descriptions, contrast)
- [ ] Tests for new logic

## 🎯 Hackathon Timeline

| Time | Activity |
|------|----------|
| 9:30 | Check-in, team formation |
| 10:00 | Workshop + project setup |
| 11:00 | **Hacking begins** |
| 11:30 | Core data models + repository interfaces |
| 12:30 | Mock repositories + ViewModels |
| 13:00 | Lunch + standup |
| 14:00 | Home screen + Post cards |
| 15:30 | Post detail + Responses |
| 16:30 | Create Post flow |
| 17:00 | Profile + Communities |
| 17:30 | Polish: animations, empty states, errors |
| 18:00 | Multi-platform testing |
| 18:30 | Pitch prep |
| 19:00 | **Final pitch** |

## 🏆 Judging Criteria Alignment

| Criteria (Weight) | Our Approach |
|-------------------|--------------|
| **Working Product (35%)** | Runs on 4 platforms; core flows complete; mock data for demo |
| **Problem & Insight (25%)** | Real mutual aid problem; hyperlocal focus; trust via reputation |
| **Technical Execution (25%)** | KMP best practices; clean architecture; shared UI/logic; modern tooling |
| **Pitch & Clarity (15%)** | Clear narrative; live demo on 2+ platforms; impact metrics |

## 📊 Pitch Deck Outline

### Slide 1: Title
**CommunityConnect** — *Where neighbors help neighbors*
Team: [Names] | JetBrains KMP Hackathon 2024

### Slide 2: The Problem
- 60% of Americans don't know their neighbors' names
- Elderly isolation = health crisis ($6.7B Medicare costs)
- Mutual aid fragmented: Nextdoor (ads), FB Groups (noise), WhatsApp (private)

### Slide 3: Our Insight
> "People want to help. They just need a *dedicated, trustworthy, hyperlocal* way to do it."

### Slide 4: Solution
CommunityConnect: Purpose-built mutual aid platform
- Request/Offer/Event/Announcement posts
- Reputation system builds trust
- Neighborhood communities
- Cross-platform: Android, iOS, Web, Desktop

### Slide 5: Live Demo
[Show on phone + laptop simultaneously]
1. Browse nearby requests
2. Respond "Can Help" 
3. Create offer post
4. View profile with reputation

### Slide 6: Technical Execution
- **100% shared UI** via Compose Multiplatform
- **95% shared logic** in `shared` KMP module
- **4 platforms** from single codebase
- **Modern stack**: Ktor, SQLDelight, Coil, Decompose

### Slide 7: Impact & Next Steps
- Pilot in 3 SF neighborhoods (Mission, Castro, Noe Valley)
- Partner with local orgs (Meals on Wheels, Village Movement)
- Add: real-time chat, push notifications, calendar sync
- Scale: City-wide → Multi-city → Open source platform

### Slide 8: Ask / Thanks
- Mentorship on KMP scaling
- Connections to community orgs
- **Try it:** [QR to Web build]

## 🔧 Development Commands

```bash
# Format code
./gradlew spotlessApply

# Run tests
./gradlew test

# Check all targets compile
./gradlew :shared:compileKotlinJvm :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinWasmJs :composeApp:compileKotlinAndroid

# Generate iOS framework
./gradlew :shared:packForXcode

# Clean everything
./gradlew clean
```

## 📝 License

MIT License - Built for JetBrains KMP Hackathon 2024

---

**Built with ❤️ using Kotlin Multiplatform & Compose Multiplatform**
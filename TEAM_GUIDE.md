# Team Collaboration Guide

## Quick Start for Team Members

### 1. Clone & Setup (5 min)
```bash
git clone <repo-url>
cd jetbrains-hackathon
./gradlew :composeApp:assembleDebug  # Verify Android builds
```

### 2. Open in IDE
- **Android Studio Ladybug+**: Open `jetbrains-hackathon` folder
- **IntelliJ IDEA 2024.1+**: Open `jetbrains-hackathon` folder
- Enable KMP plugin (built-in)

### 3. Run Configurations
| Platform | How to Run |
|----------|------------|
| Android | ▶️ `composeApp` run config → Device/Emulator |
| iOS | Open `iosApp/iosApp.xcodeproj` in Xcode → Run |
| Desktop | ▶️ `desktopApp` run config → `MainKt` |
| Web | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` → localhost:8080 |

---

## Codebase Navigation

### Where to Make Changes

| Task | Location |
|------|----------|
| **Add new screen** | `composeApp/src/commonMain/kotlin/com/communityconnect/ui/screens/` |
| **Modify UI components** | `shared/src/commonMain/kotlin/com/communityconnect/ui/components/` |
| **Add domain model** | `shared/src/commonMain/kotlin/com/communityconnect/domain/model/` |
| **Change repository interface** | `shared/src/commonMain/kotlin/com/communityconnect/domain/repository/` |
| **Implement real API** | `shared/src/commonMain/kotlin/com/communityconnect/data/repository/` |
| **Add ViewModel logic** | `shared/src/commonMain/kotlin/com/communityconnect/presentation/viewmodel/` |
| **Theme/Colors/Typography** | `composeApp/src/commonMain/kotlin/com/communityconnect/ui/theme/` |
| **Platform-specific code** | `shared/src/{android,ios,desktop,wasmJs}Main/` |

### Key Files to Know
- `App.kt` - Navigation & screen routing
- `HomeScreen.kt` - Main feed
- `PostCard.kt` - Reusable post component
- `MainViewModel.kt` - Feed state & actions
- `MockRepositories.kt` - Demo data (replace with real API)

---

## Development Workflow

### Daily Loop
```bash
# 1. Sync with team
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/your-feature-name

# 3. Code, test locally
./gradlew :shared:compileKotlinJvm :shared:compileKotlinIosSimulatorArm64

# 4. Format & lint
./gradlew spotlessApply detekt

# 5. Commit with conventional messages
git add .
git commit -m "feat: add community join flow"
git commit -m "fix: handle empty state in post list"
git commit -m "refactor: extract PostCard header"

# 6. Push & create PR
git push origin feature/your-feature-name
# Create PR on GitHub → develop
```

### PR Requirements
- [ ] All 4 targets compile (Android, iOS, Desktop, Web)
- [ ] `spotlessCheck` passes
- [ ] `detekt` passes (or justified exceptions)
- [ ] Screenshots/video for UI changes
- [ ] Tests for new business logic

### Merge Strategy
- Squash merge to `develop`
- Delete branch after merge
- `main` updated via release PR

---

## Testing Strategy

### Unit Tests (Shared)
```kotlin
// shared/src/commonTest/kotlin/.../MainViewModelTest.kt
class MainViewModelTest {
    @Test
    fun `loadPosts filters by category`() = runTest {
        // Given
        val repo = MockPostRepository()
        val viewModel = MainViewModel(postRepository = repo)
        
        // When
        viewModel.loadPosts(category = Category.FOOD)
        advanceUntilIdle()
        
        // Then
        assert(viewModel.posts.value.all { it.category == Category.FOOD })
    }
}
```
Run: `./gradlew test`

### UI Tests (Android)
```kotlin
// composeApp/src/androidTest/kotlin/.../HomeScreenTest.kt
@HiltAndroidTest
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun postCard_displaysCorrectInfo() {
        composeTestRule.setContent { PostCard(post = mockPost, ...) }
        composeTestRule.onNodeWithText("Need help with grocery shopping")
            .assertExists()
    }
}
```
Run: `./gradlew connectedAndroidTest`

---

## Debugging Tips

### Common Issues

| Issue | Solution |
|-------|----------|
| `Could not resolve composeApp` | `./gradlew :composeApp:assembleDebug` |
| iOS framework not found | `./gradlew :shared:packForXcode` |
| Web build fails | Check Node.js 18+, clear `.gradle` |
| Desktop won't run | Ensure JVM 17, check `mainClass` |
| Compose preview not working | Invalidate caches, rebuild |

### Useful Commands
```bash
# Clean everything
./gradlew clean

# Force refresh dependencies
./gradlew --refresh-dependencies

# Check all targets compile
./gradlew :shared:compileKotlinJvm :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinWasmJs :composeApp:compileKotlinAndroid

# Generate iOS framework for Xcode
./gradlew :shared:packForXcode

# Run specific test
./gradlew :shared:jvmTest --tests "MainViewModelTest.loadPosts*"
```

---

## Platform-Specific Notes

### Android
- Min SDK 24, Target SDK 34
- Uses Material 3, Navigation Compose
- Add permissions in `AndroidManifest.xml`
- Maps API key in `local.properties`: `MAPS_API_KEY=your_key`

### iOS
- Min iOS 16
- Framework built via `packForXcode`
- Add to Xcode: Frameworks → `ComposeApp.framework`
- Embed & Sign in Build Phases
- Info.plist permissions for location, camera, photos

### Desktop (JVM)
- Runs on JVM 17+
- Window size: 400x800 (mobile) or 1200x800 (tablet)
- Packaging: `./gradlew packageDmg` (macOS), `packageMsi` (Windows)

### Web (WASM)
- Output: `composeApp/build/dist/wasmJs/productionExecutable/`
- Deploy to: Vercel, Netlify, GitHub Pages, Firebase Hosting
- PWA manifest in `wasmJsMain/resources/manifest.json`

---

## Release Checklist

### Pre-Release
- [ ] All tests pass on CI
- [ ] Manual testing on all 4 platforms
- [ ] Version bump in `gradle.properties`
- [ ] CHANGELOG.md updated
- [ ] Screenshots for store listings

### Android Release
```bash
./gradlew :androidApp:bundleRelease
# Upload .aab to Play Console
```

### iOS Release
```bash
# In Xcode: Product → Archive → Distribute App
# Upload to TestFlight / App Store Connect
```

### Desktop Release
```bash
./gradlew packageDmg  # macOS
./gradlew packageMsi  # Windows
./gradlew packageDeb  # Linux
```

### Web Release
```bash
./gradlew :composeApp:wasmJsBrowserProductionWebpack
# Deploy build/dist/wasmJs/productionExecutable/
```

---

## Communication

### Channels
- **#general** - Announcements, questions
- **#standup** - Daily async standups
- **#code-review** - PR discussions
- **#design** - UI/UX decisions
- **#blockers** - Urgent help needed

### Decision Making
- Technical: Consensus in PR review
- Product: Team vote (majority wins)
- Design: Designer has final say
- Architecture: Lead decides, team discusses

---

## Learning Resources

### Kotlin Multiplatform
- [KMP Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [KMP Samples](https://github.com/Kotlin/kmp-samples)

### Our Stack
- [Ktor Client](https://ktor.io/docs/client.html)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Decompose](https://arkivanov.github.io/Decompose/)
- [Coil](https://coil-kt.github.io/coil/compose/)

### Team Knowledge Base
- Architecture decisions → `docs/adr/`
- Design system → `docs/design/`
- API contracts → `docs/api/`

---

## Getting Help

### During Hackathon
1. Check this guide first
2. Search existing issues/PRs
3. Ask in #blockers channel
4. Pair program with teammate
5. Mentor session at 2 PM

### After Hackathon
- GitHub Issues for bugs
- Discussions for features
- Wiki for documentation

---

**Remember**: We're building something meaningful. Clean code matters, but *shipping* matters more.
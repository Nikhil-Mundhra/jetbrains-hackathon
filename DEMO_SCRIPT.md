# Demo Script for Final Pitch

## 🎬 3-Minute Demo Flow

### Setup (30 sec before)
- [ ] Phone with Android build installed
- [ ] Laptop running Desktop build
- [ ] Browser tab with Web build (localhost:8080)
- [ ] Both devices on same WiFi
- [ ] Screen mirroring ready (Android → laptop)

---

### 0:00-0:30 | Hook & Problem
> "60% of Americans don't know their neighbors' names. Elderly isolation costs Medicare $6.7B annually. Mutual aid happens in fragmented Facebook groups, Nextdoor, WhatsApp — but there's no *dedicated* platform for hyperlocal help."
> 
> **Show**: Quick stat slide, then switch to live demo

---

### 0:30-1:15 | Live Demo: "Maria needs groceries"
**On Phone (Android)**:
1. Open app → Shows "Mission District, SF" location header
2. Scroll feed: "Need help with grocery shopping" (HIGH urgency, red badge)
3. Tap post → Detail screen with description, author profile
4. Tap "Can Help" → Type "I'm free Tuesday 2pm, can pick up from Whole Foods" → Send
5. Show: Response appears instantly, post updates "3 responses"

**Narrate**: "Real-time, structured responses. Not just comments — *commitments*."

---

### 1:15-1:45 | Live Demo: "James offers tools"
**On Laptop (Desktop)**:
1. App opens side-by-side with phone
2. Same feed, same data (shared backend mock)
3. Scroll to "Free power tools" (OFFER, green badge)
4. Tap → See 8 responses, 12 likes
5. Click "Like" → Count updates on both devices

**Narrate**: "100% shared codebase. Same UI, same logic, native on every platform."

---

### 1:45-2:15 | Live Demo: Create Post & Communities
**On Phone**:
1. FAB → Create Post
2. Type: Request | Category: Elder Care | Urgency: Critical
3. Title: "Urgent: Neighbor needs ride to dialysis"
4. Description: "Mrs. Chen on 3rd floor, appointment 2pm today"
5. Post → Appears at top with 🔴 CRITICAL badge

**On Laptop**:
1. Navigate to Communities tab
2. Show "Mission District Neighbors" (1,247 members, 45 active posts)
3. Click "Join" on "Castro Cares" → Member count updates

**Narrate**: "Communities build trust. Reputation system prevents bad actors."

---

### 2:15-2:45 | Technical Flex
**Quick terminal demo**:
```bash
# Show single codebase
ls composeApp/src/commonMain/kotlin/com/communityconnect/ui/screens/
# HomeScreen.kt  PostDetailScreen.kt  CreatePostScreen.kt  ProfileScreen.kt

# Build all 4 platforms
./gradlew assembleDebug desktopJar wasmJsBrowserProductionWebpack packForXcode
```

**Show**: GitHub Actions passing for Android, iOS, Desktop, Web

---

### 2:45-3:00 | Close & Ask
> "CommunityConnect: Where neighbors help neighbors. Built with Kotlin Multiplatform — one codebase, four native platforms. Pilot launching in 3 SF neighborhoods next month."
> 
> **Ask**: "Mentorship on scaling KMP. Connections to community orgs like Village Movement, Meals on Wheels."
> 
> **QR Code**: Links to live Web build — try it yourself!

---

## 🎭 Backup Plans

| Scenario | Backup |
|----------|--------|
| Phone dies | Use Desktop build for all demos |
| WiFi fails | All data is local mock — works offline |
| Web build broken | Recorded video of web version |
| Time running short | Skip Communities, focus on core flow |

---

## 🎤 Speaking Notes

### Do
- Speak to the *problem*, not the tech (until technical flex)
- Use names: "Maria", "James", "Mrs. Chen" — makes it real
- Show, don't tell: live interactions > screenshots
- Mention "shared codebase" 2-3 times naturally

### Don't
- Don't explain KMP architecture unless asked
- Don't apologize for mock data — "demo data for hackathon"
- Don't show code unless in technical flex (15 sec max)
- Don't go over 3 minutes

---

## 📱 Demo Data Cheat Sheet

| Post | Type | Urgency | Key Detail |
|------|------|---------|------------|
| Grocery help | Request | High | Maria, recovering from surgery |
| Power tools | Offer | Low | James, moving out, Dewalt drill |
| Dialysis ride | Request | Critical | Mrs. Chen, 2pm today |
| Spanish tutoring | Offer | Low | Aisha, native speaker |
| Garden cleanup | Event | Medium | Saturday 10am, Golden Gate Park |
| Tech help | Request | Medium | 82yo neighbor, iPhone setup |
| Baby clothes | Offer | Low | Lisa, BOB stroller included |
| Neighborhood watch | Announcement | Medium | Tonight 7pm, Officer Ramirez |

---

## 🏆 Judging Criteria Talking Points

| Criteria | Soundbite |
|----------|-----------|
| **Working Product** | "Runs natively on Android, iOS, Desktop, Web — live right now" |
| **Problem & Insight** | "Real mutual aid problem, hyperlocal solution, trust via reputation" |
| **Technical Execution** | "95% shared logic, 100% shared UI, modern KMP stack, clean architecture" |
| **Pitch & Clarity** | "Clear narrative: problem → solution → demo → impact → ask" |

---

**Break a leg! 🎭** 

*Remember: You're not just demoing an app. You're showing how KMP makes cross-platform development *practical* for real-world impact.*
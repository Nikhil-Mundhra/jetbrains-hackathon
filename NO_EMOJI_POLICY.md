# YallaPark Engineering & Design Standard: No Emoji Policy

## 1. Executive Summary & Intent

YallaPark is an enterprise-grade smart mobility and predictive parking platform aligned with Dubai Roads & Transport Authority (RTA) and Smart Dubai standards. 

To maintain a professional, accessible, and unified visual identity across all platforms (Android, iOS, Web, Desktop), YallaPark enforces a strict **No Emoji Policy**. 

---

## 2. Core Directives

1. **Zero Unicode Emojis in User Interfaces:**
   - No Unicode emojis in buttons, tabs, chips, tooltips, dialogs, titles, or marketing banners.
   - No emoji prefixes in lists, headers, or bullet points (e.g. avoid `[Rocket] Quick Start`, `[Tip] Tip`, `[Flower] Pink Bays`).
2. **SVG & Vector Drawables Only When Functionally Necessary:**
   - Visual icons may only be used when they provide direct functional value (such as clear navigational affordance, recognizable system controls, or status indicators).
   - In Web/HTML: Use inline standard `<svg>` elements with clean geometric paths (such as Heroicons or Lucide vectors).
   - In Kotlin Compose Multiplatform: Use Compose `ImageVector` (`androidx.compose.material.icons.Icons.*`) or custom Compose vector drawables.
3. **Prefer Semantic Typography & Badges:**
   - Specialized categories (e.g. POD, Women-Only Pink, Delivery Rider Quick Bays, EV Charging) must use clean textual badges with distinct semantic color accents (e.g. `[POD]`, `[PINK]`, `[DELIVERY]`, `[EV]`).
4. **Documentation & Commit Standards:**
   - Technical documentation, READMEs, architecture guides, and git commit messages must be written in clean, concise professional English without decorative emojis.

---

## 3. Platform Guidelines

### Web (HTML / CSS / JavaScript)
- **Forbidden:** `<span class="emoji">[Car]</span> Driver` or `<button>[Moon] Dark Mode</button>`
- **Approved:** Clean inline SVG icons:
  ```html
  <!-- Light / Dark Mode Toggle Icon -->
  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
    <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
  </svg>
  ```

### Kotlin Multiplatform (Compose)
- **Forbidden:** `enum class DriverTab(val label: String, val icon: String = "[Map]")`
- **Approved:** `enum class DriverTab(val label: String, val icon: ImageVector = Icons.Default.Place)`

### Specialized Bay Classifications
| Bay Type | Prohibited Emoji | Standard Acronym & Badge | Semantic Accent |
| :--- | :--- | :--- | :--- |
| People of Determination | `[Wheelchair]` | `POD` / `POD Accessible` | Blue (`#1976D2`) |
| Women-Only | `[Flower]` | `PINK` / `Women-Only` | Pink (`#E91E63`) |
| Delivery Riders | `[Scooter]` | `DELIVERY` / `Quick Bay` | Amber (`#FF6F00`) |
| EV Charging | `[Lightning]` | `EV` / `DEWA EV` | Green (`#388E3C`) |
| Standard Bays | `[Parking]` | `STANDARD` | Slate / Gray |

---

## 4. Compliance & Review Checklist

Before merging pull requests or creating features:
- [ ] Checked that no emoji unicode characters exist in `.kt`, `.html`, `.xml`, or `.json` files.
- [ ] Verified that all icons in web templates are SVG vector paths with explicit width, height, and accessibility labels (`aria-hidden="true"` or `aria-label`).
- [ ] Verified that Compose navigation items use `androidx.compose.material.icons` or vector painters.
- [ ] Verified markdown documentation headers are clean without emoji decorations.

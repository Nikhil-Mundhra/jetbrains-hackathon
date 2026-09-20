# YallaPark — Smart Mobility & Predictive Parking Platform

An advanced smart urban mobility and predictive parking platform engineered to solve parking congestion across Dubai's busiest commercial and residential hubs (**Bur Dubai, Karama, Deira, and Downtown Dubai**). Built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** targeting Android, iOS, Desktop, and Web.

---

## Problem & Strategic Vision

### The Urban Challenge in Dubai
In high-density commercial corridors like **Bur Dubai, Al Karama, Deira Gold Souq, and Downtown**, motorists spend excessive time circling blocks in search of open parking bays:
- **Severe Traffic Gridlock**: Secondary congestion caused by slow-moving vehicles seeking spaces.
- **Environmental Impact**: Significant carbon emissions and wasted fuel during the "circle and search" loop.
- **Double Parking Friction**: Delivery riders (Talabat, Careem, Deliveroo) frequently double-park due to a lack of designated quick-stay bays.
- **Inefficient Bay Allocation**: Underutilized spaces in adjacent lots while main streets overflow.

### The YallaPark Solution
YallaPark replaces the traditional "circle and search" with a **guaranteed, predictive reservation framework**:
- **Predictive Slot Availability**: Machine-learning-based slot decay model calculates the probability of open parking upon arrival at the driver's estimated time of arrival (ETA).
- **Guaranteed Pre-Booking & Hold**: 15-minute guaranteed slot hold backed by digital payments (Apple Pay, Google Pay, Dubai RTA NOL card, credit/debit card).
- **Inclusivity & Specialized Bays**: Dedicated mapping, filters, and turn-by-turn routing for:
  - **People of Determination (POD)** accessibility bays.
  - **Women-Only (Pink)** designated parking spaces in well-lit, secure areas.
  - **Delivery Rider Quick Bays** (15–20 min short stays) to prevent double-parking.
  - **EV Charging Bays** integrated with DEWA green charging networks.
- **Gamified Eco-Rewards**: Drivers earn green mobility points for booking off-peak or using park-and-ride facilities, redeemable for discounts at local Dubai merchants.

---

## Two Pathways for Finding & Managing Parking Spaces

### Way 1: Automated Aerial / Satellite & Drone Detection Pipeline (`python-pipeline/`)
Free satellite imagery (Sentinel-2 at 10m, Landsat at 30m) is too coarse for 2.5m x 5m parking bays. YallaPark provides a working prototype pipeline using high-resolution spatial data and computer vision:
1. **OSM Lot Polygons**: Extracts parking geometries across Dubai and Abu Dhabi via the OpenStreetMap Overpass API (`fetch_osm_lots.py`).
2. **YOLO-OBB Vehicle Detection**: Oriented Bounding Box detector (`yolov8n-obb`) trained on aerial datasets (DOTA/COWC) to detect vehicles under any angle (`detect_occupancy.py`).
3. **Polygon Containment & Occupancy**: Calculates exact vehicle counts inside lot boundaries using polygon containment algorithms.
4. **Data Bridge & MongoDB Atlas Sync**: Exports real-time occupancy feeds to local JSON and syncs directly to MongoDB Atlas (`export_to_yallapark.py`).

```bash
# Run Way 1 Pipeline
cd python-pipeline
python3 -m pip install -r requirements.txt  # Optional: shapely, ultralytics, pymongo
python3 run_prototype.py
```

### Way 2: Government & Facility Operator Admin Console (`composeApp/admin`)
An enterprise console enabling the **Roads and Transport Authority (RTA)**, **Mawaqif**, and commercial garage operators to manage parking infrastructure in real time:
- **Add & Remove Parking Facilities**: Deploy new parking lots with custom zone classifications, geofences, and capacities.
- **Bay Allocation Matrix**: Designate specific bays as POD, Women-Only Pink, Delivery Rider, or EV.
- **Real-Time Telemetry & Sensor Simulation**: Live visual grid of every bay (Available, Occupied, Reserved, Maintenance) with one-click IoT gate camera simulation.
- **Dynamic Tariffs**: Set base hourly rates, peak-hour multipliers, and delivery courier grace periods.
- **Multi-Tenant Role Switcher**: Toggle seamlessly between Motorist, RTA Public Authority, Mawaqif Operator, and Commercial Garage Operator in the top bar.

---

## YallaPark AI Concierge (OpenRouter Integration)

An intelligent conversational parking assistant powered by OpenRouter API (`openai/gpt-4o-mini`):
- Answers Dubai-specific mobility questions (e.g. *“Where can I find pink parking bays in Karama?”* or *“What is the tariff near Deira Gold Souq?”*).
- Context-aware guidance on POD permit eligibility, delivery rider regulations, and off-peak parking discounts.

---

## Architecture

```
jetbrains-hackathon/
├── composeApp/                                 # Shared UI Layer (Compose Multiplatform)
│   ├── commonMain/kotlin/
│   │   ├── com/yallapark/
│   │   │   ├── App.kt                          # Role Switcher (Driver vs Way 2 Admin) & Navigation
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   ├── DubaiZoneMap.kt         # Cross-platform Canvas vector map of Dubai
│   │   │   │   │   ├── BayIndicatorCard.kt     # Specialized bay badge (POD, Pink, Delivery, EV)
│   │   │   │   │   ├── OccupancyProgressBar.kt # Visual congestion gauge
│   │   │   │   │   └── PredictiveSlider.kt     # Dynamic ETA slot probability slider
│   │   │   │   ├── screens/
│   │   │   │   │   ├── driver/                 # Motorist search, booking, active pass, rewards
│   │   │   │   │   ├── admin/                  # Way 2 facility management, bay config, tariffs
│   │   │   │   │   └── ai/                     # OpenRouter conversational AI concierge
│   │   │   │   └── theme/                      # Dubai RTA-inspired palette (Teal, Gold, Pink)
├── shared/                                     # Core KMP Business Logic
│   ├── commonMain/kotlin/com/yallapark/
│   │   ├── domain/model/                       # ParkingLot, ParkingBay, Reservation, Forecast, UserRole
│   │   │   │   │   ├── driver/
│   │   │   │   │   │   ├── MapExplorerScreen.kt # Live search, zone pills, interactive map
│   │   │   │   │   │   ├── LotDetailScreen.kt  # Bay selection matrix & decay prediction
│   │   │   │   │   │   ├── BookingFlowScreen.kt # Payment methods (NOL, Apple Pay, GPay)
│   │   │   │   │   │   ├── ActiveSessionScreen.kt # Active pass, ANPR QR, remote extension
│   │   │   │   │   │   └── RewardsScreen.kt    # Gamified eco-mobility reward ledger
│   │   │   │   │   ├── admin/
│   │   │   │   │   │   ├── AdminDashboardScreen.kt # Citywide occupancy KPIs & telemetry
│   │   │   │   │   │   ├── ManageLotsScreen.kt # Add/remove parking facilities
│   │   │   │   │   │   └── BayConfigScreen.kt  # Interactive bay matrix & sensor simulator
│   │   │   │   │   └── ai/
│   │   │   │   │       └── YallaAiScreen.kt    # OpenRouter conversational assistant
│   │   │   │   └── theme/                      # Dubai RTA-aligned typography & design system
│   │   │   └── com/communityconnect/               # Base infrastructure
│   │   ├── androidMain/                            # Android entry point
│   │   ├── iosMain/                                # iOS entry point (SwiftUI bridge)
│   │   └── desktopMain/                            # Desktop JVM runner
├── shared/                                     # Business Logic & Core Models
│   └── commonMain/kotlin/
│       ├── com/yallapark/
│       │   ├── domain/model/                   # ParkingLot, ParkingBay, Zone, BayType, Session
│       │   ├── presentation/viewmodel/         # MapViewModel, AdminViewModel
│       │   └── ai/                             # OpenRouterClient & YallaAiViewModel
│       └── com/communityconnect/
├── python-pipeline/                            # Way 1 Prototype (Automated Aerial/CV Pipeline)
│   ├── fetch_osm_lots.py                       # Extracts Dubai parking polygons via OSM API
│   ├── detect_occupancy.py                     # YOLO-OBB detection & polygon containment
│   ├── export_to_yallapark.py                  # Exports occupancy to JSON & MongoDB Atlas
│   └── run_prototype.py                        # End-to-end Way 1 simulation script
├── server/                                     # Unified Ktor Backend & MongoDB Gateway
└── wasmJsApp/                                  # Web (WASM) runner
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Cross-Platform UI** | Compose Multiplatform (Android, iOS, Desktop JVM, Web WASM) |
| **Language** | Kotlin 2.0+ across 100% of platforms, Python 3.9+ for CV pipeline |
| **Map Rendering** | Custom Compose Vector Canvas Map (Zero external SDK dependencies) |
| **Networking** | Ktor Client with ContentNegotiation & Kotlinx Serialization |
| **State Management** | StateFlow, SharedFlow, AndroidX Lifecycle ViewModel |
| **AI Integration** | OpenRouter API (`https://openrouter.ai/api/v1/chat/completions`) |
| **Database & Cloud** | MongoDB Atlas cluster + reactive local telemetry simulator |
| **Computer Vision** | YOLOv8-OBB (`yolov8n-obb.pt`), OpenStreetMap Overpass API, Shapely |

---

## Features (MVP Walkthrough)

1. **Map Exploration & Filters**:
   - Filter by Dubai Zone: **Bur Dubai, Al Karama, Deira, Downtown Dubai**.
   - Filter by Bay Category: **POD Accessible, Women-Only Pink, Delivery Rider, EV Charging**.
2. **Predictive Arrival Slider**:
   - Drag arrival ETA slider (5 min to 60 min) to see real-time slot probability percentage and smart recommendation.
3. **Pre-Booking & Frictionless Payments**:
   - Guaranteed 15-minute slot lock with Apple Pay, Google Pay, or Dubai RTA NOL card.
   - Digital Parking Pass with QR code for ANPR smart gates.
4. **1-Click Remote Extension**:
   - Extend session (+15m, +30m, +1h) remotely without visiting a meter.
5. **Way 2 Admin Console**:
   - Toggle to Admin Mode in top bar.
   - Deploy new facilities in Deira or Bur Dubai.
   - Tap individual bays to simulate vehicle arrivals/departures or gate sensor telemetry.
   - Switch between RTA Authority, Mawaqif, and Private Operator roles.
6. **Eco-Rewards**:
   - Earn green points for off-peak parking, redeemable at local Dubai cafes and merchants.
7. **YallaPark AI**:
   - Ask questions about parking rules, tariffs, and locations in Dubai.

---

## Hackathon Team

- **Platform Architect & Lead**: Kotlin Multiplatform & Compose Multiplatform
- **Database & Cloud**: MongoDB Atlas Integration
- **AI & Mobility Intelligence**: OpenRouter LLM Integration
- **Way 1 Vision Pipeline**: YOLO-OBB & OpenStreetMap Spatial Analytics
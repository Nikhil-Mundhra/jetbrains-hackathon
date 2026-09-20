# Demo Script for Final Pitch: YallaPark

## 🎬 3-Minute Hackathon Demo Flow

### Setup (Before Presentation)
- [ ] Device/Emulator or Desktop runner active
- [ ] Browser window ready
- [ ] Python pipeline ready in terminal: `python3 python-pipeline/run_prototype.py`
- [ ] Demo vehicle plate ready: `DXB A 48291`

---

### 0:00-0:30 | Hook & Problem (Dubai Urban Mobility)
> **Speaker**: "In Dubai's busiest cultural and commercial hubs — like Bur Dubai, Karama, Deira, and Downtown — drivers spend an average of 18 minutes circling blocks just to find parking. This causes secondary traffic gridlock, unnecessary fuel waste, and rampant double-parking by delivery riders. 
> 
> Today, we present **YallaPark**: a smart urban mobility and predictive parking platform built with Kotlin Multiplatform, designed for future integration into Dubai's RTA smart city ecosystem."

---

### 0:30-1:15 | Driver Demo: Predictive Pre-Booking & Specialized Bays
**On Screen (Mobile / Web View)**:
1. **Interactive Vector Map**:
   - Point to the Dubai map showing Bur Dubai, Karama, Deira, and Downtown.
   - Click on the **"Al Karama"** zone tab.
2. **Specialized Bay Filters**:
   - Tap **"🌸 Women-Only"** and **"♿ POD Accessible"** chips.
   - Show how lot availability updates instantly.
3. **Predictive ETA Slider**:
   - Tap **"Karama Commercial Center Parking"**.
   - Move the **ETA arrival slider** from 10 mins to 45 mins.
   - **Show**: Availability dynamically recalculates from 88% down to 34% with an intelligent recommendation: *"High demand expected at your arrival. Pre-booking strongly advised."*
4. **Frictionless Booking & Hold**:
   - Select **Bay KC-W1 (Women-Only Pink Bay)**.
   - Tap **"Pre-Book Bay"**.
   - Choose **RTA NOL Card** payment.
   - Tap **"Pay AED 4.0 & Lock Bay"**.
   - **Show**: Instant digital pass generated with QR code, 15-minute guaranteed hold, and turn-by-turn routing!

---

### 1:15-1:50 | Way 2 Live Demo: RTA & Operator Admin Console
**On Screen**:
1. **Switch Mode**:
   - In the top bar, click the toggle from **"🚗 Driver"** to **"🏛️ Admin (Way 2)"**.
   - Point out the multi-tenant role switcher (**RTA Authority**, **Mawaqif Operator**, **Private Garage Operator**).
2. **Add a Parking Facility Live**:
   - Click **"+ Add Space"**.
   - Enter *"Al Sabkha Smart Commercial Lot"* in Deira, 65 bays, 4 POD bays, 6 Pink bays, 8 Delivery bays.
   - Tap **"Deploy Parking Facility to YallaPark"**.
   - **Show**: The facility immediately appears in citywide KPIs.
3. **Sensor Telemetry & Bay Control**:
   - Click **"Manage Bays & Sensors"**.
   - Tap **Bay P-1** or **S-1** to toggle from Available to Occupied.
   - Switch back to **"🚗 Driver"** mode and show the new facility and changed bay availability reflected in real time!

---

### 1:50-2:25 | Way 1 Live Demo: YOLO-OBB Aerial CV Pipeline
**In Terminal**:
1. Run:
   ```bash
   python3 python-pipeline/run_prototype.py
   ```
2. **Narrate**: 
   > "For automated lot tracking where cameras aren't installed, Way 1 extracts OpenStreetMap parking polygons via the Overpass API, passes aerial frames through an oriented bounding box detector (YOLO-OBB), computes polygon occupancy ratios, and exports live data to MongoDB Atlas."
3. **Show**: Terminal outputs 1,000+ real Dubai lots extracted and processed with capacity counts.

---

### 2:25-2:45 | AI Concierge (OpenRouter) & Eco-Rewards
**On Screen**:
1. Tap **"🤖 Yalla AI"** tab:
   - Click quick prompt: *"🌸 Pink Bays in Karama"*
   - **Show**: AI responds with exact floor levels, security lighting info, and pre-booking suggestions.
2. Tap **"🌱 Rewards"** tab:
   - Highlight **340 Green Points** earned for off-peak parking and **4.8 kg CO₂ saved**.
   - Show local Dubai merchant perks (Arabica Coffee Bur Dubai 25% off, Karama Spice House cashback).

---

### 2:45-3:00 | Closing Pitch
> **Speaker**: "With YallaPark, we don't just tell drivers where parking *was* — we guarantee where parking *will be*. A 100% shared Kotlin Multiplatform codebase across Android, iOS, Desktop, and Web, integrated with MongoDB Atlas and OpenRouter AI. 
> 
> Thank you, and Yalla, let's park smarter!"
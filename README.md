# 🕌 BlackBerry OS Adhan Application

An open-source, lightweight Islamic Prayer Times (Adhan) application built natively for legacy **BlackBerry OS** devices (BlackBerry OS 5.0 / 6.0 / 7.0).

Developed by **Mohamed BOURI**.

---

## ✨ Features

* **Native BlackBerry UI:** Built using native RIM BlackBerry UI primitives (`UiApplication`, `MainScreen`, custom `Manager`).
* **RTL & Custom Alignment:** Clean Arabic UI support with right-to-left layout alignment for prayer names and times.
* **Astronomical Prayer Calculations:** Accurate astronomical calculation of Fajr, Sunrise, Dhuhr, Asr, Maghrib, and Isha without needing an internet connection.
* **Background Timer & Notifications:** Runs in the background, updates the UI dynamically, and plays the Adhan audio when prayer time arrives.
* **Audio Playback:** Built-in player (`AdhanPlayer`) utilizing JSR-135 Mobile Media API (`MMAPI`).

---

## 📂 Project Structure


```

net.mbeffects.adhan
├── AdhanApp.java          # Main Application Entry Point (UiApplication)
├── AdhanMainScreen.java    # Primary User Interface & Custom RTL Layout
├── PrayerCalculator.java  # Mathematical & Astronomical Calculation Engine
├── TimerService.java      # Background Daemon Thread for updating UI & alerts
└── AdhanPlayer.java       # Audio Engine for playing adhan.mp3

```

---

## 🧮 How to Modify Prayer Calculations (`PrayerCalculator.java`)

The core calculation logic is isolated inside `PrayerCalculator.java`. You can easily adapt this application to **any city or country** by modifying the constants at the top of the class.

### 1. Location & Timezone Parameters

To change the target city, update the location parameters:

```java
private static final double LAT        = 32.85;    // Latitude (e.g., 32.85 for Oued Zem)
private static final double LNG        = -6.58;    // Longitude (e.g., -6.58 for Oued Zem)
private static final double ALTITUDE   = 765.0;    // Altitude in meters above sea level
private static final double TIMEZONE   = 0.0;      // Standard Time Offset (e.g., GMT+0)

```

| Parameter | Description |
| --- | --- |
| `LAT` | Geographic latitude in decimal degrees (positive for North, negative for South). |
| `LNG` | Geographic longitude in decimal degrees (positive for East, negative for West). |
| `ALTITUDE` | Elevation above sea level (used for precise horizon/sunrise/sunset refraction). |
| `TIMEZONE` | Base GMT offset in hours (e.g., `1.0` for GMT+1). |

---

### 2. Calculation Methods & Angles

Different Islamic conventions use different angles for **Fajr** and **Isha**:

```java
private static final double FAJR_ANGLE = 18.0; // Angle below horizon for Fajr
private static final double ISHA_ANGLE = 17.0; // Angle below horizon for Isha

```

Common conventions:

* **MWL (Muslim World League):** Fajr = 18°, Isha = 17°
* **ISNA (North America):** Fajr = 15°, Isha = 15°
* **Egyptian General Authority:** Fajr = 19.5°, Isha = 17.5°
* **Umm al-Qura (Makkah):** Fajr = 18.5°, Isha = 90 min after Maghrib

---

### 3. Minute Offsets & Adjustments

If local official timetables differ slightly from astronomical calculations, use the minute offset constants:

```java
private static final double CORR_FAJR    = -5.0; // Adjust Fajr by -5 minutes
private static final double CORR_DHUHR   =  5.0; // Adjust Dhuhr by +5 minutes
private static final double CORR_ASR     =  0.0; // Adjust Asr by 0 minutes
private static final double CORR_MAGHRIB =  5.0; // Adjust Maghrib by +5 minutes
private static final double CORR_ISHA    =  0.0; // Adjust Isha by 0 minutes

```

---

### 4. Mathematical Engine Overview

The calculator uses standard astronomical algorithms adapted for Java ME:

1. **Julian Day (`julianDay`)**: Converts standard Gregorian calendar dates into astronomical Julian Days.
2. **Solar Position (`sunPos`)**: Computes solar declination and the Equation of Time ($EqT$).
3. **Hour Angle (`AT`)**: Uses spherical trigonometry to calculate exact hour angles:

$$\cos(H) = \frac{-\sin(\alpha) - \sin(\delta) \cdot \sin(\phi)}{\cos(\delta) \cdot \cos(\phi)}$$

4. **Asr Calculation**: Uses the standard Shafi'i / Maliki / Hanbali shadow length calculation:

$$\text{Asr Altitude} = \text{arccot}(1 + \tan(|\phi - \delta|))$$

---

## 🛠️ How to Build & Run

### Prerequisites

* **JDK 1.5 / 1.6** (Legacy Java required for RIM BlackBerry toolchain).
* **BlackBerry JDE 5.0+** or **Eclipse with BlackBerry Plugin**.
* **BlackBerry Smartphone / Fledge Simulator** (e.g., BlackBerry Torch 9800).

### Steps

1. Clone this repository:
```bash
git clone [https://github.com/Mohamed-bouri/adhan.git](https://github.com/Mohamed-bouri/adhan.git)

```


2. Import the project into your **Eclipse / BlackBerry JDE** workspace.
3. Place an `adhan.mp3` audio file into the root of your `/res` or source directory if you want custom audio playback.
4. Build the project to generate the target `.cod` / `.jad` files.
5. Deploy to your device or load via **BlackBerry Desktop Manager** / **Javaloader**.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page if you want to contribute.

---

## 👤 Author

**Mohamed BOURI**

* Goal: Preserving legacy BlackBerry software ecosystem with free, open-source utilities.

---

## 📜 License

This project is open-source and available under the [MIT License](https://www.google.com/search?q=LICENSE).

```

```

# BB10 Hub Launcher for Xperia

A BlackBerry OS 10-style notification hub integrated into Lawnchair, for Sony Xperia devices running Android 13+.

## What It Does

- **Left swipe**: Opens the BB10 Hub with all your notifications in one place
- **Sorted notifications**: By category (email, SMS, social, calls) with colored side stripes
- **Tap to open**: Notifications open their source app directly
- **Per-category LED**: Xperia flash light colors per notification type
- **Snooze & dismiss**: Actions right from the Hub
- **Peek animation**: New notifications slide in from the top
- **Settings page**: Toggle everything on/off (all off by default for safety)
- **Diagnostics**: Export anonymized logs if something breaks

## Build & Install

Everything is automated via GitHub Actions. No local build needed.

**Step-by-step:**

1. Create a new GitHub repo: https://github.com/new
2. Upload all files from this package via the web UI (drag and drop)
3. Go to **Actions** tab, enable workflows if needed
4. Push any change (or wait ~5 min for auto-trigger)
5. Download the APK from the successful build
6. Install: `adb install -r bb10-hub-debug.apk`
7. Grant permissions: `adb shell cmd notification allow_listener app.lawnchair.debug/app.lawnchair.bb10hub.HubNotificationService`
8. Set as default launcher and test

### Detailed Setup Guide

See `SETUP.md` for step-by-step web UI instructions (no terminal needed).

## Project Structure

```
├── .github/workflows/build-bb10.yml    # CI/CD that builds everything
├── keystore/bb10-debug.keystore        # Fixed signing key (stable signatures)
├── lawnchair/src/app/lawnchair/bb10hub/
│   ├── HubModels.kt                    # Data models & enums
│   ├── HubRepository.kt                # Notification storage
│   ├── HubNotificationService.kt       # System listener
│   ├── HubLedController.kt             # Xperia LED control
│   ├── BB10HubScreen.kt                # Main Compose UI
│   ├── Bb10HubActivity.kt              # Hub activity
│   ├── Bb10SettingsActivity.kt         # Settings page
│   └── BB10HubOverlay.kt               # Launcher integration
├── lawnchair/src/main/res/             # Colors, strings, layouts
└── SETUP.md                            # User setup guide
```

## Tech Stack

- **Base**: Lawnchair 14-dev (Launcher3 from Android 14, supports Android 13)
- **UI**: Jetpack Compose with Material 3
- **Notifications**: Android `NotificationListenerService` (no permissions hacks)
- **LED**: Xperia via `NotificationChannel` LED API
- **Storage**: In-memory `StateFlow` (notifications clear on app restart)
- **CI/CD**: GitHub Actions + Gradle 8.10.2

## For Users

See `SETUP.md` for installation without touching a terminal.

## For Developers

The GitHub Actions workflow handles all the complexity:
1. Clones Lawnchair 14-dev with submodules
2. Copies BB10 files to the right places
3. Applies manifest patches
4. Builds with Gradle
5. Uploads the APK artifact

To modify:
1. Edit files in `lawnchair/src/app/lawnchair/bb10hub/`
2. Push to your fork
3. GitHub Actions auto-builds

If the build fails, paste the error log and I'll fix it.

## Design

- **Dark theme**: Material 3 on dark background
- **BB10 aesthetic**: Colored category stripes, compact cards
- **Springy animations**: Compose-based with haptic feedback ready
- **Accessibility-first**: All toggles off by default, safe defaults

## Limitations (Phase 1)

- Notifications clear when app restarts (no persistent storage yet)
- Single-category LED (flashes once per category, not per notification)
- No quick-reply UI (Android RemoteInput available but not wired)
- Swipe opens activity, not a finger-tracked panel (more reliable, less smooth)

These become Phase 2 once the base is stable on your device.

## Troubleshooting

**Hub doesn't appear when swiping?**
- Make sure Lawnchair is set as the default launcher
- Swipe from the **right edge inward** (not from the left)
- Check that the notification listener permission was granted via adb

**Build fails in GitHub Actions?**
- Paste the error log in this repo's issue and I'll fix it

**No notifications showing?**
- Did you run the `adb shell cmd notification` command?
- Check Settings → Apps → Lawnchair Debug → (Notification access or Special app access)

**LED doesn't flash?**
- Check Settings → BB10 Hub Settings → LED Light toggle
- Some Xperia models have LED disabled by firmware

## License

Same as Lawnchair (Apache 2.0).

---

**Ready to go.** See `SETUP.md` to get started.

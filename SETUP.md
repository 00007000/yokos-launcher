# BB10 Hub Launcher — GitHub Web UI Setup (No Terminal Needed)

You have everything you need. This is the step-by-step to get it building.

---

## **Step 1: Create a New GitHub Repository**

1. Go to https://github.com/new
2. **Repository name:** `lawnchair-bb10` (or whatever you like)
3. **Description:** "BB10 notification hub launcher"
4. **Public** or **Private** — doesn't matter
5. **Skip** "Initialize with README" (we'll upload files instead)
6. Click **Create repository**

You'll land on an empty repo page. Keep this page open.

---

## **Step 2: Upload the BB10 Hub Files**

On your empty repo page, click the **"uploading an existing file"** link (or click the **"Add file" → "Upload files"** button).

Then **drag and drop** the files from this package into the upload area. Upload all of them at once:

```
.github/workflows/build-bb10.yml
lawnchair/src/app/lawnchair/bb10hub/
  ├── HubNotificationItem.kt
  ├── HubNotificationService.kt
  ├── BB10HubScreen.kt
  ├── HubLedController.kt
  ├── Bb10HubActivity.kt
  ├── Bb10SettingsActivity.kt
  ├── BB10HubOverlay.kt
  ├── HubModels.kt
  └── HubRepository.kt
lawnchair/src/main/res/
  ├── values/colors.xml
  ├── values/strings.xml
  ├── values/styles.xml
  ├── xml/bb10_file_paths.xml
  └── layout/activity_bb10_hub.xml
keystore/bb10-debug.keystore
.gitignore
README.md
```

The folder structure will be created automatically as you drag files — GitHub preserves paths.

At the bottom, click **"Commit changes"** (message: "Initial BB10 Hub commit").

---

## **Step 3: Enable GitHub Actions**

1. Click the **Actions** tab on your repo
2. If you see a banner saying "Workflows aren't being run on this repository," click **"I understand my workflows, go ahead and enable them"**
3. If you see a dropdown (Settings → Actions → Allow all actions), make sure **"Allow all actions and reusable workflows"** is selected

---

## **Step 4: Trigger the Build**

The build happens automatically, but to be sure:

1. Go to the **Code** tab
2. Click **"Add file" → "Create new file"**
3. Name it `TRIGGER` (anything)
4. Add one line: `trigger`
5. Click **"Commit changes"**

This pushes a change, which triggers the workflow. Go to the **Actions** tab — you should see **"Build BB10 Hub"** running (blue spinner).

Wait for it to turn **green** (✅). This takes 5–8 minutes.

---

## **Step 5: Download the APK**

1. Click the green **"Build BB10 Hub"** run
2. Scroll to **Artifacts** at the bottom
3. Click **bb10-hub-debug.apk** to download

---

## **Step 6: Install on Your Xperia 1 III**

Connect your phone to your laptop via USB and run:

```bash
adb install -r bb10-hub-debug.apk
adb shell cmd notification allow_listener app.lawnchair.debug/app.lawnchair.bb10hub.HubNotificationService
```

(If you don't know where `adb` is, it's in `C:\platform-tools-latest-windows\platform-tools\adb.exe` — you can drag it into a terminal or right-click and "Open in Terminal" in that folder to use it from there.)

---

## **Step 7: Test the Hub**

1. Go to **Settings → Apps → Lawnchair Debug → Set as default launcher** (or the three-dots menu)
2. Press home to go to the launcher
3. **Swipe from the right edge to the left** — the Hub should slide in
4. If notifications appear, it's working! 🎉

---

## **Troubleshooting**

**Build fails in GitHub Actions?**
- Paste the error log here and I'll fix it.

**Swipe doesn't open the Hub?**
- Make sure Lawnchair is set as the default launcher.
- Make sure you're swiping from the **right edge inward**, not from the left.

**No notifications showing?**
- Did you run the `adb shell cmd notification` command? That's required to give the app access.

---

**That's it. You're done.** The workflow handles all the Lawnchair 14-dev integration and patching automatically.

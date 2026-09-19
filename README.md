<div align="center">

<img src="fastlane/metadata/android/en-US/images/icon.png" width="110" alt="NoteShoot icon" />

# NoteShoot

**A minimal, themeable notes app with a home-screen widget.**  
No account. No cloud. No clutter. Just notes.

[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)

</div>
<div align="center">
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width="720" alt="Theme picker" />

</div>




## About

NoteShoot is a notes app that stays out of the way. Write a note, pin it to your home screen, done. No account, no cloud, no ads.

## Features

- Create, edit, and delete notes
- Pin any note to your home screen with a widget
- Swap notes from the widget menu
- Full emoji support, including in the widget
- Three themes: Mono Dark, Mono Light, and a hidden VGUI theme
- Font size slider from 11 to 18 pt
- Notes and settings are saved automatically

## Widget

The widget is bitmap rendered inside the app, not by the launcher. This means emoji work on every device, even on launchers that normally break them.

Tap the widget to open the note in the editor. Tap the menu button to switch which note the widget shows.

## Themes

| Theme | Description |
|---|---|
| Mono Dark | Black background, light text |
| Mono Light | White background, dark text |
| <details><summary>???</summary>VGUI. Tap "version 1.0" ten times in the About screen to unlock it.</details> | Hidden |

## Install

| Source | Status |
|---|---|
| GitHub Releases | [Download APK](https://github.com/bamBi4k/NoteShoot/releases) |
| F-Droid | Coming soon |
| Google Play | Coming soon |

Needs Android 8.0 or newer.

## Build

```bash
git clone https://github.com/bamBi4k/NoteShoot.git
cd NoteShoot
./gradlew assembleDebug

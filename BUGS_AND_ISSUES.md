# Identified Bugs and Issues in Stealth-Calc

This document lists the bugs, issues, and potential improvements identified during the initial review of the Stealth-Calc repository.

## 1. Critical Compile Errors
These issues prevent the application from building successfully.

- **Missing Imports:**
    - `ChatRepository.kt`: Missing `kotlinx.coroutines.flow.receiveAsFlow` import.
    - `LoginFragment.kt`: Missing `com.darkempire78.opencalculator.stealth.network.ChatRepository` import.
- **Unresolved References:**
    - `ChatFragment.kt`: `activity` is referenced in `observeMessages` and `disconnectAndExit` but is not in scope or properly accessed.
    - `LoginFragment.kt`: `activity` is referenced on line 90 but is out of scope.

## 2. Logic and Functional Bugs
These issues affect the runtime behavior and features of the app.

- **Media Persistence Bug:** In `ChatRepository.kt`, when loading message history, the `mediaUrl` is not restored from the local database (only `mediaLocalPath` is handled), which might break media display for synced messages.
- **Alias Persistence:** In `ChatFragment.kt`, the `showChangeAliasDialog` method only shows a toast and does not actually save the new alias to preferences or the session.
- **Message Observation:** `ChatFragment.kt` only appends new messages from the flow, which could lead to missing updates or incorrect ordering if the flow emits non-sequential updates.
- **Redundant Code:** `ChatMessageAdapter.kt` has a redundant `areContentsTheSame` check.

## 3. Security and Architecture Issues
- **Misleading Naming:** `StealthPreferences.kt` uses the name "stealth_encrypted_prefs" but utilizes standard `MODE_PRIVATE` SharedPreferences instead of actual encrypted storage.
- **Trigger Reliability:** The `StealthTriggerEngine` uses a simultaneous long-press on two buttons. While clever, it might be difficult for users to trigger consistently or could interfere with fast calculator usage.

## 4. Minimal Upgrade Recommendations
- **Fix Compile Errors:** Essential for a working app.
- **Implement Alias Change:** Make the "Change Alias" feature functional.
- **Improve Media Handling:** Ensure `mediaUrl` and `mediaLocalPath` are correctly mapped.
- **Dependency Audit:** Ensure all dependencies in `libs.versions.toml` are stable and compatible.

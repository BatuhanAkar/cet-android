# Çet: Scalable Real-Time Chat & Self-Hosted WebRTC Infrastructure

Çet is a high-performance Android communication platform architected around real-time reactive data streams, serverless cloud triggers, and self-hosted WebRTC video/audio conferencing capabilities. 

The application is built on the MVVM (Model-View-ViewModel) architectural pattern, enforcing strict separation of concerns, repository patterns, and unidirectional data flow.

---

## Technical Architecture & Firebase Integration

The platform orchestrates multiple database and cloud paradigms simultaneously to optimize data synchronization latency and client-side processing:

* **Firebase Realtime Database:** Utilized as the primary low-latency engine for instant messaging pipelines, typing indicators, and user presence tracking (online/offline states).
* **Firebase Firestore:** Acts as the persistent storage layer for complex relational documents, including user profiles, social graphs, and chat room metadata.
* **Firebase Cloud Functions:** Executes server-side business logic, managing database cleanup operations, payload sanitization, and secure token generations.
* **Firebase Cloud Messaging (FCM):** Handles high-priority downstream push notifications for call invites and background message synchronization across device lifecycles.
* **Firebase Storage & Authentication:** Manages secure user session validation and optimized multi-media object uploads (images, voice notes) with strict security rules.

---

## Video & Audio Conferencing (Self-Hosted WebRTC)

To ensure maximum data privacy and eliminate high third-party API costs, the application avoids commercial SaaS communication providers:

* **Self-Hosted Jitsi Meet Infrastructure:** Real-time audio and video communications are powered by a custom-configured, self-hosted Jitsi Meet instance.
* **API Integration:** The Android client communicates directly with the custom WebRTC bridges using the Jitsi Meet SDK, optimizing peer-to-peer connection handshakes and bandwidth usage adaptively based on the user's network state.

---

## Tech Stack & Architecture (Android Client)

| Component | Architecture / Technology |
| :--- | :--- |
| **Design Pattern** | Clean Architecture with MVVM Pattern |
| **Asynchronous Engine** | Kotlin Coroutines & Reactive LiveData/Flow pipelines |
| **Local Cache** | Context-aware state management for offline message queuing |
| **Media Handling** | Firebase Cloud Storage SDK with local performance caching |
| **Network Framework** | Hybrid infrastructure (Firebase WebSockets + REST API clients) |

---

## Core Application Workflows

### 1. Hybrid Database Routing
When a message is sent, the transaction bypasses standard document storage and hits the WebSockets-based **Realtime Database** first to guarantee sub-millisecond delivery to active peers. Simultaneously, cloud triggers index the metadata for historical search capabilities within **Firestore**.

### 2. Low-Latency Call Orchestration
1. **Initiation:** User triggers a video call.
2. **Signaling:** A Firebase Cloud Function validates the session and generates a dynamic, secure room ID on the self-hosted Jitsi server.
3. **Payload Delivery:** FCM sends a high-priority data payload to the recipient's device, waking up the background listener to launch the incoming call UI.
4. **WebRTC Stream:** Upon acceptance, the Jitsi Meet SDK takes over the hardware camera/audio layers to initiate the encrypted media stream.

---

## Project Status

* **Core Infrastructure:** Production-ready real-time communication modules and WebRTC bridges are completed.
* **Deployment:** Self-hosted signaling servers and cloud infrastructure are fully operational.

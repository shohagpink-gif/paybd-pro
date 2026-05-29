# PayBD Pro

Android application for managing bKash payment transactions with webhook integration.

## Features

- **SMS Detection**: Automatically intercepts and parses bKash payment SMS
- **Room Database**: Stores all transactions locally with full details
- **Transaction Management**: Approve or Cancel individual transactions
- **Webhook Integration**: Sends transaction data to configurable webhook endpoint
- **Notifications**: Shows push notifications for new incoming payments
- **Material 3 UI**: Modern Material Design 3 interface

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 34 (Android 14)
- Kotlin 1.9.22
- Gradle 8.5

## Setup

1. Clone this repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

## Configuration

1. Launch the app
2. Enter your Webhook URL in the settings section
3. Enter your Auth Token (optional)
4. Tap "Save Settings"

## Permissions Required

- `RECEIVE_SMS` - To detect incoming bKash SMS
- `READ_SMS` - To read SMS content
- `INTERNET` - To send webhook requests
- `POST_NOTIFICATIONS` - To show transaction notifications

## Architecture

```
com.paybd.pro/
├── MainActivity.kt          - Main UI with settings and transaction list
├── PayBDApplication.kt      - Application class with notification channel
├── adapter/
│   └── TransactionAdapter.kt - RecyclerView adapter for transactions
├── data/
│   ├── TransactionEntity.kt  - Room entity definition
│   ├── TransactionDao.kt     - Room DAO interface
│   ├── AppDatabase.kt        - Room database singleton
│   └── Converters.kt         - Room type converters
├── network/
│   └── WebhookClient.kt      - OkHttp webhook POST client
├── receiver/
│   └── SmsReceiver.kt        - BroadcastReceiver for SMS
└── util/
    └── BkashParser.kt        - Regex-based bKash SMS parser
```

## Webhook Payload

When a transaction is approved/cancelled, the following JSON is POSTed:

```json
{
  "trxId": "ABC123XYZ",
  "amount": 500.0,
  "balance": 1500.0,
  "sender": "01712345678",
  "status": "APPROVED",
  "rawMessage": "You have received Tk 500.00...",
  "timestamp": 1717000000000
}
```

## License

Private - All rights reserved.

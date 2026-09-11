# 📱 Hash Vault

HashVault is a simple Android application that generates cryptographic hash values for user-entered text. The application supports MD5, SHA-1, and SHA-256 hashing algorithms using Java's built-in MessageDigest class, providing a fast and reliable way to compute hash digests.
The project follows the Model-View-ViewModel (MVVM) architecture to ensure a clean separation of concerns, making the codebase modular, maintainable, and easy to understand. The UI interacts with the ViewModel, which coordinates the hashing logic through the repository layer, while utility classes handle the cryptographic operations.

---

## 🚀 Key Features

- Generate MD5, SHA-1, and SHA-256 hashes from text input.
- Uses Java's MessageDigest API for secure hash generation.
- Implements the MVVM architecture for better code organization.
- Clean and simple user interface.
- Lightweight and easy to extend with additional hashing algorithms

---

## 🧑‍💻 Tech Stack

- Java
- MVVM Architecture
- ViewModel
- MessageDigest API
- XML

---

## 📁 Project Structure
```text
app
└── src
    └── main
        ├── java
        │   └── com.example.hashvault
        │       ├── model
        │       │   └── HashModel.java
        │       ├── repository
        │       │   └── HashRepository.java
        │       ├── viewmodel
        │       │   └── HashViewModel.java
        │       ├── ui
        │       │   └── MainActivity.java
        │       └── utils
        │           └── HashUtils.java
        │
        ├── res
        │   ├── layout
        │   │   └── activity_main.xml
        │   ├── values
        │   ├── drawable
        │   └── font
        │
        └── AndroidManifest.xml
```
---

## 📷 Screenshots

| Home                             | After Generating Hash                     |
|----------------------------------|-------------------------------------------|
| ![](ProjectScreenShots/home.png) | ![](ProjectScreenShots/hashGenerated.png) |

---

## 📖 What I Learned

While building this project, I gained experience with:

- Understanding and implementing the MVVM (Model-View-ViewModel) architecture.
- Using Java's MessageDigest class to generate cryptographic hashes.
- Generating MD5, SHA-1, and SHA-256 hashes from user-provided text.
- Separating UI logic from application and data-processing logic using MVVM.
- Taking user input and dynamically displaying the generated hash results.
- Understanding that MD5 and SHA-1 are considered cryptographically weak and should not be used for modern password security.
- Improving my understanding of cryptographic hashing and one-way functions.

---

<div align="center">

![Project Status](https://img.shields.io/badge/Project%20Status-25%25%20Completed-ffd900?style=for-the-badge)

</div>

---
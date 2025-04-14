
# 📱 Práctica 3 – Gestor de Archivos, Cámara y Micrófono

Este proyecto es una aplicación Android desarrollada con **Jetpack Compose** que incluye dos funcionalidades principales:

1. Un **gestor de archivos** con soporte para favoritos, archivos recientes, vista de texto e imágenes.
2. Una interfaz para **captura de fotos y grabación/reproducción de audios** usando la cámara y el micrófono del dispositivo.

---

## 🧰 Requisitos

- Android Studio **2024.2.2 (Ladybug)** o superior
- SDK mínimo: **API 25**
- SDK objetivo: **API 35 (Android 15)**
- Dispositivo o emulador con cámara y micrófono
- Permisos activos para almacenamiento y audio

---

## 🚀 Instrucciones de uso

### 🔹 Gestor de Archivos
- Navega por las carpetas del almacenamiento interno.
- Puedes:
  - Abrir archivos `.txt` y `.md`.
  - Ver imágenes `.jpg`, `.jpeg`, `.png` (con miniaturas).
  - Marcar archivos como favoritos ⭐.
  - Acceder a archivos recientes.
  - Abrir otros archivos con apps externas.
  - Ver la ruta de navegación (breadcrumbs).
  - Borrar favoritos o historial.

### 🔹 Cámara y Micrófono
- Captura una foto con la app de cámara del sistema.
- Graba audio tocando el botón de grabar/detener.
- Accede a la lista de audios grabados desde "Ver Audios".
- Reproduce o elimina audios directamente.

---

## 🎨 Temas y diseño

- Se incluye un **tema azul (ESCOM)** y **tema guinda (IPN)** adaptables al modo claro/oscuro.
- Interfaz moderna usando Material 3 y Jetpack Compose.

---

## 📦 Estructura del proyecto

```
├── MainActivity.kt
├── navigation/
│   └── NavGraph.kt
├── screens/
│   ├── FileManagerScreen.kt
│   ├── CameraMicScreen.kt
│   ├── TextViewerScreen.kt
│   ├── ImageViewerScreen.kt
│   ├── RecentFilesScreen.kt
│   └── FavoriteFilesScreen.kt
├── data/
│   ├── RecentFilesStore.kt
│   └── FavoritesStore.kt
├── ui.theme/
│   └── ThemeViewModel.kt, Color.kt, etc.
```

---

## 📜 Permisos requeridos

- `READ_EXTERNAL_STORAGE`
- `CAMERA`
- `RECORD_AUDIO`

Se solicitan automáticamente en tiempo de ejecución.

---

## Autor

- Nombre: **Carlos Adrián Hernández Torres**
- Boleta: **2021630417**
- Grupo: **7CV3**
- ESCOM - IPN

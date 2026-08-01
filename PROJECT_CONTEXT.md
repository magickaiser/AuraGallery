# AuraGallery — Project Context

> **Leer este archivo primero** al retomar el proyecto. Contiene el estado completo de la app: arquitectura, features implementadas, pendientes, y reglas de código.

---

## Resumen

**AuraGallery** es una aplicación de galería nativa para Android, desarrollada en Kotlin con Jetpack Compose + Material 3. Permite ver fotos y videos del dispositivo, organizados por carpetas/álbumes, con zoom fluido, swipe entre imágenes, favoritos y borrado directo.

**Repo**: https://github.com/magickaiser/AuraGallery (público)
**Package**: `com.aura.gallery`

---

## Tech Stack

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Lenguaje | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material 3 | BOM 2024.12.01 |
| Build | Gradle (Kotlin DSL) | 8.11.1 / AGP 8.7.3 |
| Navegación | Compose Navigation | 2.8.5 |
| Imágenes | Coil 3 | 3.0.4 |
| Video | Media3 ExoPlayer | 1.5.1 |
| Base de datos | Room | 2.6.1 |
| Preferencias | DataStore | 1.1.1 |
| DI | Hilt | 2.53.1 |
| Async | Coroutines + Flow | — |
| Acceso multimedia | MediaStore API (Scoped Storage) | — |
| MinSdk / TargetSdk | 26 (Android 8) / 35 (Android 15) | — |

---

## Arquitectura

```
PRESENTATION (Compose Screens + ViewModels)
    ├── albums/   gallery/   viewer/   player/   favorites/
    ├── components/   theme/   navigation/
    └── States: MutableStateFlow + collectAsState()
         ↑
DOMAIN (Models + Repository Interfaces + UseCases)
    ├── MediaItem, Album, MediaType
    ├── MediaRepository, FavoriteRepository
    └── UseCases: GetAlbums, GetMediaByAlbum, ToggleFavorite,
                   ShareMedia, DeleteMedia, GetFavorites
         ↑
DATA (Implementations)
    ├── MediaRepositoryImpl → ContentResolver + MediaStore
    ├── FavoriteRepositoryImpl → Room (FavoriteDao)
    ├── SettingsDataStore → DataStore Preferences
    └── MediaMapper → Cursor/Entity ↔ Domain Model
```

**Patrón**: MVVM con Clean Architecture de 3 capas:
- **Domain** no depende de Android (solo Kotlin + Coroutines)
- **Data** implementa las interfaces del Domain
- **Presentation** usa `StateFlow` en ViewModels y `collectAsState()` en Composables

**DI**: Hilt — `DatabaseModule` (Room), `RepositoryModule` (binds interfaces→impl)

**Navegación**: `NavGraph` con rutas definidas en `Screen` sealed class.

---

## Features implementadas

| Feature | Estado | Detalles |
|---------|--------|----------|
| Permisos runtime | OK | Android 13+: `READ_MEDIA_IMAGES/VIDEO`. Pre-13: `READ_EXTERNAL_STORAGE`. Diálogo con botón "Abrir ajustes" si se deniegan. |
| Álbumes + "Todas las fotos" | OK | `AlbumsScreen` → `AlbumsViewModel` → `loadAlbums()` agrupa por `BUCKET_ID`. Primer ítem: álbum especial `bucketId=0`. |
| Grid con filtro | OK | `GalleryScreen` → filtro Todos/Fotos/Videos mediante `MediaTypeFilter` chips. `GalleryViewModel.applyFilter()`. |
| Visor de imagen | OK | `MediaViewerScreen` → `ZoomableImage` con `detectTransformGestures` + clamping de bordes. Doble tap = zoom 2.5x. |
| Zoom fluido | OK | Fix: `rememberUpdatedState(scale)` + `pointerInput(Unit)` estable (no se reinicia el detector). |
| Swipe entre fotos | OK | `GallerySharedViewModel` scoped a activity vía NavGraph. `AnimatedContent` con slide transition. `detectHorizontalDragGestures` (solo a scale=1). |
| Barra de contador | OK | En visor: "N / total" cuando hay más de 1 imagen. |
| Favoritos | OK | Room `FavoriteEntity` + `FavoriteDao`. `ToggleFavoriteUseCase`. Visor: ícono corazón en top bar. |
| Selección múltiple | OK | Long-press en thumbnail → entra modo selección. Barra superior cambia a color primario con contador + botones: seleccionar todo, favoritos, eliminar. |
| Borrado directo | OK | `DeleteMediaUseCase`. Android 11+ usa `MediaStore.createDeleteRequest`. Pre-11 usa `contentResolver.delete()`. El ítem se elimina permanentemente del dispositivo. |
| Reproductor de video | OK | `VideoPlayerScreen` con ExoPlayer (`AndroidView` + `PlayerView`). |
| Compartir | OK | `Intent.ACTION_SEND` con URI del media item en el visor individual. |
| Toggle UI en visor | OK | Tap en la imagen = mostrar/ocultar top bar. |
| Deploy script | OK | `deploy.ps1` — build + install en emulador (`-emulator`) y dispositivo físico (`-device`). |

---

## Features eliminadas

| Feature | Motivo |
|---------|--------|
| Papelera/Trash (Room) | Reemplazado por borrado directo. Eliminados: `TrashScreen`, `TrashViewModel`, `TrashDao`, `TrashEntity`, `TrashRepository`, `TrashRepositoryImpl`, `MoveToTrashUseCase`, `RestoreFromTrashUseCase`, `GetTrashItemsUseCase`. |
| Pestaña Papelera en bottom nav | Eliminada. Solo 2 tabs: Álbumes y Favoritos. |

---

## Features pendientes

| Item | Prioridad | Notas |
|------|-----------|-------|
| Toggle modo oscuro desde UI | Alta | Tema `AuraGalleryTheme` ya definido con light/dark. Falta toggle en UI + persistir en DataStore. |
| Animación al borrar del grid | Media | Al eliminar en selección múltiple, los ítems desaparecen sin animación. |
| Compartir múltiples imágenes | Media | Solo se comparte 1 imagen desde el visor. |
| Swipe en visor desde Favoritos | Media | Al abrir desde Favoritos, el swipe no funciona (el `GallerySharedViewModel` no tiene la lista). |
| Ordenación por fecha/nombre | Baja | DataStore ya tiene key `sort_order`. Falta UI + lógica. |
| Grid columns configurable | Baja | DataStore ya tiene key `grid_columns`. Falta UI + lógica. |
| Tests unitarios / UI | Baja | Estructura `src/test/` creada pero vacía. |
| Soporte GIF/WebP animado | Baja | Coil 3 lo soporta, no probado. |
| Pantalla de ajustes | Baja | Para exponer opciones de tema, grid, orden. |

---

## Estructura de paquetes

```
com.aura.gallery/
├── AuraApplication.kt              ← @HiltAndroidApp
├── MainActivity.kt                 ← @AndroidEntryPoint, setContent, bottom nav
│
├── domain/
│   ├── model/
│   │   ├── MediaItem.kt            ← id, uri, mimeType, size, bucketId, isFavorite
│   │   ├── Album.kt                ← bucketId, bucketName, coverUri, itemCount
│   │   └── MediaType.kt            ← IMAGE, VIDEO
│   ├── repository/
│   │   ├── MediaRepository.kt      ← getAlbums(), getMediaByAlbum(), deleteMedia(), getMediaById()
│   │   └── FavoriteRepository.kt   ← getFavorites(), toggleFavorite(), isFavorite()
│   └── usecase/
│       ├── GetAlbumsUseCase.kt
│       ├── GetMediaByAlbumUseCase.kt
│       ├── GetFavoritesUseCase.kt
│       ├── ToggleFavoriteUseCase.kt
│       ├── DeleteMediaUseCase.kt
│       └── ShareMediaUseCase.kt
│
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── AppDatabase.kt      ← Room DB (entities: FavoriteEntity)
│   │   │   ├── dao/FavoriteDao.kt
│   │   │   └── entity/FavoriteEntity.kt
│   │   └── datastore/
│   │       └── SettingsDataStore.kt ← theme_mode, grid_columns, sort_order
│   ├── repository/
│   │   ├── MediaRepositoryImpl.kt  ← ContentResolver + MediaStore
│   │   └── FavoriteRepositoryImpl.kt ← Room FavoriteDao
│   └── mapper/
│       └── MediaMapper.kt          ← Cursor → MediaItem, Entity ↔ MediaItem
│
├── presentation/
│   ├── navigation/
│   │   ├── Screen.kt               ← Sealed class with routes
│   │   └── NavGraph.kt             ← NavHost + all composable() destinations
│   ├── theme/
│   │   ├── Theme.kt                ← AuraGalleryTheme (light/dark + dynamic colors)
│   │   ├── Color.kt                ← Palette (LightPrimary=#7C4DFF, etc.)
│   │   └── Type.kt                 ← Typography
│   ├── albums/
│   │   ├── AlbumsScreen.kt         ← Permission dialog + album grid
│   │   └── AlbumsViewModel.kt      ← loadAlbums() + "Todas las fotos" special album
│   ├── gallery/
│   │   ├── GalleryScreen.kt        ← Grid + filter chips + selection mode
│   │   └── GalleryViewModel.kt     ← loadMedia(), filter, selection state
│   ├── viewer/
│   │   ├── MediaViewerScreen.kt    ← Zoom + swipe + actions
│   │   └── MediaViewerViewModel.kt ← loadMediaById(), toggleFavorite(), deletePermanently()
│   ├── player/
│   │   ├── VideoPlayerScreen.kt    ← ExoPlayer via AndroidView
│   │   └── VideoPlayerViewModel.kt
│   ├── favorites/
│   │   ├── FavoritesScreen.kt      ← Grid of favorites
│   │   └── FavoritesViewModel.kt
│   └── components/
│       ├── MediaThumbnail.kt       ← AsyncImage + selection checkmark + video indicator
│       ├── AlbumCard.kt            ← Card with cover + name + count
│       ├── BottomNavBar.kt         ← 2 tabs: Álbumes, Favoritos
│       ├── MediaTypeFilter.kt      ← FilterChip row (Todos/Fotos/Videos)
│       ├── EmptyStateView.kt       ← Centered icon + message
│       └── GallerySharedViewModel.kt ← Shared VM for swipe navigation between images
│
└── di/
    ├── DatabaseModule.kt           ← Room DB + FavoriteDao provider
    └── RepositoryModule.kt         ← Binds MediaRepository, FavoriteRepository
```

---

## Build & Deploy

### Requisitos
- Android SDK instalado en `%LOCALAPPDATA%\Android\Sdk`
- JDK 21 (bundled con Android Studio en `C:\Program Files\Android\Android Studio\jbr`)
- ADB en `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`

### Comandos

```powershell
# Build APK debug
.\gradlew.bat assembleDebug

# Deploy completo (build + emulador + dispositivo)
.\deploy.ps1 -all

# Solo emulador
.\deploy.ps1 -emulator

# Solo dispositivo físico
.\deploy.ps1 -device

# Solo build
.\deploy.ps1 -buildOnly
```

### Dispositivos configurados

| Nombre | Tipo | ADB Serial |
|--------|------|------------|
| Pixel 6 API 35 | Emulador | `emulator-5554` |
| Móvil físico | Dispositivo | `e709f789` |

---

## Reglas de código

1. **MVVM estricto**: ViewModels NO importan clases de Compose. States son data classes inmutables con `copy()`.
2. **StateFlow + collectAsState**: Los ViewModels exponen `StateFlow<UiState>`, las pantallas usan `collectAsState()`.
3. **Hilt**: `@HiltViewModel` en ViewModels, `@Inject constructor` en UseCases y repositorios. Módulos en `di/`.
4. **No lógica en Composables**: Toda la lógica de negocio va en ViewModels/UseCases. Los Composables solo renderizan UI.
5. **Navigation con argumentos**: Rutas definidas en `Screen` sealed class. Argumentos tipados (`NavType.LongType`, `NavType.StringType`).
6. **pointerInput estable**: Usar `rememberUpdatedState` para valores mutables dentro de `pointerInput(Unit)`.
7. **Permisos**: Siempre verificar antes de acceder a MediaStore. Android 13+ usa permisos granulares.
8. **ContentResolver con proyecciones**: Solo usar nombres de columna reales, NO expresiones SQL como `COUNT(*)`.
9. **Modificadores Compose**: `Modifier.fillMaxSize()` en Box raíz de cada pantalla. `dp` para dimensiones, `sp` para texto.

---

## Notas de troubleshooting

| Problema | Solución |
|----------|----------|
| `Unresolved reference: dependencyResolution` en settings.gradle.kts | Usar `dependencyResolutionManagement` (no `dependencyResolution`) |
| `SDK location not found` | Crear `local.properties` con `sdk.dir=C:/Users/Markel/AppData/Local/Android/Sdk` |
| `Invalid column COUNT(*) as count` | No usar SQL en proyecciones de ContentResolver. Agrupar en Kotlin. |
| `Activity class does not exist` | El debug build tiene suffix `.debug`. Usar `com.aura.gallery.debug` para `am start`. |
| TopAppBar sin title | Material3 requiere `title = {}` explícito. |

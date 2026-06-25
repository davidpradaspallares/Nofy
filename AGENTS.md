# Nofy - Project Context for AI Assistants

## Project Overview
Android app built with Jetpack Compose, following MVVM architecture with multi-module structure by layers.

---

## Stack Técnico

| Categoría | Tecnología |
|---|---|
| UI | Jetpack Compose + Material3 |
| Arquitectura | MVVM + Use Cases |
| DI | Hilt |
| Async | Coroutines + Flow |
| Navegación | Compose Navigation |
| Almacenamiento | Room |
| Serialización | Kotlinx Serialization |
| Logging | Timber |
| Testing | JUnit 5 + MockK + Turbine |
| minSdk | 24 (Android 7.0) |
| targetSdk | 35 |
| Compile SDK | 35 |
| Kotlin | 2.0.21 |
| AGP | 8.7.3 |
| Gradle | 8.9 |
| Version Catalog | libs.versions.toml |

---

## Estructura del Proyecto

```
Nofy/
├── app/                   # Punto de entrada, Application, MainActivity, NavGraph
├── core/                  # Lógica compartida: theme, componentes UI, DI, extensions
├── data/                  # Capa de datos: Room, DAOs, repositorios
├── domain/                # Capa de dominio: modelos, interfaces de repositorio, use cases
└── feature/               # Módulos de features (por feature)
    └── home/              # Feature: Home
```

### Reglas de dependencia entre módulos

- `feature/*` → `core`, `domain`
- `data` → `domain`
- `domain` → (sin dependencias Android)
- `app` → `core`, `data`, `domain`, `feature/*`

---

## Paleta de Colores

| Token | Color | Hex |
|---|---|---|
| Background | Fondo Base | `#191B24` |
| Surface | Superficie Tarjetas/Menús | `#2E313D` |
| Primary | Acento Nofy | `#FFCC00` |
| TextPrimary | Texto principal | `#F5F6F8` |
| TextSecondary | Texto secundario | `#B0B3B8` |
| Outline | Líneas divisorias y Bordes | `#4A4D57` |

**Archivo:** `core/src/main/java/com/nofy/core/ui/theme/Color.kt`

Solo existe tema oscuro. El tema se define en `core/.../theme/Theme.kt` usando `darkColorScheme`.

---

## Convenciones de Código

### Naming
- **Paquetes:** `com.nofy.{modulo}` (ej: `com.nofy.feature.home`)
- **Clases Compose:** PascalCase, sufijo `Screen`, `Card`, etc.
- **ViewModels:** PascalCase, sufijo `ViewModel`
- **UI State:** PascalCase, sufijo `UiState`
- **Use Cases:** PascalCase, prefijo verbo en infinitivo (ej: `GetHomeDataUseCase`)
- **Repositorios:** PascalCase, sufijo `Repository`
- **DAOs:** PascalCase, sufijo `Dao`
- **Entities:** PascalCase, sufijo opcional
- **Funciones y variables:** camelCase
- **Constantes:** UPPER_SNAKE_CASE

### Archivos
- Un `data class` por archivo para UiState
- Un `@Composable` por archivo para Screens
- Un ViewModel por archivo
- Un Use Case por archivo

### Composables
- Prefijo `Nofy` para componentes compartidos del core (ej: `NofyCard`)
- Sin prefijo de módulo para features (ej: `HomeScreen`)
- Usar `collectAsStateWithLifecycle()` para observar Flows en Compose
- No usar `collectAsState()` directamente

### ViewModel
- Usar `@HiltViewModel` + `@Inject constructor`
- State expuesto como `StateFlow` inmutable, respaldado por `MutableStateFlow` privado
- Usar `viewModelScope.launch` para corrutinas

---

## Cómo crear un nuevo Feature

1. Crear directorio `feature/{nombre}/`
2. Crear `feature/{nombre}/build.gradle.kts`
3. Agregar `include(":feature:{nombre}")` en `settings.gradle.kts`
4. Crear estructura:
```
feature/{nombre}/
└── src/main/java/com/nofy/feature/{nombre}/
    ├── {Nombre}Screen.kt
    ├── {Nombre}ViewModel.kt
    └── {Nombre}UiState.kt
```
5. Agregar la ruta en `app/.../navigation/NavGraph.kt`
6. Si necesita datos: crear/actualizar interfaces en `domain` e implementaciones en `data`

---

## Testing

### Framework
- JUnit 5 (jupiter) para tests unitarios
- MockK para mocks
- Turbine para testing de Flows

### Ubicación
- Tests en `src/test/` dentro de cada módulo
- Tests de ViewModel en el módulo feature correspondiente
- Tests de Use Cases en el módulo domain
- Tests de DAOs/Repositorios en el módulo data

### Patrón de test para ViewModel
```kotlin
@Test
fun `when viewmodel loads then state is correct`() = runTest {
    val useCase = mockk<GetHomeDataUseCase>()
    coEvery { useCase() } returns HomeData("title", "desc")

    val viewModel = HomeViewModel(useCase)

    viewModel.uiState.test {
        assertEquals(HomeUiState(isLoading = true), awaitItem())
        assertEquals(
            HomeUiState(title = "title", description = "desc"),
            awaitItem()
        )
        cancelAndIgnoreRemainingEvents()
    }
}
```

---

## Dependencias (Version Catalog)

Todas las dependencias se gestionan desde `gradle/libs.versions.toml`. No hardcodees versiones en los `build.gradle.kts`.

### Plugins principales
- `com.android.application` / `com.android.library`
- `org.jetbrains.kotlin.android`
- `org.jetbrains.kotlin.plugin.compose`
- `org.jetbrains.kotlin.plugin.serialization`
- `com.google.dagger.hilt.android`
- `com.google.devtools.ksp`

### Librerías clave
- `compose-bom` (Bill of Materials para Compose)
- `hilt-android` + `hilt-compiler` (ksp)
- `room-runtime` + `room-compiler` (ksp) + `room-ktx`
- `kotlinx-serialization-json`
- `kotlinx-coroutines-core` / `kotlinx-coroutines-android`
- `timber`
- `navigation-compose`
- `hilt-navigation-compose`
- `lifecycle-runtime-compose` / `lifecycle-viewmodel-compose`

---

## Flujo de datos típico

```
Screen (Composable)
    ↓ collectAsStateWithLifecycle()
ViewModel
    ↓ viewModelScope.launch { useCase() }
UseCase
    ↓ invoke
Repository (interfaz en domain)
    ↓ implementación en data
Room DAO / Database
```

---

## Configuración de Hilt

- `@HiltAndroidApp` en `NofyApplication`
- `@AndroidEntryPoint` en `MainActivity`
- `@HiltViewModel` en ViewModels con `@Inject constructor`
- Módulos Hilt en `core/di/` y `data/di/`
- `@InstallIn(SingletonComponent::class)` para módulos de datos globales

---

## Configuración de Compose Navigation

- NavHost definido en `app/.../navigation/NavGraph.kt`
- `startDestination = "home"`
- Cada feature registra su ruta con `composable("ruta") { ... }`
- Navegación entre features usando `navController.navigate("ruta")`

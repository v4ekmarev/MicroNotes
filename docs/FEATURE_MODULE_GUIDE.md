# Руководство по Feature-модулям

Этот документ описывает стандартную структуру и соглашения для feature-модулей в проекте MicroNotes.

## Общая архитектура

Каждая фича разделена на два модуля:
- **`feature/<name>-api`** — публичный API фичи (интерфейсы, модели, маршруты)
- **`feature/<name>-impl`** — реализация (UI, ViewModel, Repository, API, DI)

```
feature/
├── note-api/           # Публичный API
│   └── src/commonMain/kotlin/com/develop/feature/note/
│       ├── route/
│       │   └── NoteRoute.kt           # Навигационный маршрут
│       └── domain/
│           ├── NoteRepository.kt      # Интерфейс репозитория
│           ├── model/
│           │   ├── NoteDetails.kt     # Доменная модель
│           │   └── NotesWithCategories.kt
│           └── usecase/
│               ├── GetNoteUseCase.kt      # Интерфейсы UseCase
│               ├── SaveNoteUseCase.kt
│               ├── DeleteNoteUseCase.kt
│               └── GetNoteDetailsUseCase.kt
│
└── note-impl/          # Реализация
    └── src/commonMain/kotlin/com/develop/feature/note/
        ├── data/
        │   ├── api/
        │   │   └── NoteSharingApi.kt      # HTTP API клиент
        │   ├── models/
        │   │   ├── SendNoteRequest.kt     # Сетевые модели
        │   │   └── SendNoteResponse.kt
        │   └── repository/
        │       └── NoteRepositoryImpl.kt  # Реализация репозитория
        ├── di/
        │   └── NoteModule.kt              # Koin DI модуль
        ├── domain/
        │   └── usecase/
        │       ├── GetNoteUseCaseImpl.kt      # Реализации UseCase
        │       ├── SaveNoteUseCaseImpl.kt
        │       ├── DeleteNoteUseCaseImpl.kt
        │       └── NoteUseCases.kt            # Контейнер UseCase
        └── presentation/
            ├── NoteScreen.kt              # Compose UI (entry point)
            ├── NoteViewModel.kt           # ViewModel
            ├── contract/
            │   ├── NoteState.kt           # UI State
            │   ├── NoteAction.kt          # User actions (Intent/Event)
            │   └── NoteEffect.kt          # One-time effects
            └── components/
                └── NoteBottomSheets.kt    # UI компоненты
```

---

## Модуль `-api`

### Назначение
Содержит **публичный контракт** фичи, который могут использовать другие модули.

### Содержимое

| Пакет | Содержимое |
|-------|------------|
| `route/` | `Route` — навигационный маршрут |
| `domain/` | Интерфейсы `Repository` |
| `domain/model/` | Доменные модели (без суффиксов) |
| `domain/usecase/` | Интерфейсы UseCase |

### Зависимости (build.gradle.kts)

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

sourceSets {
    commonMain.dependencies {
        api(project(":core:model"))       // Если нужны общие модели
        api(project(":core:navigation"))  // Для Route
        implementation(libs.kotlinx.serialization.core)
    }
}
```

### Пример Route

```kotlin
// route/NoteRoute.kt
package com.develop.feature.note.route

import com.develop.core.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class NoteRoute(val id: Long? = null) : Route
```

### Пример UseCase интерфейса

UseCase интерфейсы наследуются от базовых классов из `core/common/usecase/`:

| Базовый интерфейс | Когда использовать |
|-------------------|-------------------|
| `UseCase<R>` | Без параметров, возвращает `R` |
| `UseCaseWithParams<P, R>` | С параметрами `P`, возвращает `R` |
| `ResultUseCase<R>` | Без параметров, возвращает `Result<R>` |
| `ResultUseCaseWithParams<P, R>` | С параметрами `P`, возвращает `Result<R>` |

```kotlin
// domain/usecase/GetNoteDetailsUseCase.kt
package com.develop.feature.note.domain.usecase

import com.develop.core.common.usecase.UseCaseWithParams
import com.develop.feature.note.domain.model.NoteDetails

interface GetNoteDetailsUseCase : UseCaseWithParams<Long?, NoteDetails>
```

```kotlin
// domain/usecase/GetProfileUseCase.kt
package com.develop.feature.profile.domain.usecase

import com.develop.core.common.usecase.ResultUseCase
import com.develop.feature.profile.domain.model.UserProfile

interface GetProfileUseCase : ResultUseCase<UserProfile>
```

> **Примечание:** Используем `execute()` вместо `operator fun invoke()` для удобной навигации в IDE.

### Пример интерфейса Repository

```kotlin
// domain/NoteRepository.kt
package com.develop.feature.note.domain

import com.develop.core.model.Note

interface NoteRepository {
    suspend fun getAll(): List<Note>
    suspend fun getById(id: Long): Note?
    suspend fun save(note: Note)
    suspend fun delete(id: Long)
}
```

---

## Модуль `-impl`

### Назначение
Содержит **реализацию** фичи: UI, бизнес-логику, работу с сетью и БД.

### Структура пакетов

| Пакет | Содержимое |
|-------|------------|
| `data/api/` | HTTP API клиенты (Ktor) |
| `data/models/` | Сетевые модели (`*Request`, `*Response`) |
| `data/repository/` | Реализации репозиториев |
| `di/` | Koin модуль |
| `domain/usecase/` | Реализации UseCase + контейнер `NoteUseCases` |
| `presentation/` | UI компоненты |

### Зависимости (build.gradle.kts)

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

sourceSets {
    commonMain.dependencies {
        // Core модули
        implementation(project(":core:common"))
        implementation(project(":core:model"))
        api(project(":core:navigation"))
        implementation(project(":core:ui-kit"))
        
        // API модуль этой фичи
        implementation(project(":feature:note-api"))
        
        // Data модули (при необходимости)
        implementation(project(":data:database"))
        implementation(project(":data:network"))
        
        // Ktor (для API)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.json)
        
        // Compose
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.ui)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.components.ui.tooling.preview)
        
        // Koin DI
        implementation(libs.koin.core)
        implementation(libs.koin.compose)
        implementation(libs.koin.compose.viewmodel)
        
        // Lifecycle
        implementation(libs.androidx.lifecycle.viewmodelCompose)
        implementation(libs.androidx.lifecycle.runtimeCompose)
        
        // Прочее
        implementation(libs.material.icons.extended)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
    }
    androidMain.dependencies {
        implementation(libs.koin.android)
    }
}
```

---

## Соглашения по именованию моделей

| Суффикс | Слой | Пример |
|---------|------|--------|
| `*Request` | Network (запрос) | `SendNoteRequest`, `AddContactRequest` |
| `*Response` | Network (ответ) | `ContactResponse`, `UserProfileResponse` |
| *(без суффикса)* | Domain | `Note`, `Contact`, `UserProfile` |
| `*Entity` | Database | `NoteEntity`, `CategoryEntity` |
| `*UiModel` | Presentation (если отличается от domain) | `NoteUiModel` |

### Правила
1. **Один класс = один файл** (имя файла = имя класса)
2. Сетевые модели содержат `@SerialName` для защиты от обфускации
3. Доменные модели — чистые data class без аннотаций сериализации

---

## Presentation слой

### State

```kotlin
// presentation/NoteState.kt
data class NoteState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
```

### Action (события от UI)

```kotlin
// presentation/action/NoteAction.kt
sealed interface NoteAction {
    data object Load : NoteAction
    data object Save : NoteAction
    data class UpdateTitle(val title: String) : NoteAction
    data class UpdateContent(val content: String) : NoteAction
}
```

### Effect (одноразовые события для UI)

```kotlin
// presentation/action/NoteEffect.kt
sealed interface NoteEffect {
    data object NoteSaved : NoteEffect
    data object NavigateBack : NoteEffect
    data class ShowError(val message: String) : NoteEffect
}
```

### ViewModel

```kotlin
// presentation/NoteViewModel.kt
class NoteViewModel(
    private val interactor: NoteInteractor,
    private val route: NoteRoute
) : ViewModel() {
    
    private val _state = MutableStateFlow(NoteState())
    val state: StateFlow<NoteState> = _state.asStateFlow()
    
    private val _effect = MutableSharedFlow<NoteEffect>()
    val effect = _effect.asSharedFlow()
    
    fun onAction(action: NoteAction) {
        when (action) {
            NoteAction.Load -> load()
            NoteAction.Save -> save()
            is NoteAction.UpdateTitle -> updateTitle(action.title)
            is NoteAction.UpdateContent -> updateContent(action.content)
        }
    }
    
    private fun load() { /* ... */ }
    private fun save() { /* ... */ }
    // ...
}
```

### Screen

```kotlin
// presentation/NoteScreen.kt
@Composable
fun NoteScreen(
    route: NoteRoute,
    onBack: () -> Unit
) {
    val viewModel: NoteViewModel = koinViewModel { parametersOf(route) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NoteEffect.NavigateBack -> onBack()
                NoteEffect.NoteSaved -> { /* показать snackbar */ }
                is NoteEffect.ShowError -> { /* показать ошибку */ }
            }
        }
    }
    
    NoteScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun NoteScreenContent(
    state: NoteState,
    onAction: (NoteAction) -> Unit
) {
    // UI реализация
}
```

---

## DI модуль

```kotlin
// di/NoteModule.kt
package com.develop.feature.note.di

expect val notePlatformModule: Module  // Для платформо-зависимых зависимостей

val noteModule = module {
    includes(notePlatformModule)
    
    // API
    single { NoteSharingApi(get()) }
    
    // Repository
    single { NoteRepositoryImpl(get(), get()) } bind NoteRepository::class
    
    // Interactor
    single { NoteInteractorImpl(get()) } bind NoteInteractor::class
    
    // ViewModel
    viewModel { params ->
        NoteViewModel(
            interactor = get(),
            route = params.get()
        )
    }
}
```

---

## Интеграция в приложение

### 1. Добавить в settings.gradle.kts

```kotlin
include(":feature:note-api")
include(":feature:note-impl")
```

### 2. Добавить зависимости в composeApp/build.gradle.kts

```kotlin
implementation(project(":feature:note-api"))
implementation(project(":feature:note-impl"))
```

### 3. Подключить DI модуль в Koin.kt

```kotlin
fun appModule(context: Context) = module {
    single { context }
    includes(noteModule, /* другие модули */)
}
```

### 4. Добавить навигацию в App.kt

```kotlin
is NoteRoute -> NavEntry(key) {
    NoteScreen(
        route = key,
        onBack = { backStack.removeLastOrNull() }
    )
}
```

---

## Чеклист для новой фичи

- [ ] Создать `feature/<name>-api/build.gradle.kts`
- [ ] Создать `feature/<name>-impl/build.gradle.kts`
- [ ] Добавить модули в `settings.gradle.kts`
- [ ] Создать `<Name>Route` в `-api`
- [ ] Создать интерфейсы `Repository`/`Interactor` в `-api/domain/`
- [ ] Создать доменные модели в `-api/domain/model/`
- [ ] Создать сетевые модели в `-impl/data/models/`
- [ ] Создать API клиент в `-impl/data/api/`
- [ ] Создать реализацию репозитория в `-impl/data/repository/`
- [ ] Создать `State`, `Action`, `Effect` в `-impl/presentation/`
- [ ] Создать `ViewModel` в `-impl/presentation/`
- [ ] Создать `Screen` в `-impl/presentation/`
- [ ] Создать DI модуль в `-impl/di/`
- [ ] Добавить зависимости в `composeApp`
- [ ] Подключить DI модуль в `Koin.kt`
- [ ] Добавить навигацию в `App.kt`

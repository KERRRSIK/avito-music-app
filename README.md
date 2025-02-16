# avito-music-app
Музыкальное приложение, разработанное в рамках зимней стажировки в Авито.

#  Описание
Приложение позволяет прослушивать локальные треки, а также загружать музыку из API Deezer. Оно построено на модульной архитектуре, использует Activity и Fragment для интерфейса и ExoPlayer для воспроизведения аудио.

# Технологический стек 

Язык: Kotlin

Архитектура: MVVM

UI: Activity, Fragment

Сетевые запросы: Retrofit, OkHttp

Многопоточность: Kotlin Coroutines, Flow

Сериализация: Kotlinx Serialization

Навигация: Jetpack Navigation

Dependency Injection: Dagger Hilt

Медиаплеер: ExoPlayer


# Архитектура проекта
Проект разделен на несколько модулей для удобства поддержки и расширения.

# Запуск проекта
Клонируйте репозиторий:

```bash
git clone https://github.com/KERRRSIK/avito-music-app.git
```
Откройте проект в Android Studio.

Выполните Gradle Sync для загрузки зависимостей.

Запустите приложение на эмуляторе или реальном устройстве.

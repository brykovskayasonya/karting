Картинг-центр — клиентское приложение

Мобильное приложение для записи клиентов на заезды в картинг-центре:
просмотр доступных слотов с фильтрами, запись (себя и гостей), просмотр и
отмена своих броней, профиль, превью маршрута заезда.

Проект выполнен в рамках летней школы 2026.

О проекте и референсе

Приложение построено на основе референсного проекта «Волна» — учебного
клиентского приложения для записи на групповые SUP-прогулки, разобранного на
лекциях школы. Общая архитектура, стек (Kotlin Multiplatform + Compose,
чистая архитектура, MVI, Go-бэкенд), структура требований/дизайна и часть
доменной логики (авторизация по OTP, доступность мест, расчёт цены, отмена
брони) переиспользованы из референса и адаптированы под предметную область
картинг-центра.

Что было переработано под картинг-тематику:


превью маршрута/трассы в карточке заезда (убрана морская
тематика — см. 02-development/tasks/BUG-001-route-map-water-color.md)


Пакет/applicationId (com.volna.app) и внутренние имена классов сохранены
от референсного проекта — переименование пакета не входило в объём этой
итерации.

Структура репозитория

01-analysis/       — требования, use-cases, user stories, дизайн-бриф,
                      мобильная спецификация экранов (SCR-*/BS-*/LOGIC-*),
                      OpenAPI-контракты, промпты
02-development/     — планы реализации (BE/клиент) и отчёты по фичам/багам
  └─ tasks/         — FEATURE-*.md, BUG-*.md, TESTING.md (см. ниже)
backend/            — Go REST API + PostgreSQL + миграции + k6-нагрузочные тесты
client/             — Kotlin Multiplatform клиент (Android, iOS, Web)
requirements-review.md,
requirements-rework-tasks.md,
todo_after_review.md — ревью требований и бэклог доработок референса

Как запустить

Подробный гайд: LOCAL_DEV_GUIDE.md. Кратко:

1. База данных и backend

bashcd backend
docker compose --profile db up -d db   # поднять PostgreSQL
make migrate                            # применить миграции + dev-seed
make run                                # запустить API на :8080

Проверка, что backend жив:

bashcurl http://127.0.0.1:8080/healthz

2. Клиент

bashcd client


Android — открыть client/ в Android Studio, выбрать конфигурацию
androidApp, запустить на эмуляторе/устройстве (▶ Run), либо из
консоли:


bash  ./gradlew :androidApp:assembleDebug


Web — dev-сервер:


bash  ./gradlew :webApp:wasmJsBrowserDevelopmentRun


iOS — открыть client/iosApp/iosApp.xcodeproj в Xcode, запустить
схему iosApp.


По умолчанию клиент обращается к http://localhost:8080. Для Android-эмулятора,
если запросы не доходят до backend на хост-машине, используйте 10.0.2.2:8080
вместо localhost.


Документация по фичам, багам и тестам

Все отчёты по реализованным задачам собраны в 02-development/tasks/:

ФайлСодержаниеFEATURE-001-auth-otp.mdАвторизация по телефону и OTPFEATURE-002-catalog-filters.mdКаталог слотов и фильтрыFEATURE-003-booking-flow.mdОформление, просмотр и отмена брониBUG-001-route-map-water-color.mdБаг с цветовой схемой превью маршрута и его причина (Delta Install)TESTING.mdТаблица автотестов + сценарии ручного регрессионного тестирования

Каждый файл оформлен по единой структуре: симптом/цель → требования →
реализация → промпты → проверка → коммит.

Промпты


01-analysis/prompts/good-prompts.md — промпты по итоговому коду фич.

Тесты

bashcd client
./gradlew :shared:allTests --console=plain

Юнит-тесты покрывают доменную логику доступности мест, расчёта цены, правила
отмены брони и валидацию телефонного номера — см. TESTING.md для полного
списка тест-кейсов, включая ручные сценарии регрессии.

Требования к окружению


Docker Compose
Go 1.23+
JDK 17+
Android Studio (или IntelliJ IDEA) — для Android/Web
Xcode — для iOS
Node.js/npm — только для команд lint/bundle OpenAPI из 01-analysis/api

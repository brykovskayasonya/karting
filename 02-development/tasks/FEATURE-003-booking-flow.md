# FEATURE-003: Оформление, просмотр и отмена записи (бронирования)

## Цель

Клиент оформляет запись на выбранный слот (себя + гостей, выбор варианта
доски), видит список своих записей и их детали, может отменить запись с учётом
правил ранней/поздней отмены.

## Требования

- `01-analysis/5-mobile-app-spec/SCR-004-booking.md` — оформление брони.
- `01-analysis/5-mobile-app-spec/BS-002-booking-success.md` — экран успеха.
- `01-analysis/5-mobile-app-spec/SCR-005-my-bookings.md` — список записей.
- `01-analysis/5-mobile-app-spec/SCR-006-booking-details.md` — детали записи.
- `01-analysis/5-mobile-app-spec/BS-003-cancel-confirm.md` — подтверждение отмены.
- `01-analysis/5-mobile-app-spec/09_Логики/LOGIC-002_Расчёт-доступности.md`,
  `LOGIC-003_Расчёт-цены-брони.md` — доступность мест/прокатного фонда, расчёт цены.
- `01-analysis/api/bookings/api.yaml`, `models.yaml` — контракт API.

## Реализация

- `booking/BookingContracts.kt` — `BookingRepository`
  (`createBooking`/`listBookings`/`getBooking`/`cancelBooking`),
  `IdempotencyKey`/`IdempotencyKeyFactory` (защита от дублирования запроса
  при повторной отправке формы).
- `booking/data/KtorBookingRepository.kt` + `BookingDto.kt` + `BookingMappers.kt`
  — реализация поверх API.
- `booking/data/RandomIdempotencyKeyFactory.kt` — генерация ключа идемпотентности.
- `booking/presentation/BookingFormStore.kt` + `BookingFormScreen.kt` —
  форма записи: число гостей, выбор доски (своя/прокатная) на каждое место,
  расчёт итоговой цены (`LOGIC-003`), отправка `createBooking`.
- `booking/presentation/BookingListStore.kt` + `BookingListScreen.kt` — список
  записей клиента (юнит-тест: `BookingListStoreTest.kt`).
- `booking/presentation/BookingDetailsStore.kt` + `BookingDetailsScreen.kt` —
  детали одной записи + отмена (юнит-тест: `BookingDetailsStoreTest.kt`).
- `domain/policy/CancellationPolicy.kt` — правило ранней (≥2ч) / поздней (<2ч)
  отмены (юнит-тест: `DomainPolicyTest.kt`).

## Промпты

см `01-analysis/prompts/good-prompts.md`

## Проверка

Ключевая бизнес-логика (доступность мест/прокатного фонда, расчёт цены,
граница ранней/поздней отмены) покрыта автотестами
(`DomainPolicyTest`, `BookingListStoreTest`, `BookingDetailsStoreTest`) —
см. `TESTING.md`. Отдельного цикла сквозного ручного тестирования формы
записи на устройстве в рамках этой сдачи проведено не было — отмечено как
зона для последующей проверки.

## Коммит

Включено в общий коммит с документацией фич/багов перед сдачей задания.

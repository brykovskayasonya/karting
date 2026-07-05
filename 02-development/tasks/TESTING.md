# TESTING: Тест-кейсы клиента 

## Автотесты (уже реализованы)

| Файл | Тест-кейс | Что проверяет |
|---|---|---|
| `domain/policy/DomainPolicyTest.kt` | `availabilityIsLimitedByFreeSeatsRouteCapAndClientMaximum` | Доступность мест = `min(free_seats, route.capacity_cap, 3)` (LOGIC-002) |
| `domain/policy/DomainPolicyTest.kt` | `rentalBoardsCannotExceedFreeRentalBoards` | Прокатный фонд считается отдельно от мест группы |
| `domain/policy/DomainPolicyTest.kt` | `validDraftHasNoAvailabilityViolation` | Корректный черновик записи не даёт нарушений доступности |
| `domain/policy/DomainPolicyTest.kt` | `bookingPriceUsesSeatAndRentalPrices` | Цена = `price*seats + rental_price*rental_count` (LOGIC-003) |
| `domain/policy/DomainPolicyTest.kt` | `bookingPriceForExistingBookingIsDerivedFromSlotPrices` | Цена существующей брони берётся из цен слота |
| `domain/policy/DomainPolicyTest.kt` | `exactlyTwoHoursBeforeStartIsEarlyCancellation` | Граница ранней/поздней отмены — ровно 2 часа (LOGIC-004) |
| `booking/presentation/BookingListStoreTest.kt` | `refreshFailureKeepsExistingBookingsAndShowsMessage` | При ошибке обновления список брони не затирается, показывается сообщение об ошибке |
| `booking/presentation/BookingDetailsStoreTest.kt` | `slotStartedCancelFailureClosesSheetAndRefreshesBooking` | Отмена брони после старта слота — шторка закрывается, данные брони обновляются |
| `core/phone/PhoneInputCoreTest.kt` | `sanitize keeps ten local digits from formatted phone` | Очистка ввода телефона оставляет 10 локальных цифр |
| `core/phone/PhoneInputCoreTest.kt` | `sanitize limits user input to ten digits` | Ввод больше 10 цифр обрезается |
| `core/phone/PhoneInputCoreTest.kt` | `normalize converts local digits to e164` | Нормализация номера в формат E.164 |
| `core/phone/PhoneInputCoreTest.kt` | `format displays local and e164 values with same mask` | Маска отображения одинакова для локального/E.164 значений |
| `core/phone/PhoneInputCoreTest.kt` | `complete russian phone has ten local digits` | Полный российский номер — ровно 10 локальных цифр |

**Запуск:** `./gradlew :shared:allTests --console=plain` (или `:shared:testDebugUnitTest` для Android-таргета).

## Ручные тест-кейсы (для регрессии перед сдачей)

| № | Сценарий | Ожидаемый результат |
|---|---|---|
| M1 | Запросить OTP-код на телефон, ввести неверный код | Показывается ошибка, повторный ввод возможен |
| M2 | Ввести верный код | Сессия создаётся, переход на список слотов |
| M3 | Отфильтровать слоты по "Сегодня" + типу маршрута | Список сужается по обоим условиям (AND между группами) |
| M4 | Записаться на слот с гостем и прокатной доской | Цена и доступность мест/прокатного фонда считаются корректно |
| M5 | Дважды быстро нажать "Записаться" на форме | Создаётся только одна запись (idempotency key) |
| M6 | Отменить запись за 3 часа до старта | Ранняя отмена, место возвращается в слот |
| M7 | Отменить запись за 30 минут до старта | Поздняя отмена, место не освобождается |
| M8 | Открыть превью маршрута (карта) | Заглушка карты показывает актуальную цветовую схему (см. `BUG-001`) |
| M9 | Перезапустить приложение после входа | Сессия сохраняется, повторный вход не требуется |

## Найденные и исправленные баги

См. отдельные файлы:
- `BUG-001-route-map-water-color.md` — цветовая схема заглушки карты маршрута.
- *(добавить BUG-002, BUG-003 при наличии — см. `todo_after_review.md`, разделы
  P1/P2 backlog, как источник кандидатов на баги, например п.10 "Профиль:
  инлайн-редактирование" или п.12 "Фильтры: пресеты Эта неделя/Выходные" —
  если что-то из этого фактически не соответствует коду, это баг/расхождение
  с требованиями, которое можно оформить как BUG-002.)*

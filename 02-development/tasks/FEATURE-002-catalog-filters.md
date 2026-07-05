# FEATURE-002: Каталог слотов со списком и фильтрами

## Цель

Клиент видит список доступных слотов (заездов/прогулок) на карте и фильтрует
их по дате, типу маршрута, инструктору и наличию свободных мест — чтобы найти
подходящий вариант и перейти к записи.

## Требования

- `01-analysis/5-mobile-app-spec/SCR-002-slot-list.md` — список слотов.
- `01-analysis/5-mobile-app-spec/SCR-003-slot-card.md` — карточка/детали слота.
- `01-analysis/5-mobile-app-spec/BS-001-filters.md` — блок фильтров.
- `01-analysis/5-mobile-app-spec/09_Логики/LOGIC-005_Фильтрация-слотов.md` —
  логика фильтрации (OR внутри группы фильтров, AND между группами).
- `01-analysis/api/slots/api.yaml`, `models.yaml` — контракт API.

## Реализация

- `catalog/CatalogContracts.kt` — `SlotFilters` (диапазон дат, типы маршрута,
  инструкторы, только доступные), `SlotRepository`/`InstructorRepository`,
  пагинация через `PageRequest`/`Page<T>`.
- `catalog/data/KtorCatalogRepository.kt` + `CatalogDto.kt` + `CatalogMappers.kt`
  — реализация поверх API, маппинг слотов/инструкторов.
- `catalog/presentation/SlotListStore.kt` + `SlotListScreen.kt` — стор и экран
  списка с фильтрами (пресеты по датам, мультивыбор маршрутов/инструкторов).
- `catalog/presentation/SlotDetailsStore.kt` + `SlotDetailsScreen.kt` —
  карточка слота с деталями маршрута, ценой, доступностью мест.
- `catalog/presentation/SlotSharedUi.kt` — переиспользуемые UI-компоненты
  (форматирование даты/времени слота).

## Промпты

см `01-analysis/prompts/good-prompts.md` 

## Проверка

Функциональность реализована согласно контракту API и ТЗ (`SCR-002/003`,
`BS-001`, `LOGIC-005`); отдельного цикла ручного регрессионного тестирования
по всем сценариям фильтрации в рамках этой сдачи проведено не было —
отмечено как зона для последующей проверки.

## Коммит

Включено в общий коммит с документацией фич/багов перед сдачей задания.

package com.volna.app.rating

import com.volna.app.domain.model.BookingId
import com.volna.app.domain.model.InstructorId
import com.volna.app.domain.model.MarshalRating

/**
 * MVP: без настоящего API. Реализация (InMemoryRatingRepository) хранит оценки
 * только в памяти клиента на время сессии — этого достаточно, чтобы показать
 * поведение экрана "Оценить маршала". Заменить на Ktor-репозиторий, когда
 * на бэкенде появится соответствующий эндпоинт.
 */
interface RatingRepository {
    suspend fun submitRating(
        bookingId: BookingId,
        instructorId: InstructorId,
        score: Int,
        comment: String,
    ): Result<MarshalRating>

    suspend fun getRating(bookingId: BookingId): MarshalRating?
}

package com.volna.app.domain.model

import kotlinx.datetime.Instant

/**
 * Оценка маршала клиентом после завершенного заезда.
 * MVP: хранится только на клиенте (см. rating/data/InMemoryRatingRepository),
 * без реального API — по ТЗ бэкенд для этой фичи пока не нужен.
 */
data class MarshalRating(
    val id: RatingId,
    val bookingId: BookingId,
    val instructorId: InstructorId,
    val score: Int,
    val comment: String,
    val createdAt: Instant,
)

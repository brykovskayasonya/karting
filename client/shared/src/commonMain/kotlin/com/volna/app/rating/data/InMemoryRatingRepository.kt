package com.volna.app.rating.data

import com.volna.app.domain.model.BookingId
import com.volna.app.domain.model.InstructorId
import com.volna.app.domain.model.MarshalRating
import com.volna.app.domain.model.RatingId
import com.volna.app.rating.RatingRepository
import kotlin.time.Clock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryRatingRepository : RatingRepository {
    private val mutex = Mutex()
    private val ratings = mutableMapOf<BookingId, MarshalRating>()
    private var nextId = 1

    override suspend fun submitRating(
        bookingId: BookingId,
        instructorId: InstructorId,
        score: Int,
        comment: String,
    ): Result<MarshalRating> {
        if (score !in 1..5) {
            return Result.failure(IllegalArgumentException("Оценка должна быть от 1 до 5"))
        }
        return mutex.withLock {
            val rating = MarshalRating(
                id = RatingId("local-${nextId++}"),
                bookingId = bookingId,
                instructorId = instructorId,
                score = score,
                comment = comment.trim(),
                createdAt = Clock.System.now(),
            )
            ratings[bookingId] = rating
            Result.success(rating)
        }
    }

    override suspend fun getRating(bookingId: BookingId): MarshalRating? =
        mutex.withLock { ratings[bookingId] }
}

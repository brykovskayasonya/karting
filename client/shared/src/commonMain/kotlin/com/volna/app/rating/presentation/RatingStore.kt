package com.volna.app.rating.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volna.app.core.logging.AppLogger
import com.volna.app.core.mvi.MviStore
import com.volna.app.core.ui.ActionStatus
import com.volna.app.domain.model.BookingId
import com.volna.app.domain.model.InstructorId
import com.volna.app.rating.RatingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RatingState(
    val visible: Boolean = false,
    val bookingId: BookingId? = null,
    val instructorId: InstructorId? = null,
    val marshalName: String = "",
    val score: Int = 0,
    val comment: String = "",
    val submitStatus: ActionStatus = ActionStatus.Idle,
    val ratedBookingIds: Set<BookingId> = emptySet(),
    val message: String? = null,
) {
    val isSubmitting: Boolean = submitStatus == ActionStatus.Submitting
    val canSubmit: Boolean = score in 1..5 && !isSubmitting
}

sealed interface RatingIntent {
    data class Open(val bookingId: BookingId, val instructorId: InstructorId, val marshalName: String) : RatingIntent
    data object Dismiss : RatingIntent
    data class SetScore(val score: Int) : RatingIntent
    data class SetComment(val comment: String) : RatingIntent
    data object Submit : RatingIntent
    data object MessageShown : RatingIntent
    data object Reset : RatingIntent
}

sealed interface RatingEffect {
    data object Submitted : RatingEffect
}

// Новая фича MVP «Апекс»: отзыв о маршале после завершенного заезда.
// Без реального API — см. RatingRepository / InMemoryRatingRepository.
class RatingStore(
    private val ratingRepository: RatingRepository,
    scope: CoroutineScope? = null,
) : ViewModel(), MviStore<RatingState, RatingIntent, RatingEffect> {
    private val mutableState = MutableStateFlow(RatingState())
    private val effects = Channel<RatingEffect>(Channel.BUFFERED)
    private val storeScope = scope ?: viewModelScope

    override val state: StateFlow<RatingState> = mutableState

    override fun accept(intent: RatingIntent) {
        when (intent) {
            is RatingIntent.Open -> open(intent.bookingId, intent.instructorId, intent.marshalName)
            RatingIntent.Dismiss -> mutableState.update {
                it.copy(visible = false, message = null)
            }
            is RatingIntent.SetScore -> mutableState.update { it.copy(score = intent.score, message = null) }
            is RatingIntent.SetComment -> mutableState.update { it.copy(comment = intent.comment) }
            RatingIntent.Submit -> submit()
            RatingIntent.MessageShown -> mutableState.update { it.copy(message = null) }
            RatingIntent.Reset -> mutableState.value = RatingState()
        }
    }

    override suspend fun effects(): RatingEffect = effects.receive()

    private fun open(bookingId: BookingId, instructorId: InstructorId, marshalName: String) {
        mutableState.update {
            it.copy(
                visible = true,
                bookingId = bookingId,
                instructorId = instructorId,
                marshalName = marshalName,
                score = 0,
                comment = "",
                submitStatus = ActionStatus.Idle,
                message = null,
            )
        }
    }

    private fun submit() {
        val state = mutableState.value
        val bookingId = state.bookingId ?: return
        val instructorId = state.instructorId ?: return
        if (!state.canSubmit) return

        storeScope.launch {
            mutableState.update { it.copy(submitStatus = ActionStatus.Submitting, message = null) }
            ratingRepository.submitRating(
                bookingId = bookingId,
                instructorId = instructorId,
                score = state.score,
                comment = state.comment,
            ).fold(
                onSuccess = {
                    mutableState.update {
                        it.copy(
                            visible = false,
                            submitStatus = ActionStatus.Idle,
                            ratedBookingIds = it.ratedBookingIds + bookingId,
                            message = "Спасибо за оценку!",
                        )
                    }
                    effects.send(RatingEffect.Submitted)
                },
                onFailure = { failure ->
                    AppLogger.e(failure, "Failed to submit marshal rating")
                    mutableState.update {
                        it.copy(
                            submitStatus = ActionStatus.Idle,
                            message = "Не удалось отправить оценку. Попробуйте снова.",
                        )
                    }
                },
            )
        }
    }
}

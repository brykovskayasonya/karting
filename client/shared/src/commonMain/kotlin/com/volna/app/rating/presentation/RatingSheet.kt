package com.volna.app.rating.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.volna.app.core.theme.VolnaTheme
import com.volna.app.uikit.icons.Icons
import com.volna.app.uikit.icons.Info
import com.volna.app.uikit.icons.VolnaIcon
import kotlinx.coroutines.delay

// MVP «Апекс»: оценка маршала после завершенного заезда. Мок без реального API.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingSheet(
    state: RatingState,
    onIntent: (RatingIntent) -> Unit,
) {
    LaunchedEffect(state.message) {
        if (state.message != null && !state.visible) {
            delay(2_500)
            onIntent(RatingIntent.MessageShown)
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { !state.isSubmitting },
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!state.isSubmitting) onIntent(RatingIntent.Dismiss)
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = VolnaTheme.tokens.radius.lg,
            topEnd = VolnaTheme.tokens.radius.lg,
        ),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = VolnaTheme.tokens.spacing.md,
                    end = VolnaTheme.tokens.spacing.md,
                    bottom = VolnaTheme.tokens.spacing.md,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VolnaTheme.tokens.spacing.md),
        ) {
            Text(
                text = "Оценить маршала",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = state.marshalName.ifBlank { "Маршал заезда" },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            StarPicker(
                score = state.score,
                onScoreSelected = { onIntent(RatingIntent.SetScore(it)) },
            )
            OutlinedTextField(
                value = state.comment,
                onValueChange = { onIntent(RatingIntent.SetComment(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Комментарий (необязательно)") },
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            state.message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VolnaTheme.tokens.spacing.xxs),
            ) {
                VolnaIcon(
                    imageVector = Icons.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 16.dp,
                )
                Text(
                    text = "Оценка пока сохраняется только на этом устройстве",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = { onIntent(RatingIntent.Submit) },
                enabled = state.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(VolnaTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(VolnaTheme.tokens.radius.pill),
            ) {
                Text(if (state.isSubmitting) "Отправляем…" else "Отправить оценку")
            }
        }
    }
}

@Composable
private fun StarPicker(
    score: Int,
    onScoreSelected: (Int) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(VolnaTheme.tokens.spacing.xs)) {
        for (value in 1..5) {
            val filled = value <= score
            Text(
                text = if (filled) "★" else "☆",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onScoreSelected(value) },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                color = if (filled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

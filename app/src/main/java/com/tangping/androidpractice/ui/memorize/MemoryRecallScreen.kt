package com.tangping.androidpractice.ui.memorize

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.model.memorize.QuestionCard
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground
import com.tangping.androidpractice.utils.StringUtils
import kotlinx.coroutines.delay

interface MemoryRecallScreenCallback {
    fun onNavigateBack()
}

@Composable
fun MemoryRecallScreen(
    callback: MemoryRecallScreenCallback? = null,
    viewModel: MemoryRecallViewModel = hiltViewModel(),
    fileName: String? = null,
    defaultShowCancelDialog: Boolean = false
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current
    var needRefreshDueText by rememberSaveable { mutableStateOf(false) }
    var showCancelDialog by rememberSaveable { mutableStateOf(defaultShowCancelDialog) }
    var showAnswer by rememberSaveable { mutableStateOf(false) }
    val answerScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (fileName != null) {
            viewModel.dispatch(
                MemoryRecallViewAction.UseLocalCache(fileName),
                context
            )
        }

        viewModel.viewEvents.collect {
            when (it) {
                is MemoryRecallViewEvent.DisplayMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is MemoryRecallViewEvent.DismissPopup -> {
                    viewModel.dispatch(MemoryRecallViewAction.ChangeCard, context)
                }
                is MemoryRecallViewEvent.RefreshDueTime -> {
                    needRefreshDueText = true
                    delay(1000)
                    needRefreshDueText = false
                    viewModel.dispatch(MemoryRecallViewAction.ChangeCard, context)
                }
                is MemoryRecallViewEvent.HideAnswer -> {
                    showAnswer = false
                }
            }
        }
    }

    ConstraintLayout(modifier = Modifier
        .background(darkBackground)
        .fillMaxSize()
    ) {
        val (btnClose, mainArea, controlArea, cancelDialog) = createRefs()

        IconButton(
            onClick = {
                showCancelDialog = true
            },
            modifier = Modifier.constrainAs(btnClose) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        ) {
            Icon(
                Icons.Sharp.Close,
                contentDescription = "Close Button",
                tint = Color.White
            )
        }

        QuestionAndAnswerColumn(
            modifier = Modifier
                .constrainAs(mainArea) {
                    top.linkTo(btnClose.bottom)
                    bottom.linkTo(controlArea.top)
                    start.linkTo(parent.start)
                    height = Dimension.fillToConstraints
                },
            currentCard = viewStates.currentCard,
            needRefreshDueText = needRefreshDueText,
            showAnswer = showAnswer,
            answerScrollState = answerScrollState,
            onAnswerTipClick = {
                showAnswer = true
            }
        )

        Row(
            modifier = Modifier
                .constrainAs(controlArea) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .height(60.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickUnfamiliar, context)
                    }
                },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp),
                enabled = showAnswer
            ) {
                Text(text = stringResource(id = R.string.unfamiliar))
            }

            Button(
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickHesitated, context)
                    }
                },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp),
                enabled = showAnswer
            ) {
                Text(text = stringResource(id = R.string.hesitated))
            }

            Button(
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickRecalled, context)
                    }
                },
                shape = RectangleShape,
                enabled = showAnswer
            ) {
                Text(text = stringResource(id = R.string.recalled))
            }
        }

        if (showCancelDialog) {
            CancelDialog(
                modifier = Modifier.constrainAs(cancelDialog) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                onCancel = {
                    showCancelDialog = false
                },
                onConfirm = {
                    viewModel.dispatch(
                        MemoryRecallViewAction.SaveQuestionDeck(fileName),
                        context
                    )
                    callback?.onNavigateBack()
                }
            )
        }
    }
}

@Composable
private fun QuestionAndAnswerColumn(
    modifier: Modifier,
    currentCard: QuestionCard? = null,
    needRefreshDueText: Boolean,
    showAnswer: Boolean,
    answerScrollState: ScrollState,
    onAnswerTipClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 6.dp)
                .weight(0.5f)
                .background(gayBackground)
        ) {
            val (questionText, divider, dueTime) = createRefs()

            Text(
                text = currentCard?.question ?: "",
                color = Color.White,
                modifier = Modifier
                    .constrainAs(questionText) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(3.dp)
            )

            if (currentCard != null) {
                Divider(
                    modifier = Modifier
                        .constrainAs(divider) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom, margin = 45.dp)
                        },
                    color = darkBackground,
                    thickness = 1.dp
                )

                DueTimeText(
                    modifier = Modifier
                        .constrainAs(dueTime) {
                            top.linkTo(divider.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    dueTimeString = "${stringResource(id = R.string.due_time)}: ${StringUtils.convertTimestampToString(currentCard.dueTime)}",
                    needRefresh = needRefreshDueText
                )
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 6.dp)
                .weight(0.5f)
                .background(gayBackground)
                .clickable {
                    if (currentCard != null) {
                        onAnswerTipClick?.invoke()
                    }
                }
                .verticalScroll(answerScrollState)
        ) {
            val (tipText, answerText) = createRefs()

            if (showAnswer.not() && currentCard != null) {
                Text(
                    text = stringResource(id = R.string.show_answer_tip),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .constrainAs(tipText) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            } else {
                Text(
                    text = currentCard?.answer ?: "",
                    color = Color.White,
                    modifier = Modifier
                        .constrainAs(answerText) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .padding(3.dp)
                )
            }
        }
    }
}

@Composable
private fun DueTimeText(
    modifier: Modifier,
    dueTimeString: String,
    needRefresh: Boolean
) {
    val color by animateColorAsState(
        targetValue = if (needRefresh) Color.Green else Color.LightGray,
        animationSpec = tween(durationMillis = 500),
        label = "dueTimeColor"
    )

    Text(
        text = dueTimeString,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun CancelDialog(
    modifier: Modifier,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .background(
                darkBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.cancel_dialog_tip),
            color = Color.White
        )

        Row(modifier = Modifier.padding(top = 6.dp)) {
            Button(
                onClick = {
                    onCancel.invoke()
                },
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }

            Button(onClick = {
                onConfirm.invoke()
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewMemoryRecallScreen() {
    MemoryRecallScreen(defaultShowCancelDialog = true)
}
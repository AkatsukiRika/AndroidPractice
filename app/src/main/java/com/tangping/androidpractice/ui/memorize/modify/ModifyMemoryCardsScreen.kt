package com.tangping.androidpractice.ui.memorize.modify

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

interface ModifyMemoryCardsCallback {
    fun onNavigateBack()
}

@Composable
fun ModifyMemoryCardsScreen(
    callback: ModifyMemoryCardsCallback? = null,
    viewModel: ModifyMemoryCardsViewModel = hiltViewModel(),
    fileName: String? = null
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fileName?.let {
            viewModel.dispatch(
                ModifyMemoryCardsAction.ReadJson(fileName),
                context
            )
        }

        viewModel.viewEvents.collect {
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, btnDone, qaColumn, seeker) = createRefs()

        CloseButton(
            modifier = Modifier.constrainAs(btnClose) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            onClick = {
                callback?.onNavigateBack()
            }
        )

        QuestionSeeker(
            modifier = Modifier.constrainAs(seeker) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
            },
            currentIndex = viewStates.currentIndex + 1,
            totalCount = viewStates.questionCards.size,
            onLastEntry = {
                viewModel.dispatch(
                    ModifyMemoryCardsAction.SetIndex(viewStates.currentIndex - 1),
                    context
                )
            },
            onNextEntry = {
                viewModel.dispatch(
                    ModifyMemoryCardsAction.SetIndex(viewStates.currentIndex + 1),
                    context
                )
            },
            onAddEntry = {
                val index = if (viewStates.currentIndex >= viewStates.questionCards.size - 1) {
                    null
                } else viewStates.currentIndex
                viewModel.dispatch(
                    ModifyMemoryCardsAction.AddNewEntry(index),
                    context
                )
            }
        )

        DoneButton(
            modifier = Modifier.constrainAs(btnDone) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }
        )

        QuestionAndAnswerColumn(
            modifier = Modifier.constrainAs(qaColumn) {
                top.linkTo(btnClose.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                height = Dimension.fillToConstraints
            },
            question = viewStates.currentCard?.question ?: "",
            onQuestionChange = {
                viewModel.dispatch(
                    ModifyMemoryCardsAction.ChangeQuestion(question = it),
                    context
                )
            },
            answer = viewStates.currentCard?.answer ?: "",
            onAnswerChange = {
                viewModel.dispatch(
                    ModifyMemoryCardsAction.ChangeAnswer(answer = it),
                    context
                )
            }
        )
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            Icons.Sharp.Close,
            contentDescription = "Close Button",
            tint = Color.White
        )
    }
}

@Composable
private fun QuestionSeeker(
    modifier: Modifier,
    currentIndex: Int = 0,
    totalCount: Int = 0,
    onLastEntry: () -> Unit,
    onNextEntry: () -> Unit,
    onAddEntry: () -> Unit
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(22.dp)
                .padding(end = 3.dp)
        ) {
            Icon(
                Icons.Sharp.Delete,
                contentDescription = "Delete Entry",
                tint = Color.White
            )
        }

        IconButton(
            onClick = { onLastEntry.invoke() },
            modifier = Modifier
                .width(25.dp)
                .padding(end = 3.dp)
        ) {
            Icon(
                Icons.Sharp.KeyboardArrowLeft,
                contentDescription = "Last Entry",
                tint = if (currentIndex == 0) Color.Gray else Color.White
            )
        }

        Text(
            text = stringResource(R.string.entry_indicator, currentIndex, totalCount),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { onNextEntry.invoke() },
            modifier = Modifier
                .width(25.dp)
                .padding(start = 3.dp)
        ) {
            Icon(
                Icons.Sharp.KeyboardArrowRight,
                contentDescription = "Next Entry",
                tint = Color.White
            )
        }

        IconButton(
            onClick = { onAddEntry.invoke() },
            modifier = Modifier
                .width(22.dp)
                .padding(start = 3.dp)
        ) {
            Icon(
                Icons.Sharp.AddCircle,
                contentDescription = "Create Entry",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun DoneButton(
    modifier: Modifier
) {
    IconButton(
        onClick = { /*TODO*/ },
        modifier = modifier
    ) {
        Icon(
            Icons.Sharp.Done,
            contentDescription = "Done Button",
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionAndAnswerColumn(
    modifier: Modifier,
    question: String,
    onQuestionChange: (String) -> Unit,
    answer: String,
    onAnswerChange: (String) -> Unit
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
            val (questionText) = createRefs()

            TextField(
                value = question,
                onValueChange = {
                    onQuestionChange.invoke(it)
                },
                modifier = Modifier
                    .constrainAs(questionText) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    .padding(3.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.White
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.question_here),
                        color = Color.Gray
                    )
                }
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 6.dp)
                .weight(0.5f)
                .background(gayBackground)
        ) {
            val (answerText) = createRefs()

            TextField(
                value = answer,
                onValueChange = {
                    onAnswerChange.invoke(it)
                },
                modifier = Modifier
                    .constrainAs(answerText) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(3.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.White
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.answer_here),
                        color = Color.Gray
                    )
                }
            )
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
@Preview(showSystemUi = true, showBackground = true)
fun PreviewModifyMemoryCardsScreen() {
    ModifyMemoryCardsScreen()
}
package com.tangping.androidpractice.ui.memorize.modify

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.colorGreen
import com.tangping.androidpractice.ui.theme.colorRed
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

interface ModifyMemoryCardsCallback {
    fun onNavigateBack()
}

@Composable
fun ModifyMemoryCardsScreen(
    callback: ModifyMemoryCardsCallback? = null,
    viewModel: ModifyMemoryCardsViewModel = hiltViewModel(),
    fileName: String? = null,
    defaultShowDeletePopup: Boolean = false
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current
    var showDeletePopup by rememberSaveable { mutableStateOf(defaultShowDeletePopup) }

    LaunchedEffect(Unit) {
        fileName?.let {
            viewModel.dispatch(
                ModifyMemoryCardsAction.ReadJson(fileName),
                context
            )
        }

        viewModel.viewEvents.collect {
            when (it) {
                is ModifyMemoryCardsEvent.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is ModifyMemoryCardsEvent.DismissDeletePopup -> {
                    showDeletePopup = false
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, btnDone, qaColumn, seeker, deletePopup) = createRefs()

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
                if (showDeletePopup) {
                    return@QuestionSeeker
                }
                viewModel.dispatch(
                    ModifyMemoryCardsAction.SetIndex(viewStates.currentIndex - 1),
                    context
                )
            },
            onNextEntry = {
                if (showDeletePopup) {
                    return@QuestionSeeker
                }
                viewModel.dispatch(
                    ModifyMemoryCardsAction.SetIndex(viewStates.currentIndex + 1),
                    context
                )
            },
            onAddEntry = {
                if (showDeletePopup) {
                    return@QuestionSeeker
                }
                val index = if (viewStates.currentIndex >= viewStates.questionCards.size - 1) {
                    null
                } else viewStates.currentIndex
                viewModel.dispatch(
                    ModifyMemoryCardsAction.AddNewEntry(index),
                    context
                )
            },
            onDeleteEntry = {
                showDeletePopup = true
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

        if (showDeletePopup) {
            DeletePopup(
                modifier = Modifier.constrainAs(deletePopup) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onCancel = {
                    showDeletePopup = false
                },
                onDelete = {
                    viewModel.dispatch(
                        ModifyMemoryCardsAction.DeleteEntry(viewStates.currentIndex),
                        context
                    )
                }
            )
        }
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
    onAddEntry: () -> Unit,
    onDeleteEntry: () -> Unit
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onDeleteEntry.invoke() },
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
private fun DeletePopup(
    modifier: Modifier,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .wrapContentSize()
            .background(
                Color.DarkGray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        val (title, buttons) = createRefs()

        Text(
            text = stringResource(id = R.string.confirm_delete),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 12.dp)
                }
                .padding(horizontal = 48.dp)
        )

        Row(
            modifier = Modifier
                .constrainAs(buttons) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(title.bottom)
                }
                .padding(top = 12.dp, start = 48.dp, end = 48.dp, bottom = 6.dp)
        ) {
            Button(
                onClick = { onCancel.invoke() },
                modifier = Modifier.padding(end = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorGreen)
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = Color.White
                )
            }

            Button(
                onClick = { onDelete.invoke() },
                colors = ButtonDefaults.buttonColors(containerColor = colorRed)
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = Color.White
                )
            }
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
    ModifyMemoryCardsScreen(defaultShowDeletePopup = true)
}
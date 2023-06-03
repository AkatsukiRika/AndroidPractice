package com.tangping.androidpractice.ui.memorize.modify

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

interface ModifyMemoryCardsCallback {
    fun onNavigateBack()
}

@Composable
fun ModifyMemoryCardsScreen(
    callback: ModifyMemoryCardsCallback? = null,
    fileName: String? = null
) {
    var question by rememberSaveable { mutableStateOf("") }
    var answer by rememberSaveable { mutableStateOf("") }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, btnDone, qaColumn) = createRefs()

        CloseButton(
            modifier = Modifier.constrainAs(btnClose) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            onClick = {
                callback?.onNavigateBack()
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
            question = question,
            onQuestionChange = {
                question = it
            },
            answer = answer,
            onAnswerChange = {
                answer = it
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
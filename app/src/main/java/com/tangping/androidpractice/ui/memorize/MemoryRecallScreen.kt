package com.tangping.androidpractice.ui.memorize

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

interface MemoryRecallScreenCallback {
    fun onBtnCloseClick()
}

@Composable
fun MemoryRecallScreen(
    callback: MemoryRecallScreenCallback? = null,
    viewModel: MemoryRecallViewModel = hiltViewModel()
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.dispatch(MemoryRecallViewAction.ChangeCard, context)
        viewModel.viewEvents.collect {
            when (it) {
                is MemoryRecallViewEvent.DisplayMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ConstraintLayout(modifier = Modifier
        .background(darkBackground)
        .fillMaxSize()
    ) {
        val (btnClose, mainArea, controlArea) = createRefs()

        IconButton(
            onClick = {
                callback?.onBtnCloseClick()
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

        Column(
            modifier = Modifier
                .constrainAs(mainArea) {
                    top.linkTo(btnClose.bottom)
                    bottom.linkTo(controlArea.top)
                    start.linkTo(parent.start)
                    height = Dimension.fillToConstraints
                }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(all = 6.dp),
                color = gayBackground
            ) {
                Text(
                    text = viewStates.currentCard?.question ?: "",
                    color = Color.White
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(all = 6.dp),
                color = gayBackground
            ) {
                Text(
                    text = viewStates.currentCard?.answer ?: "",
                    color = Color.White
                )
            }
        }

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
                onClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(text = stringResource(id = R.string.unfamiliar))
            }

            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(text = stringResource(id = R.string.hesitated))
            }

            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.recalled))
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewMemoryRecallScreen() {
    MemoryRecallScreen()
}
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
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
    var url by remember { mutableStateOf("") }
    var showPopup by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is MemoryRecallViewEvent.DisplayMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is MemoryRecallViewEvent.DismissPopup -> {
                    showPopup = false
                    viewModel.dispatch(MemoryRecallViewAction.ChangeCard, context)
                }
            }
        }
    }

    ConstraintLayout(modifier = Modifier
        .background(darkBackground)
        .fillMaxSize()
    ) {
        val (btnClose, mainArea, controlArea, popup) = createRefs()

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
                    color = Color.White,
                    modifier = Modifier.padding(3.dp)
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
                    color = Color.White,
                    modifier = Modifier.padding(3.dp)
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
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickUnfamiliar, context)
                        dispatch(MemoryRecallViewAction.ChangeCard, context)
                    }
                },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(text = stringResource(id = R.string.unfamiliar))
            }

            Button(
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickHesitated, context)
                        dispatch(MemoryRecallViewAction.ChangeCard, context)
                    }
                },
                shape = RectangleShape,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(text = stringResource(id = R.string.hesitated))
            }

            Button(
                onClick = {
                    viewModel.apply {
                        dispatch(MemoryRecallViewAction.ClickRecalled, context)
                        dispatch(MemoryRecallViewAction.ChangeCard, context)
                    }
                },
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.recalled))
            }
        }

        if (showPopup) {
            SettingsPopup(
                modifier = Modifier.constrainAs(popup) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                url = url,
                onUrlChange = {
                    url = it
                },
                onUseLocalCache = {},
                onUseRemoteData = { url ->
                    viewModel.dispatch(
                        MemoryRecallViewAction.UseRemoteData(url),
                        context
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPopup(
    modifier: Modifier,
    url: String,
    onUrlChange: (String) -> Unit,
    onUseLocalCache: () -> Unit,
    onUseRemoteData: (String) -> Unit
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
            text = stringResource(id = R.string.memorize_popup),
            color = Color.White
        )
        TextField(
            value = url,
            onValueChange = {
                onUrlChange.invoke(it)
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = darkBackground,
                textColor = Color.Green
            )
        )
        Button(onClick = {
            if (url.isEmpty()) {
                onUseLocalCache.invoke()
            } else {
                onUseRemoteData.invoke(url)
            }
        }) {
            Text(
                text = if (url.isEmpty()) {
                    stringResource(id = R.string.use_local_cache)
                } else {
                    stringResource(id = R.string.use_remote_data)
                },
                color = Color.White
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewMemoryRecallScreen() {
    MemoryRecallScreen()
}
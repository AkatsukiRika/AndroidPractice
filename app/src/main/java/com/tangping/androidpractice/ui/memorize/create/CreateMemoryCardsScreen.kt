package com.tangping.androidpractice.ui.memorize.create

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

interface CreateMemoryCardsCallback {
    fun onNavigateBack()
}

@Composable
fun CreateMemoryCardsScreen(
    callback: CreateMemoryCardsCallback? = null,
    viewModel: CreateMemoryCardsViewModel = hiltViewModel()
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current
    val jsonFiles = rememberSaveable(viewStates.jsonFiles) {
        val newList = mutableListOf<String>()
        newList.add(context.getString(R.string.create_new_file))
        newList.addAll(viewStates.jsonFiles)
        Log.i("CreateMemoryCardsScreen", "newList size=${newList.size}, jsonFiles size=${viewStates.jsonFiles.size}")
        newList
    }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, btnDone, fileSelector) = createRefs()
        var showFileSelector by rememberSaveable { mutableStateOf(true) }

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

        if (showFileSelector) {
            FileSelector(
                modifier = Modifier.constrainAs(fileSelector) {
                    top.linkTo(btnDone.bottom)
                    start.linkTo(parent.start)
                },
                onInit = {
                    viewModel.dispatch(CreateMemoryCardsAction.ScanCacheDirectory, context)
                },
                jsonFiles = jsonFiles
            )
        }
    }
}

@Composable
private fun FileSelector(
    modifier: Modifier,
    onInit: () -> Unit,
    jsonFiles: List<String>
) {
    LaunchedEffect(Unit) {
        onInit.invoke()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.select_file),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
        ) {
            items(jsonFiles) {
                Log.i("CreateMemoryCardsScreen", "jsonFiles.size=${jsonFiles.size}, current=$it")
                FileSelectorItem(fileName = it)
            }
        }
    }
}

@Composable
private fun FileSelectorItem(fileName: String) {
    Column(
        modifier = Modifier
            .clickable {

            }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(
                if (fileName == stringResource(id = R.string.create_new_file)) {
                    Color.DarkGray
                } else {
                    gayBackground
                }
            )
            .padding(6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = fileName,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Divider(
            color = darkBackground,
            thickness = 2.dp
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

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewCreateMemoryCardsScreen() {
    CreateMemoryCardsScreen()
}
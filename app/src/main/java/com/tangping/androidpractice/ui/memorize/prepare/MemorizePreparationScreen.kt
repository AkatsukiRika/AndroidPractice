package com.tangping.androidpractice.ui.memorize.prepare

import android.widget.Toast
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.tangping.androidpractice.widgets.CloseButton
import com.tangping.androidpractice.widgets.UrlPopup

interface MemorizePreparationCallback {
    fun onNavigateBack()

    fun goRecallScreen(fileName: String)
}

@Composable
fun MemorizePreparationScreen(
    callback: MemorizePreparationCallback? = null,
    viewModel: MemorizePreparationViewModel = hiltViewModel(),
    defaultShowUrlPopup: Boolean = false
) {
    val context = LocalContext.current
    val viewStates = viewModel.viewStates
    val jsonFiles = rememberSaveable(viewStates.jsonFiles) {
        val newList = mutableListOf<String>()
        newList.add(context.getString(R.string.use_remote_data))
        newList.addAll(viewStates.jsonFiles)
        newList
    }
    var showUrlPopup by remember { mutableStateOf(defaultShowUrlPopup) }
    var url by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is MemorizePreparationEvent.GoRecall -> {
                    callback?.goRecallScreen(it.fileName)
                }
                is MemorizePreparationEvent.DismissUrlPopup -> {
                    showUrlPopup = false
                }
                is MemorizePreparationEvent.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, fileSelector, urlPopup) = createRefs()

        CloseButton(
            modifier = Modifier.constrainAs(btnClose) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            onClick = {
                callback?.onNavigateBack()
            }
        )

        FileSelector(
            modifier = Modifier.constrainAs(fileSelector) {
                top.linkTo(btnClose.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                height = Dimension.fillToConstraints
            },
            onInit = {
                viewModel.dispatch(
                    MemorizePreparationAction.ScanCacheDirectory,
                    context
                )
            },
            jsonFiles = jsonFiles,
            onFileSelect = { fileName ->
                if (fileName != context.getString(R.string.use_remote_data)) {
                    callback?.goRecallScreen(fileName)
                } else {
                    showUrlPopup = true
                }
            },
        )

        if (showUrlPopup) {
            UrlPopup(
                modifier = Modifier.constrainAs(urlPopup) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                url = url,
                onUrlChange = {
                    url = it
                },
                onConfirm = {
                    viewModel.dispatch(
                        MemorizePreparationAction.UseRemoteData(it),
                        context
                    )
                },
                onClose = {
                    showUrlPopup = false
                },
                backgroundColor = gayBackground
            )
        }
    }
}

@Composable
private fun FileSelector(
    modifier: Modifier,
    onInit: () -> Unit,
    jsonFiles: List<String>,
    onFileSelect: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        onInit.invoke()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.select_file_to_memorize),
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
                FileSelectorItem(
                    fileName = it,
                    onClick = onFileSelect
                )
            }
        }
    }
}

@Composable
private fun FileSelectorItem(
    fileName: String,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            onClick.invoke(fileName)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (fileName == stringResource(id = R.string.use_remote_data)) {
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
@Preview(showBackground = true, showSystemUi = true)
fun PreviewMemorizePreparationScreen() {
    MemorizePreparationScreen(defaultShowUrlPopup = true)
}
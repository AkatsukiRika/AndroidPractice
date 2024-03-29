package com.tangping.androidpractice.ui.memorize.create

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
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
import com.tangping.androidpractice.widgets.ConfirmPopup

interface CreateMemoryCardsCallback {
    fun onNavigateBack()

    fun goModifyScreen(fileName: String)
}

@Composable
fun CreateMemoryCardsScreen(
    callback: CreateMemoryCardsCallback? = null,
    viewModel: CreateMemoryCardsViewModel = hiltViewModel(),
    defaultShowNewFilePopup: Boolean = false,
    defaultShowModifyPopup: Boolean = false
) {
    val viewStates = viewModel.viewStates
    val context = LocalContext.current
    val jsonFiles = rememberSaveable(viewStates.jsonFiles) {
        val newList = mutableListOf<String>()
        newList.add(context.getString(R.string.create_new_file))
        newList.addAll(viewStates.jsonFiles)
        Log.i(
            "CreateMemoryCardsScreen",
            "newList size=${newList.size}, jsonFiles size=${viewStates.jsonFiles.size}"
        )
        newList
    }
    var fileName by rememberSaveable {
        mutableStateOf(context.getString(R.string.default_new_file_name))
    }
    var modifyFileName by rememberSaveable { mutableStateOf("") }
    var showNewFilePopup by rememberSaveable { mutableStateOf(defaultShowNewFilePopup) }
    var showModifyPopup by rememberSaveable { mutableStateOf(defaultShowModifyPopup) }

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is CreateMemoryCardsEvent.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is CreateMemoryCardsEvent.DismissNewFilePopup -> {
                    showNewFilePopup = false
                    viewModel.dispatch(
                        CreateMemoryCardsAction.ScanCacheDirectory,
                        context
                    )
                }
                is CreateMemoryCardsEvent.DismissModifyPopup -> {
                    showModifyPopup = false
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, fileSelector, newFilePopup, modifyPopup) = createRefs()

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
                viewModel.dispatch(CreateMemoryCardsAction.ScanCacheDirectory, context)
            },
            jsonFiles = jsonFiles,
            onFileSelect = { fileName ->
                if (fileName == context.getString(R.string.create_new_file)) {
                    showNewFilePopup = true
                } else {
                    modifyFileName = fileName
                    showModifyPopup = true
                }
            },
            isModifyPopupVisible = showModifyPopup
        )

        if (showNewFilePopup) {
            NewFilePopup(
                modifier = Modifier.constrainAs(newFilePopup) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                fileName = fileName,
                onFileNameChange = {
                    fileName = it
                },
                onCancel = {
                    showNewFilePopup = false
                },
                onConfirm = { fileName ->
                    viewModel.dispatch(
                        CreateMemoryCardsAction.CreateNewFile(fileName),
                        context
                    )
                },
                existingFiles = jsonFiles.filter { it.endsWith(CreateMemoryCardsViewModel.JSON_SUFFIX) }
            )
        }

        if (showModifyPopup) {
            ConfirmPopup(
                modifier = Modifier.constrainAs(modifyPopup) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                titleText = stringResource(id = R.string.modify_popup),
                riskyButtonText = stringResource(id = R.string.delete),
                onRiskyButtonClick = {
                    viewModel.dispatch(
                        CreateMemoryCardsAction.DeleteFile(modifyFileName),
                        context
                    )
                },
                safeButtonText = stringResource(id = R.string.modify),
                onSafeButtonClick = {
                    callback?.goModifyScreen(modifyFileName)
                    showModifyPopup = false
                },
                onCloseButtonClick = {
                    showModifyPopup = false
                }
            )
        }
    }
}

@Composable
private fun FileSelector(
    modifier: Modifier,
    onInit: () -> Unit,
    jsonFiles: List<String>,
    onFileSelect: (String) -> Unit,
    isModifyPopupVisible: Boolean
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
                FileSelectorItem(
                    fileName = it,
                    onClick = onFileSelect,
                    isModifyPopupVisible = isModifyPopupVisible
                )
            }
        }
    }
}

/**
 * @param isModifyPopupVisible 当[ModifyPopup]可见时，屏蔽文件列表中文件的点击事件，防止「当前选中的文件名」改变导致删除了其他文件
 */
@Composable
private fun FileSelectorItem(
    fileName: String,
    onClick: (String) -> Unit,
    isModifyPopupVisible: Boolean
) {
    Column(
        modifier = Modifier.clickable {
            if (isModifyPopupVisible.not()) {
                onClick.invoke(fileName)
            }
        }
    ) {
        Row(
            modifier = Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewFilePopup(
    modifier: Modifier,
    fileName: String,
    onFileNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
    existingFiles: List<String>
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .background(
                Color.DarkGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.new_file_popup),
            color = Color.White
        )
        TextField(
            value = fileName,
            onValueChange = {
                onFileNameChange.invoke(it)
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.DarkGray,
                focusedTextColor = if (existingFiles.contains(fileName)) {
                    Color.Red
                } else Color.Green,
                unfocusedTextColor = if (existingFiles.contains(fileName)) {
                    Color.Red
                } else Color.Green
            ),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace
            )
        )
        Row {
            Button(
                onClick = {
                    onCancel.invoke()
                },
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = Color.White
                )
            }

            Button(onClick = {
                onConfirm.invoke(fileName)
            }) {
                Text(
                    text = stringResource(
                        id = if (existingFiles.contains(fileName)) R.string.overwrite else R.string.confirm
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewCreateMemoryCardsScreen() {
    CreateMemoryCardsScreen(defaultShowModifyPopup = true)
}
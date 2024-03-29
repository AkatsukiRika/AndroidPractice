package com.tangping.androidpractice.ui.datastore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground
import com.tangping.androidpractice.ui.theme.gayBackground

private val dataTypeItems = listOf(
    DataStoreDemoViewModel.TYPE_INT,
    DataStoreDemoViewModel.TYPE_LONG,
    DataStoreDemoViewModel.TYPE_BOOLEAN,
    DataStoreDemoViewModel.TYPE_STRING,
    DataStoreDemoViewModel.TYPE_DOUBLE,
    DataStoreDemoViewModel.TYPE_FLOAT
)

interface DataStoreDemoScreenCallback {
    fun onNavigateBack()
}

@Composable
fun DataStoreDemoScreen(
    callback: DataStoreDemoScreenCallback? = null,
    viewModel: DataStoreDemoViewModel = hiltViewModel()
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(darkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SwitchLayout(viewModel)

            Spacer(modifier = Modifier.height(32.dp))

            WriteDataLayout(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(48.dp))

            ReadDataLayout(viewModel = viewModel)
        }

        CloseButton(
            modifier = Modifier.align(Alignment.TopStart),
            callback
        )
    }
}

@Composable
private fun SwitchLayout(viewModel: DataStoreDemoViewModel) {
    val context = LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.preferences),
            color = Color.White
        )

        Switch(
            checked = viewModel.viewStates.mode == DataStoreDemoMode.PROTO,
            onCheckedChange = {
                viewModel.dispatch(context, DataStoreDemoEvent.ChangeMode(
                    mode = if (it) DataStoreDemoMode.PROTO else DataStoreDemoMode.PREFERENCES
                ))
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.proto),
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WriteDataLayout(viewModel: DataStoreDemoViewModel) {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedDataType by rememberSaveable { mutableStateOf(0) }
    var dataKey by rememberSaveable { mutableStateOf("") }
    var dataValue by rememberSaveable { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.write_data),
            style = TextStyle(color = Color.White, fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.key).uppercase(),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.width(70.dp)
            )

            TextField(
                value = dataKey,
                onValueChange = {
                    dataKey = it
                },
                modifier = Modifier
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = gayBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.value).uppercase(),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.width(70.dp)
            )

            TextField(
                value = dataValue,
                onValueChange = {
                    dataValue = it
                },
                modifier = Modifier
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = gayBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    expanded = !expanded
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.width(144.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.type_s).format(dataTypeItems[selectedDataType]),
                    style = TextStyle(color = Color.White)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 42.dp, y = 0.dp),
                modifier = Modifier.width(144.dp)
            ) {
                dataTypeItems.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        text = {
                            Text(text = s)
                        },
                        onClick = {
                            expanded = false
                            selectedDataType = index
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.dispatch(
                        context,
                        DataStoreDemoEvent.WriteData(
                            type = dataTypeItems[selectedDataType],
                            key = dataKey,
                            value = dataValue
                        )
                    )
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.write),
                    style = TextStyle(color = Color.White)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadDataLayout(viewModel: DataStoreDemoViewModel) {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedDataType by rememberSaveable { mutableStateOf(0) }
    var dataKey by rememberSaveable { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.read_data),
            style = TextStyle(color = Color.White, fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.key).uppercase(),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.width(70.dp)
            )

            TextField(
                value = dataKey,
                onValueChange = {
                    dataKey = it
                },
                modifier = Modifier
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = gayBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.value).uppercase(),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.width(70.dp)
            )

            TextField(
                value = viewModel.viewStates.readValue,
                onValueChange = {},
                modifier = Modifier
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = gayBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 1,
                readOnly = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    expanded = !expanded
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.width(144.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.type_s).format(dataTypeItems[selectedDataType]),
                    style = TextStyle(color = Color.White)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 42.dp, y = 0.dp),
                modifier = Modifier.width(144.dp)
            ) {
                dataTypeItems.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        text = {
                            Text(text = s)
                        },
                        onClick = {
                            expanded = false
                            selectedDataType = index
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.dispatch(context, DataStoreDemoEvent.ReadData(
                        type = dataTypeItems[selectedDataType],
                        key = dataKey
                    ))
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.read),
                    style = TextStyle(color = Color.White)
                )
            }
        }
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    callback: DataStoreDemoScreenCallback? = null
) {
    IconButton(
        onClick = {
            callback?.onNavigateBack()
        },
        modifier = modifier
    ) {
        Icon(
            Icons.Sharp.Close,
            contentDescription = "Close Button",
            tint = Color.White
        )
    }
}
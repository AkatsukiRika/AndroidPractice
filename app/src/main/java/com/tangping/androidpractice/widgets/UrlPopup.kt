package com.tangping.androidpractice.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.tangping.androidpractice.R
import com.tangping.androidpractice.ui.theme.darkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlPopup(
    modifier: Modifier,
    url: String,
    onUrlChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onClose: () -> Unit,
    title: String? = null,
    buttonText: String? = null,
    backgroundColor: Color = darkBackground
) {
    ConstraintLayout(
        modifier = modifier
            .wrapContentSize()
            .background(
                backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        val (titleText, textField, btnConfirm, btnClose) = createRefs()

        Text(
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            },
            text = title ?: stringResource(id = R.string.memorize_popup),
            color = Color.White
        )

        TextField(
            modifier = Modifier.constrainAs(textField) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(titleText.bottom)
            }.padding(horizontal = 18.dp),
            value = url,
            onValueChange = {
                onUrlChange.invoke(it)
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                focusedTextColor = Color.Green,
                unfocusedTextColor = Color.Green
            )
        )

        Button(
            modifier = Modifier.constrainAs(btnConfirm) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(textField.bottom)
            },
            onClick = {
                onConfirm.invoke(url)
            }
        ) {
            Text(
                text = buttonText ?: stringResource(id = R.string.use_remote_data),
                color = Color.White
            )
        }

        CloseButton(
            modifier = Modifier.constrainAs(btnClose) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }.size(24.dp),
            onClick = {
                onClose.invoke()
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewUrlPopup() {
    UrlPopup(
        modifier = Modifier,
        url = "http://127.0.0.1/api/get_deck?deck_name=test",
        onUrlChange = {},
        onConfirm = {},
        onClose = {}
    )
}
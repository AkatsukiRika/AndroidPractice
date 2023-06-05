package com.tangping.androidpractice.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.tangping.androidpractice.ui.theme.colorGreen
import com.tangping.androidpractice.ui.theme.colorRed

/**
 * 通用确认弹窗
 * @param riskyButtonText 用于指示有风险的操作，如删除条目、不保存退出页面等，默认为红色按钮，展示在左侧
 * @param safeButtonText 用于指示安全的操作，一般用于关闭当前弹窗，默认为绿色按钮，展示在右侧
 * @param reverseButtons 设置为true后，将绿色按钮和红色按钮的显示位置反转
 */
@Composable
fun ConfirmPopup(
    modifier: Modifier,
    titleText: String,
    riskyButtonText: String,
    onRiskyButtonClick: () -> Unit,
    safeButtonText: String,
    onSafeButtonClick: () -> Unit,
    showCloseButton: Boolean = true,
    onCloseButtonClick: (() -> Unit)? = null,
    reverseButtons: Boolean = false
) {
    ConstraintLayout(
        modifier = modifier
            .wrapContentSize()
            .background(
                Color.DarkGray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        val (title, buttons, btnClose) = createRefs()

        Text(
            text = titleText,
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
                onClick = {
                    if (reverseButtons) {
                        onSafeButtonClick.invoke()
                    } else {
                        onRiskyButtonClick.invoke()
                    }
                },
                modifier = Modifier.padding(end = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (reverseButtons) colorGreen else colorRed)
            ) {
                Text(
                    text = if (reverseButtons) safeButtonText else riskyButtonText,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    if (reverseButtons) {
                        onRiskyButtonClick.invoke()
                    } else {
                        onSafeButtonClick.invoke()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (reverseButtons) colorRed else colorGreen)
            ) {
                Text(
                    text = if (reverseButtons) riskyButtonText else safeButtonText,
                    color = Color.White
                )
            }
        }

        if (showCloseButton) {
            CloseButton(
                modifier = Modifier.constrainAs(btnClose) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                onClick = {
                    onCloseButtonClick?.invoke()
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewConfirmPopup() {
    ConfirmPopup(
        modifier = Modifier,
        titleText = "title",
        riskyButtonText = "risky",
        onRiskyButtonClick = {},
        safeButtonText = "safe",
        onSafeButtonClick = {},
        reverseButtons = true
    )
}
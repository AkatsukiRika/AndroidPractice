package com.tangping.androidpractice.ui.memorize.modify

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.tangping.androidpractice.ui.theme.darkBackground

interface ModifyMemoryCardsCallback {
    fun onNavigateBack()
}

@Composable
fun ModifyMemoryCardsScreen(
    callback: ModifyMemoryCardsCallback? = null,
    fileName: String? = null
) {
    Log.i("ModifyMemoryCardsScreen", "fileName=$fileName")

    ConstraintLayout(
        modifier = Modifier
            .background(darkBackground)
            .fillMaxSize()
    ) {
        val (btnClose, btnDone) = createRefs()

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
fun PreviewModifyMemoryCardsScreen() {
    ModifyMemoryCardsScreen()
}
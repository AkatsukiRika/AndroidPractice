package com.tangping.androidpractice.ui.text

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.tangping.androidpractice.databinding.ActivityAutoSizeTextBinding
import com.tangping.androidpractice.utils.DimensionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoSizeTextActivity : ComponentActivity() {
    private lateinit var binding: ActivityAutoSizeTextBinding
    val viewModel: AutoSizeTextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoSizeTextBinding.inflate(layoutInflater)
        initListeners()
        observeUiState()
        setContentView(binding.root)
        binding.composeView.setContent {
            AutoSizeTextCompose()
        }
    }

    private fun initListeners() {
        binding.apply {
            btnHeightMinus.setOnClickListener {
                viewModel.adjustHeight(true)
            }
            btnHeightPlus.setOnClickListener {
                viewModel.adjustHeight(false)
            }
            btnLinesMinus.setOnClickListener {
                viewModel.adjustLines(true)
            }
            btnLinesPlus.setOnClickListener {
                viewModel.adjustLines(false)
            }
            btnWidthMinus.setOnClickListener {
                viewModel.adjustWidth(true)
            }
            btnWidthPlus.setOnClickListener {
                viewModel.adjustWidth(false)
            }
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) {
            binding.apply {
                tvHeight.text = "${it.fixedHeightDp} dp"
                btnHeightMinus.isEnabled = it.fixedHeightDp > 0
                setFixedHeightUi(it.fixedHeightDp)

                tvLines.text = "${it.fixedLines}"
                btnLinesMinus.isEnabled = it.fixedLines > 1
                tvLimitLines.maxLines = it.fixedLines

                tvWidth.text = "${it.fixedWidth} dp"
                btnWidthMinus.isEnabled = it.fixedWidth > 0
                setFixedWidthUi(it.fixedWidth)
            }
        }
    }

    private fun setFixedHeightUi(dpValue: Int) {
        binding.apply {
            val lp = tvLimitHeight.layoutParams
            lp?.height = DimensionUtils.dp2px(this@AutoSizeTextActivity, dpValue.toFloat())
            tvLimitHeight.layoutParams = lp
        }
    }

    private fun setFixedWidthUi(dpValue: Int) {
        binding.apply {
            val lp = tvLimitWidth.layoutParams
            lp?.width = DimensionUtils.dp2px(this@AutoSizeTextActivity, dpValue.toFloat())
            tvLimitWidth.layoutParams = lp
        }
    }
}
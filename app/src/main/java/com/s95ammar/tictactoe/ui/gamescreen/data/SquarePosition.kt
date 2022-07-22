package com.s95ammar.tictactoe.ui.gamescreen.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SquarePosition(val row: Int, val column: Int) : Parcelable

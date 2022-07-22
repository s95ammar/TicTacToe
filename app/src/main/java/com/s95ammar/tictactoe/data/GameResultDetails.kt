package com.s95ammar.tictactoe.data

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameResultDetails(
    val isGameOver: Boolean,
    @Nullable
    val winner: TicTacToePlayer? = null
) : Parcelable

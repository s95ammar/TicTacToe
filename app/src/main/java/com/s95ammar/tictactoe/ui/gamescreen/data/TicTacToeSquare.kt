package com.s95ammar.tictactoe.ui.gamescreen.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class TicTacToeSquare : Parcelable {

    @Parcelize
    object Empty : TicTacToeSquare()

    @Parcelize
    object X : TicTacToeSquare()

    @Parcelize
    object O : TicTacToeSquare()
}

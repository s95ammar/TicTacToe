package com.s95ammar.tictactoe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class TicTacToePlayer(val name: String): Parcelable {

    @Parcelize
    class X : TicTacToePlayer(DEFAULT_NAME) {
        companion object {
            const val DEFAULT_NAME = "Player 1"
        }
    }

    @Parcelize
    class O : TicTacToePlayer(DEFAULT_NAME) {
        companion object {
            const val DEFAULT_NAME = "Player 2"
        }
    }
}

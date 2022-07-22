package com.s95ammar.tictactoe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

typealias TicTacToeBoard = List<List<TicTacToeSquare>>

sealed class TicTacToeSquare(open val id: UUID = UUID.randomUUID()) : Parcelable {

    @Parcelize
    class Empty : TicTacToeSquare()

    @Parcelize
    class X : TicTacToeSquare()

    @Parcelize
    class O : TicTacToeSquare()
}

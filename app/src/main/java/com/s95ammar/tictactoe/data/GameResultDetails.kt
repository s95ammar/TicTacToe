package com.s95ammar.tictactoe.data

data class GameResultDetails(
    val winner: String? = null
) {
    val isGameOver
        get() = !winner.isNullOrEmpty()
}

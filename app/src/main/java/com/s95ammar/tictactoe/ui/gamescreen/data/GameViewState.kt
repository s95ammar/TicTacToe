package com.s95ammar.tictactoe.ui.gamescreen.data

import com.s95ammar.tictactoe.util.TicTacToeSquares

data class GameViewState(
    val currentPlayer: TicTacToePlayer,
    val board: TicTacToeSquares,
    val gameResultDetails: GameResultDetails
)

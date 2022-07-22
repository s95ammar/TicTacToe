package com.s95ammar.tictactoe.data

data class GameViewState(
    val currentPlayer: TicTacToePlayer,
    val board: TicTacToeSquares,
    val gameResultDetails: GameResultDetails
)

package com.s95ammar.tictactoe.data

data class GameViewState(
    val currentPlayer: TicTacToePlayer,
    val board: List<List<TicTacToeSquare>>,
//    val gameResultDetails: GameResultDetails = GameResultDetails()
)
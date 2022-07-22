package com.s95ammar.tictactoe.ui.gamescreen.data

sealed class GameUiEvent {
    data class ShowGameEndDialog(val gameResultDetails: GameResultDetails): GameUiEvent()
    // other events would go here
}

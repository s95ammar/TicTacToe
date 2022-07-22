package com.s95ammar.tictactoe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s95ammar.tictactoe.data.GameViewState
import com.s95ammar.tictactoe.data.TicTacToeBoard
import com.s95ammar.tictactoe.data.TicTacToePlayer
import com.s95ammar.tictactoe.data.TicTacToeSquare
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val ROWS_IN_COLUMN = 3
        private const val SQUARES_IN_ROW = 3
        private const val SQUARES_REQUIRED_FOR_WIN = 3
        private const val KEY_IS_GAME_IN_PROGRESS = "KEY_IS_GAME_IN_PROGRESS"
        private const val KEY_CURRENT_PLAYER_TURN = "KEY_CURRENT_PLAYER_TURN"
        private const val KEY_BOARD_VALUE = "KEY_BOARD_VALUE"
        private const val KEY_PLAYER_X = "KEY_PLAYER_X"
        private const val KEY_PLAYER_O = "KEY_PLAYER_O"
    }

    private val playerX = TicTacToePlayer.X().also {
        savedStateHandle.set<TicTacToePlayer>(KEY_PLAYER_X, it)
    }
    private val playerO = TicTacToePlayer.O().also {
        savedStateHandle.set<TicTacToePlayer>(KEY_PLAYER_O, it)
    }
    private val isGameInProgress = savedStateHandle.getStateFlow(KEY_IS_GAME_IN_PROGRESS, initialValue = true)
    private val currentPlayerTurn = savedStateHandle.getStateFlow<TicTacToePlayer>(KEY_CURRENT_PLAYER_TURN, playerX)
    private val board = savedStateHandle.getStateFlow(KEY_BOARD_VALUE, generateBlankBoard())

//    private val _uiEventFlow = MutableSharedFlow<>()

    val gameViewState = combine(
        currentPlayerTurn,
        board
    ) { currentPlayerTurn, squares ->
        GameViewState(
            currentPlayerTurn,
            squares
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onSquareClick(clickedSquare: TicTacToeSquare) {
        if (clickedSquare is TicTacToeSquare.Empty) {
            savedStateHandle[KEY_BOARD_VALUE] = getUpdatedBoard(clickedSquare)
            when (currentPlayerTurn.value) {
                is TicTacToePlayer.X -> savedStateHandle[KEY_CURRENT_PLAYER_TURN] = playerO
                is TicTacToePlayer.O -> savedStateHandle[KEY_CURRENT_PLAYER_TURN] = playerX
            }
        }
    }

    private fun getUpdatedBoard(clickedEmptySquare: TicTacToeSquare): TicTacToeBoard {
        return board.value.map { row ->
            row.map { rowSquare ->
                if (clickedEmptySquare.id == rowSquare.id) {
                    val newSquare = when (currentPlayerTurn.value) {
                        is TicTacToePlayer.X -> TicTacToeSquare.X()
                        is TicTacToePlayer.O -> TicTacToeSquare.O()
                    }
                    newSquare
                } else {
                    rowSquare
                }
            }
        }
    }

    private fun generateBlankBoard(): TicTacToeBoard = buildList {
        repeat(ROWS_IN_COLUMN) {
            add(
                mutableListOf<TicTacToeSquare>().also { row ->
                    repeat(SQUARES_IN_ROW) {
                        row.add(TicTacToeSquare.Empty())
                    }
                }
            )
        }
    }

}
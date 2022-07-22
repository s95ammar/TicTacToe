package com.s95ammar.tictactoe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s95ammar.tictactoe.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SQUARES_IN_A_SIDE = 3
        private const val KEY_GAME_RESULT_DETAILS = "KEY_GAME_RESULT_DETAILS"
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
    private val gameResultDetails = savedStateHandle.getStateFlow(
        KEY_GAME_RESULT_DETAILS,
        GameResultDetails(isGameOver = false)
    )
    private val currentPlayer = savedStateHandle.getStateFlow<TicTacToePlayer>(KEY_CURRENT_PLAYER_TURN, playerX)
    private val board = savedStateHandle.getStateFlow(KEY_BOARD_VALUE, generateEmptyBoard())

    private val _uiEventFlow = MutableSharedFlow<GameUiEvent>()

    val gameViewState = combine(
        currentPlayer,
        board,
        gameResultDetails
    ) { currentPlayerTurn, squares, gameResultDetails ->
        GameViewState(
            currentPlayerTurn,
            squares,
            gameResultDetails
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val uiEventFlow = _uiEventFlow.asSharedFlow()

    fun onSquareClick(
        clickedSquarePosition: SquarePosition,
        clickedSquare: TicTacToeSquare
    ) = viewModelScope.launch {
        if (gameResultDetails.value.isGameOver) {
            _uiEventFlow.emit(GameUiEvent.ShowGameEndDialog(gameResultDetails.value))
            return@launch
        }
        if (clickedSquare is TicTacToeSquare.Empty) {
            savedStateHandle[KEY_BOARD_VALUE] = getUpdatedBoard(clickedSquarePosition)
            when {
                isGameWon(clickedSquarePosition) -> {
                    savedStateHandle[KEY_GAME_RESULT_DETAILS] = GameResultDetails(isGameOver = true, winner = currentPlayer.value)
                    _uiEventFlow.emit(GameUiEvent.ShowGameEndDialog(gameResultDetails.value))
                }
                board.value.none { it.value is TicTacToeSquare.Empty } -> {
                    savedStateHandle[KEY_GAME_RESULT_DETAILS] = GameResultDetails(isGameOver = true, winner = null)
                    _uiEventFlow.emit(GameUiEvent.ShowGameEndDialog(gameResultDetails.value))
                }
                else -> {
                    switchTurns()
                }
            }
        }
    }

    fun restart() {
        savedStateHandle[KEY_GAME_RESULT_DETAILS] = GameResultDetails(isGameOver = false, winner = null)
        savedStateHandle[KEY_CURRENT_PLAYER_TURN] = playerX
        savedStateHandle[KEY_BOARD_VALUE] = generateEmptyBoard()
    }

    private fun getUpdatedBoard(clickedSquarePosition: SquarePosition): TicTacToeSquares {
        return board.value.toMutableMap().apply {
            val newSquare = when (currentPlayer.value) {
                is TicTacToePlayer.X -> TicTacToeSquare.X()
                is TicTacToePlayer.O -> TicTacToeSquare.O()
            }
            put(clickedSquarePosition, newSquare)
        }
    }

    private fun switchTurns() {
        when (currentPlayer.value) {
            is TicTacToePlayer.X -> savedStateHandle[KEY_CURRENT_PLAYER_TURN] = playerO
            is TicTacToePlayer.O -> savedStateHandle[KEY_CURRENT_PLAYER_TURN] = playerX
        }
    }

    private fun generateEmptyBoard(): TicTacToeSquares = buildMap {
        repeat(SQUARES_IN_A_SIDE) { rowNumber ->
            repeat(SQUARES_IN_A_SIDE) { columnNumber ->
                put(SquarePosition(rowNumber, columnNumber), TicTacToeSquare.Empty())
            }
        }
    }

    private fun isGameWon(clickedSquarePosition: SquarePosition): Boolean {

        val isHorizontalWin = isWin(board.value.filter { it.key.row == clickedSquarePosition.row })
        if (isHorizontalWin) return true

        val isVerticalWin = isWin(board.value.filter { it.key.column == clickedSquarePosition.column })
        if (isVerticalWin) return true

        val isDiagonalWin1 = isWin(board.value.filter { it.key.row == it.key.column })
        if (isDiagonalWin1) return true

        val isDiagonalWin2 = isWin(board.value.filter {
            val lastIndex = SQUARES_IN_A_SIDE - 1
            it.key.row == lastIndex - it.key.column
        })
        if (isDiagonalWin2) return true

        return false
    }

    private fun isWin(winningSquares: TicTacToeSquares): Boolean {
        return when (currentPlayer.value) {
            is TicTacToePlayer.X -> winningSquares.all { it.value is TicTacToeSquare.X }
            is TicTacToePlayer.O -> winningSquares.all { it.value is TicTacToeSquare.O }
        }
    }

}
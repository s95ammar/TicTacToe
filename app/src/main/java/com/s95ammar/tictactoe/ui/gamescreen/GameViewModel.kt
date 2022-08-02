package com.s95ammar.tictactoe.ui.gamescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s95ammar.tictactoe.ui.gamescreen.data.*
import com.s95ammar.tictactoe.util.SQUARES_IN_A_SIDE
import com.s95ammar.tictactoe.util.TicTacToeSquares
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_PLAYER_X = "KEY_PLAYER_X"
        private const val KEY_PLAYER_O = "KEY_PLAYER_O"
        private const val KEY_GAME_RESULT_DETAILS = "KEY_GAME_RESULT_DETAILS"
        private const val KEY_CURRENT_PLAYER_TURN = "KEY_CURRENT_PLAYER_TURN"
        private const val KEY_BOARD_VALUE = "KEY_BOARD_VALUE"
    }

    private val playerX
        get() = savedStateHandle.playerX ?: TicTacToePlayer.X().also {
            savedStateHandle.set<TicTacToePlayer>(KEY_PLAYER_X, it)
        }
    private val playerO
        get() = savedStateHandle.playerO ?: TicTacToePlayer.O().also {
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
            savedStateHandle.boardValue = getUpdatedBoard(clickedSquarePosition)
            when {
                isGameWon(clickedSquarePosition) -> {
                    savedStateHandle.gameResultDetails = GameResultDetails(isGameOver = true, winner = currentPlayer.value)
                    _uiEventFlow.emit(GameUiEvent.ShowGameEndDialog(gameResultDetails.value))
                }
                board.value.none { it.value is TicTacToeSquare.Empty } -> {
                    savedStateHandle.gameResultDetails = GameResultDetails(isGameOver = true, winner = null)
                    _uiEventFlow.emit(GameUiEvent.ShowGameEndDialog(gameResultDetails.value))
                }
                else -> {
                    switchTurns()
                }
            }
        }
    }

    fun restart() {
        savedStateHandle.gameResultDetails = GameResultDetails(isGameOver = false, winner = null)
        savedStateHandle.currentPlayerTurn = playerX
        savedStateHandle.boardValue = generateEmptyBoard()
    }

    private fun getUpdatedBoard(clickedSquarePosition: SquarePosition): TicTacToeSquares {
        return board.value.toMutableMap().apply {
            val newSquare = when (currentPlayer.value) {
                is TicTacToePlayer.X -> TicTacToeSquare.X
                is TicTacToePlayer.O -> TicTacToeSquare.O
            }
            put(clickedSquarePosition, newSquare)
        }
    }

    private fun switchTurns() {
        when (currentPlayer.value) {
            is TicTacToePlayer.X -> savedStateHandle.currentPlayerTurn = playerO
            is TicTacToePlayer.O -> savedStateHandle.currentPlayerTurn = playerX
        }
    }

    private fun generateEmptyBoard(): TicTacToeSquares = buildMap {
        repeat(SQUARES_IN_A_SIDE) { rowNumber ->
            repeat(SQUARES_IN_A_SIDE) { columnNumber ->
                put(SquarePosition(rowNumber, columnNumber), TicTacToeSquare.Empty)
            }
        }
    }

    private fun isGameWon(clickedSquarePosition: SquarePosition): Boolean {

        val isHorizontalWin = isWin(board.value.filter { it.key.row == clickedSquarePosition.row })
        if (isHorizontalWin) return true

        val isVerticalWin = isWin(board.value.filter { it.key.column == clickedSquarePosition.column })
        if (isVerticalWin) return true

        val isDiagonalWin1 = isSquareOnDiagonal1(clickedSquarePosition) && isWin(board.value.filter { isSquareOnDiagonal1(it.key) })
        if (isDiagonalWin1) return true

        val isDiagonalWin2 = isSquareOnDiagonal2(clickedSquarePosition) && isWin(board.value.filter { isSquareOnDiagonal2(it.key) })
        if (isDiagonalWin2) return true

        return false
    }

    private fun isSquareOnDiagonal1(squarePosition: SquarePosition): Boolean {
        return squarePosition.row == squarePosition.column
    }

    private fun isSquareOnDiagonal2(squarePosition: SquarePosition): Boolean {
        val lastSquareIndex = SQUARES_IN_A_SIDE - 1
        return squarePosition.row == lastSquareIndex - squarePosition.column
    }

    private fun isWin(squaresToWin: TicTacToeSquares): Boolean {
        return when (currentPlayer.value) {
            is TicTacToePlayer.X -> squaresToWin.all { it.value is TicTacToeSquare.X }
            is TicTacToePlayer.O -> squaresToWin.all { it.value is TicTacToeSquare.O }
        }
    }

    private var SavedStateHandle.playerX: TicTacToePlayer?
        get() = savedStateHandle[KEY_PLAYER_X]
        set(value) {
            savedStateHandle[KEY_PLAYER_X] = value
        }

    private var SavedStateHandle.playerO: TicTacToePlayer?
        get() = savedStateHandle[KEY_PLAYER_O]
        set(value) {
            savedStateHandle[KEY_PLAYER_O] = value
        }

    private var SavedStateHandle.gameResultDetails: GameResultDetails?
        get() = savedStateHandle[KEY_GAME_RESULT_DETAILS]
        set(value) {
            savedStateHandle[KEY_GAME_RESULT_DETAILS] = value
        }

    private var SavedStateHandle.currentPlayerTurn: TicTacToePlayer?
        get() = savedStateHandle[KEY_CURRENT_PLAYER_TURN]
        set(value) {
            savedStateHandle[KEY_CURRENT_PLAYER_TURN] = value
        }

    private var SavedStateHandle.boardValue: TicTacToeSquares?
        get() = savedStateHandle[KEY_BOARD_VALUE]
        set(value) {
            savedStateHandle[KEY_BOARD_VALUE] = value
        }

}
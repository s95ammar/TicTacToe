package com.s95ammar.tictactoe.data

import com.s95ammar.tictactoe.R

sealed class TicTacToeViewType(val viewType: Int) {
    data class CurrentPlayer(val item : TicTacToePlayer) : TicTacToeViewType(VIEW_TYPE) {
        companion object {
            const val VIEW_TYPE = R.layout.item_current_player_turn
        }
    }
    data class Square(val position: SquarePosition, val item : TicTacToeSquare) : TicTacToeViewType(VIEW_TYPE) {
        companion object {
            const val VIEW_TYPE = R.layout.item_square
        }
    }
}

package com.s95ammar.tictactoe.ui.gamescreen.adapter

import com.s95ammar.tictactoe.R
import com.s95ammar.tictactoe.ui.gamescreen.data.SquarePosition
import com.s95ammar.tictactoe.ui.gamescreen.data.TicTacToePlayer
import com.s95ammar.tictactoe.ui.gamescreen.data.TicTacToeSquare

sealed class GameViewType(val viewType: Int) {
    data class CurrentPlayer(val value : TicTacToePlayer) : GameViewType(VIEW_TYPE) {
        companion object {
            const val VIEW_TYPE = R.layout.item_current_player_turn
        }
    }
    data class Square(val position: SquarePosition, val value : TicTacToeSquare) : GameViewType(VIEW_TYPE) {
        companion object {
            const val VIEW_TYPE = R.layout.item_square
        }
    }
}

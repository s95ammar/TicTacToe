package com.s95ammar.tictactoe.ui.gamescreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.s95ammar.tictactoe.R
import com.s95ammar.tictactoe.databinding.EmptyLayoutBinding
import com.s95ammar.tictactoe.databinding.ItemCurrentPlayerTurnBinding
import com.s95ammar.tictactoe.databinding.ItemSquareBinding
import com.s95ammar.tictactoe.ui.gamescreen.data.SquarePosition
import com.s95ammar.tictactoe.ui.gamescreen.data.TicTacToePlayer
import com.s95ammar.tictactoe.ui.gamescreen.data.TicTacToeSquare

class GameAdapter(
    private val onSquareClick: (SquarePosition, TicTacToeSquare) -> Unit
) : ListAdapter<GameViewType, RecyclerView.ViewHolder>(TicTacToeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GameViewType.CurrentPlayer.VIEW_TYPE -> CurrentPlayerViewHolder(
                ItemCurrentPlayerTurnBinding.inflate(inflater, parent, false)
            )
            GameViewType.Square.VIEW_TYPE -> SquareViewHolder(
                ItemSquareBinding.inflate(inflater, parent, false),
                onSquareClick
            )
            else -> EmptyViewHolder(
                EmptyLayoutBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is CurrentPlayerViewHolder -> {
                    (it as? GameViewType.CurrentPlayer)?.value?.let { item -> holder.bind(item) }
                }
                is SquareViewHolder -> {
                    (it as? GameViewType.Square)?.let { square -> holder.bind(square.position, square.value) }
                }
                else -> {}
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    class CurrentPlayerViewHolder(private val binding: ItemCurrentPlayerTurnBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TicTacToePlayer) {
            binding.playerTurnTextView.text = itemView.resources.getString(R.string.format_player_turn, item.name)
        }
    }

    class SquareViewHolder(
        private val binding: ItemSquareBinding,
        val onSquareClick: (SquarePosition, TicTacToeSquare) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: SquarePosition, item: TicTacToeSquare) {
            itemView.setOnClickListener { onSquareClick(position, item) }
            binding.squareValueTextView.text = when (item) {
                is TicTacToeSquare.Empty -> null
                is TicTacToeSquare.X -> itemView.resources.getString(R.string.square_value_x)
                is TicTacToeSquare.O -> itemView.resources.getString(R.string.square_value_O)
            }
        }
    }

    class EmptyViewHolder(private val binding: EmptyLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class TicTacToeDiffUtil : DiffUtil.ItemCallback<GameViewType>() {
        override fun areItemsTheSame(oldItem: GameViewType, newItem: GameViewType): Boolean {
            return when (oldItem) {
                is GameViewType.CurrentPlayer -> newItem is GameViewType.CurrentPlayer
                is GameViewType.Square -> oldItem.position == (newItem as? GameViewType.Square)?.position
            }
        }

        override fun areContentsTheSame(oldItem: GameViewType, newItem: GameViewType): Boolean {
            return when (oldItem) {
                is GameViewType.CurrentPlayer -> {
                    newItem is GameViewType.CurrentPlayer && oldItem.value == newItem.value
                }
                is GameViewType.Square -> {
                    newItem is GameViewType.Square && oldItem.value == newItem.value
                }
            }
        }
    }
}
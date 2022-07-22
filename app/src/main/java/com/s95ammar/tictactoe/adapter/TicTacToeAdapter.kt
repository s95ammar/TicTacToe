package com.s95ammar.tictactoe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.s95ammar.tictactoe.R
import com.s95ammar.tictactoe.data.TicTacToePlayer
import com.s95ammar.tictactoe.data.TicTacToeSquare
import com.s95ammar.tictactoe.data.TicTacToeViewType
import com.s95ammar.tictactoe.databinding.EmptyLayoutBinding
import com.s95ammar.tictactoe.databinding.ItemCurrentPlayerTurnBinding
import com.s95ammar.tictactoe.databinding.ItemSquareBinding

class TicTacToeAdapter(
    private val onSquareClick: (TicTacToeSquare) -> Unit
) : ListAdapter<TicTacToeViewType, RecyclerView.ViewHolder>(TicTacToeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TicTacToeViewType.CurrentPlayer.VIEW_TYPE -> CurrentPlayerViewHolder(
                ItemCurrentPlayerTurnBinding.inflate(inflater, parent, false)
            )
            TicTacToeViewType.Square.VIEW_TYPE -> SquareViewHolder(
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
                    (it as? TicTacToeViewType.CurrentPlayer)?.item?.let { item -> holder.bind(item) }
                }
                is SquareViewHolder -> {
                    (it as? TicTacToeViewType.Square)?.item?.let { item -> holder.bind(item) }
                }
                else -> {}
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TicTacToeViewType.CurrentPlayer -> TicTacToeViewType.CurrentPlayer.VIEW_TYPE
            is TicTacToeViewType.Square -> TicTacToeViewType.Square.VIEW_TYPE
        }
    }

    class CurrentPlayerViewHolder(private val binding: ItemCurrentPlayerTurnBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TicTacToePlayer) {
            binding.playerTurnTextView.text = itemView.resources.getString(R.string.format_player_turn, item.name)
        }
    }

    class SquareViewHolder(
        private val binding: ItemSquareBinding,
        val onSquareClick: (TicTacToeSquare) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TicTacToeSquare) {
            itemView.setOnClickListener { onSquareClick(item) }
            binding.squareValueTextView.text =
                when (item) {
                    is TicTacToeSquare.Empty -> null
                    is TicTacToeSquare.X -> itemView.resources.getString(R.string.square_value_x)
                    is TicTacToeSquare.O -> itemView.resources.getString(R.string.square_value_O)
                }
        }
    }

    class EmptyViewHolder(private val binding: EmptyLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class TicTacToeDiffUtil : DiffUtil.ItemCallback<TicTacToeViewType>() {
        override fun areItemsTheSame(oldItem: TicTacToeViewType, newItem: TicTacToeViewType): Boolean {
            return when (oldItem) {
                is TicTacToeViewType.CurrentPlayer -> newItem is TicTacToeViewType.CurrentPlayer
                is TicTacToeViewType.Square -> oldItem.item.id == (newItem as? TicTacToeViewType.Square)?.item?.id
            }
        }

        override fun areContentsTheSame(oldItem: TicTacToeViewType, newItem: TicTacToeViewType): Boolean {
            return when (oldItem) {
                is TicTacToeViewType.CurrentPlayer -> {
                    newItem is TicTacToeViewType.CurrentPlayer && oldItem.item::class == newItem.item::class
                }
                is TicTacToeViewType.Square -> {
                    newItem is TicTacToeViewType.Square && oldItem.item::class == newItem.item::class
                }
            }
        }
    }
}
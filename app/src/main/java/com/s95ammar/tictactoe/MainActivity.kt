package com.s95ammar.tictactoe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.s95ammar.tictactoe.adapter.TicTacToeAdapter
import com.s95ammar.tictactoe.data.TicTacToeBoard
import com.s95ammar.tictactoe.data.TicTacToePlayer
import com.s95ammar.tictactoe.data.TicTacToeViewType
import com.s95ammar.tictactoe.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { TicTacToeAdapter(viewModel::onSquareClick) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gameRecyclerView.adapter = adapter
        binding.gameRecyclerView.itemAnimator = null
        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    TicTacToeViewType.CurrentPlayer.VIEW_TYPE -> 3
                    else -> 1
                }
            }
        }
        binding.gameRecyclerView.layoutManager = layoutManager

        // TODO: add flowOnLifecycle
        lifecycleScope.launch {
            viewModel.gameViewState.collect { gameViewState ->
                gameViewState?.let {
                    submitList(getTicTacToeAdapterList(gameViewState.currentPlayer, gameViewState.board))
                }
            }
        }
    }

    private fun submitList(list: List<TicTacToeViewType>) {
        adapter.submitList(list)
    }

    private fun getTicTacToeAdapterList(playerTurn: TicTacToePlayer, board: TicTacToeBoard): List<TicTacToeViewType> {
        return buildList {
            add(TicTacToeViewType.CurrentPlayer(playerTurn))
            addAll(board.flatten().map { TicTacToeViewType.Square(it) })
        }
    }

}
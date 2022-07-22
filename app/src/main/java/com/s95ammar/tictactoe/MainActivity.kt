package com.s95ammar.tictactoe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.s95ammar.tictactoe.adapter.TicTacToeAdapter
import com.s95ammar.tictactoe.data.*
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
                    TicTacToeViewType.Square.VIEW_TYPE -> 1
                    else -> 3
                }
            }
        }
        binding.gameRecyclerView.layoutManager = layoutManager

        lifecycleScope.launch {
            viewModel.gameViewState.flowWithLifecycle(lifecycle).collect { gameViewState ->
                gameViewState?.let {
                    adapter.submitList(getTicTacToeAdapterList(gameViewState.currentPlayer, gameViewState.board))
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uiEventFlow.flowWithLifecycle(lifecycle).collect { event ->
                handleUiEvent(event)
            }
        }
    }

    private fun handleUiEvent(uiEvent: GameUiEvent) {
        when (uiEvent) {
            is GameUiEvent.ShowGameEndDialog -> showWinningPlayerDialog(uiEvent.gameResultDetails)
            // other events would be handled here
        }
    }

    private fun getTicTacToeAdapterList(playerTurn: TicTacToePlayer, board: TicTacToeSquares): List<TicTacToeViewType> {
        return buildList {
            add(TicTacToeViewType.CurrentPlayer(playerTurn))
            addAll(
                board.toList().map { (position, square) -> TicTacToeViewType.Square(position, square) }
                    .sortedWith(compareBy({it.position.row}, {it.position.column}))
            )
        }
    }

    private fun showWinningPlayerDialog(gameResultDetails: GameResultDetails) {
        val title = getString(gameResultDetails.winner?.let { R.string.player_won_title } ?: R.string.draw_title)
        val message = if (gameResultDetails.winner != null)
            getString(R.string.format_player_won_message, gameResultDetails.winner.name)
        else
            getString(R.string.draw_message)

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.restart) { _, _ -> viewModel.restart() }
            .setNegativeButton(R.string.cancel) { _, _, -> }
            .show()
    }

}
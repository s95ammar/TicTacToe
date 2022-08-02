package com.s95ammar.tictactoe.ui.gamescreen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.s95ammar.tictactoe.R
import com.s95ammar.tictactoe.databinding.ActivityMainBinding
import com.s95ammar.tictactoe.ui.gamescreen.adapter.GameAdapter
import com.s95ammar.tictactoe.ui.gamescreen.adapter.GameViewType
import com.s95ammar.tictactoe.ui.gamescreen.data.GameResultDetails
import com.s95ammar.tictactoe.ui.gamescreen.data.GameUiEvent
import com.s95ammar.tictactoe.ui.gamescreen.data.TicTacToePlayer
import com.s95ammar.tictactoe.util.SQUARES_IN_A_SIDE
import com.s95ammar.tictactoe.util.TicTacToeSquares
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    private val adapter by lazy { GameAdapter(viewModel::onSquareClick) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()

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

    private fun setUpRecyclerView() {
        binding.gameRecyclerView.adapter = adapter
        binding.gameRecyclerView.itemAnimator = null
        val layoutManager = GridLayoutManager(this, SQUARES_IN_A_SIDE)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    GameViewType.Square.VIEW_TYPE -> 1
                    else -> SQUARES_IN_A_SIDE
                }
            }
        }
        binding.gameRecyclerView.layoutManager = layoutManager
    }

    private fun handleUiEvent(uiEvent: GameUiEvent) {
        when (uiEvent) {
            is GameUiEvent.ShowGameEndDialog -> showGameEndDialog(uiEvent.gameResultDetails)
            // other events would be handled here
        }
    }

    private fun getTicTacToeAdapterList(playerTurn: TicTacToePlayer, board: TicTacToeSquares): List<GameViewType> {
        return buildList {
            add(GameViewType.CurrentPlayer(playerTurn))
            addAll(
                board.toList().map { (position, square) -> GameViewType.Square(position, square) }
                    .sortedWith(compareBy({it.position.row}, {it.position.column}))
            )
        }
    }

    private fun showGameEndDialog(gameResultDetails: GameResultDetails) {
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
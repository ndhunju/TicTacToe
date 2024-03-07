package com.ndhunju.tictactoe

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ndhunju.tictactoe.ui.theme.Purple40
import com.ndhunju.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityWithCompose: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                MainContent(
                    GameLogic.uiState.asStateFlow(),
                    onClickGridCell = { row, col ->
                        GameLogic.makeMoveIfValid(row, col, GameLogic.currentPlayer)
                    },
                    onClickPlayAgain = { GameLogic.playAgain() },
                    onClickReset = { GameLogic.reset() }
                )
            }
        }
    }
}

object GameLogic {

    // Member Variables
    private var playerX = "X"
    private var playerO = "O"
    var currentPlayer = playerX
    private var playerXWins = 0
    private var playerOWins = 0
    val uiState = MutableStateFlow(UiState())

    /**
     * Makes the move if it is a valid move
     */
    fun makeMoveIfValid(row: Int, col: Int, forPlayer: String) {
        if (isValidMove(row, col)) {
            makeMove(row, col, forPlayer)
            checkForWinnerOrTie()
            toggleCurrentPlayer()
        }
    }

    private fun toggleCurrentPlayer() {
        currentPlayer = if (currentPlayer == playerX) {
            playerO
        } else {
            playerX
        }
        updatePlayerTurnText()
    }

    private fun updatePlayerTurnText() {
        uiState.value = uiState.value.copy(playerTurnText = "$currentPlayer Turn")
    }

    private fun updateScoreTexts() {
        uiState.value = uiState.value.copy(
            playerXScore = "$playerX - $playerXWins wins",
            playerOScore = "$playerO - $playerOWins wins"
        )
    }

    private fun isValidMove(row: Int, col: Int): Boolean {
        // Check if there is already a mark in current view
        return uiState.value.gridValues[row][col].isNullOrEmpty()
    }

    private fun makeMove(row: Int, col: Int, forPlayer: String) {
        uiState.value.gridValues[row][col] = forPlayer
//        textView.text = SpannableString(currentPlayer).apply {
//            // Use white color text for O and black for x
//            setSpan(
//                ForegroundColorSpan(if (currentPlayer == playerO) Color.WHITE else Color.BLACK),
//                0,
//                currentPlayer.length,
//                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
//            )
//        }
    }

    private fun checkForWinnerOrTie() {
        val winner = getWinnerIfExist()
        if (winner != null) {
            if (winner == playerX) playerXWins++
            if (winner == playerO) playerOWins++
            updateScoreTexts()
            setGameOverText("Player $winner won! 🎉")
        } else if (isTie()) {
            setGameOverText("It is a tie!")
        }
    }

    /**
     * Displays alert dialog with [title] and a reset button
     */
    private fun setGameOverText(title: String) {
        uiState.value = uiState.value.copy(gameOverText = title)
    }

    fun playAgain() {
        // Reset all texts
        uiState.value = UiState()
    }

    fun reset() {
        playAgain()
        playerXWins = 0
        playerOWins = 0
        updateScoreTexts()
    }

    /**
     * Returns winner's value if there is one. Otherwise, null
     */
    private fun getWinnerIfExist(): String? {
        // Check if the same value are present in any column
        val col1 = arrayOf(
            uiState.value.gridValues[0][0],
            uiState.value.gridValues[1][0],
            uiState.value.gridValues[2][0],
        )

        if (hasSameNonEmptyValues(col1)) {
            return col1[0]
        }

        val col2 = arrayOf(
            uiState.value.gridValues[0][1],
            uiState.value.gridValues[1][1],
            uiState.value.gridValues[2][1],
        )

        if (hasSameNonEmptyValues(col2)) {
            return col2[0]
        }

        val col3 = arrayOf(
            uiState.value.gridValues[0][2],
            uiState.value.gridValues[1][2],
            uiState.value.gridValues[2][2],
        )

        if (hasSameNonEmptyValues(col1)) {
            return col3[0]
        }

        // Check if the same value are present in any row
        uiState.value.gridValues.forEach { row ->
            if (hasSameNonEmptyValues(cells = row)) {
                return row[0]
            }
        }

        // Check if the same value are present in any diagonal
        val diagonal1 = arrayOf(
            uiState.value.gridValues[0][0],
            uiState.value.gridValues[1][1],
            uiState.value.gridValues[2][2],
        )

        if (hasSameNonEmptyValues(diagonal1)) {
            return diagonal1[0]
        }

        val diagonal2 = arrayOf(
            uiState.value.gridValues[0][2],
            uiState.value.gridValues[1][1],
            uiState.value.gridValues[2][0],
        )

        if (hasSameNonEmptyValues(diagonal2)) {
            return diagonal2[0]
        }

        return null

    }

    private fun isTie(): Boolean {
        // Check if all the boxes are filled
        uiState.value.gridValues.forEach { row ->
            row.forEach { cell ->
                if (cell.isNullOrEmpty()) return false
            }
        }

        return true
    }

    /**
     * Returns true if all the [TextView] in [textViews] have same text
     */
    private fun hasSameNonEmptyValues(cells: Array<String?>): Boolean {
        val firstValue = cells.firstOrNull() ?: return false
        if (firstValue.isEmpty()) return false
        cells.forEach { cell ->
            if ((cell == firstValue).not()) {
                return false
            }
        }

        return true
    }

}

//region UI Related (Ideally, should go on a separate file)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainContentPreview() {
    TicTacToeTheme {
        MainContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    uiStateFlow: StateFlow<UiState> = MutableStateFlow(sampleUiState).asStateFlow(),
    onClickGridCell: ((row: Int, col: Int) -> Unit)? = null,
    onClickPlayAgain: (() -> Unit)? = null,
    onClickReset: (() -> Unit)? = null
) {
    val uiState = uiStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(30))
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(3.dp)
                    .padding(horizontal = 3.dp),
                text = uiState.value.playerXScore
            )

            Spacer(modifier = Modifier.padding(6.dp))

            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(30))
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(3.dp)
                    .padding(horizontal = 3.dp),
                text = uiState.value.playerOScore
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
            text = uiState.value.playerTurnText,
            style = MaterialTheme.typography.bodyMedium
        )

        LazyVerticalGrid(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 64.dp)
                .background(color = Purple40),
            columns = GridCells.Fixed(3)
        ){
            items(count = 9) { index ->
                val row = index/3
                val col = index % 3
                Button(
                    onClick = { onClickGridCell?.invoke(row, col) },
                    modifier = Modifier
                        .padding(getPaddingFor(row, col, 3.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = uiState.value.gridValues[row][col] ?: "",
                        fontSize = 32.sp
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .padding(12.dp)
                .padding(top = 34.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(30))
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(3.dp)
                .padding(horizontal = 3.dp)
                .clickable { onClickReset?.invoke() },
            text = "Reset"
        )

        AnimatedVisibility(visible = uiState.value.gameOverText.isNotEmpty()) {
            AlertDialog(onDismissRequest = {}) {
                Column {
                    Text(text = uiState.value.gameOverText)
                    Spacer(modifier = Modifier.size(6.dp))
                    Button(onClick = { onClickPlayAgain?.invoke() }) {
                        Text(text = "Play Again")
                    }
                }
            }
        }
    }
}

fun getPaddingFor(row: Int, col: Int, padding: Dp): PaddingValues {
    if (row == 0 && col == 0) return PaddingValues(end = padding, bottom = padding)
    if (row == 1 && col == 0) return PaddingValues(end = padding)
    if (row == 0 && col == 2) return PaddingValues(start = padding, bottom = padding)
    if (row == 1 && col == 2) return PaddingValues(start = padding)
    if (row == 2 && col == 0) return PaddingValues(top = padding, end = padding)
    if (row == 2 && col == 2) return PaddingValues(top = padding, start = padding)
    if (row == 2 && col == 1) return PaddingValues(top = padding)
    return PaddingValues()
}

data class UiState(
    val playerXScore: String = "X - 0 wins",
    val playerOScore: String = "O - 0 wins",
    val playerTurnText: String = "X Turn",
    val gameOverText: String = "",
    val gridValues: Array<Array<String?>> = arrayOf(
        arrayOf(null, null, null),
        arrayOf(null, null, null),
        arrayOf(null, null, null)
    )
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiState

        return gridValues.contentDeepEquals(other.gridValues)
    }

    override fun hashCode(): Int {
        return gridValues.contentDeepHashCode()
    }
}

val sampleUiState = UiState(gridValues = arrayOf(
    arrayOf(null, "X", null),
    arrayOf(null, "O", null),
    arrayOf("X", null, null)
))

/**
 * data class UiState(
 *     val playerXScore: MutableStateFlow<String> = MutableStateFlow("X - 0"),
 *     val playerOScore: MutableStateFlow<String> = MutableStateFlow("O - 0"),
 *     val playerTurnText: MutableStateFlow<String> = MutableStateFlow("X Turn"),
 *     val gameOverText: MutableStateFlow<String?> = MutableStateFlow(null),
 *     val gridValues: SnapshotStateList<Array<Array<String?>>> = mutableStateListOf<Array<Array<String?>>>( arrayOf(
 *         arrayOf(null, null, null),
 *         arrayOf(null, null, null),
 *         arrayOf(null, null, null)
 *     )
 * ))
 */

//endregion
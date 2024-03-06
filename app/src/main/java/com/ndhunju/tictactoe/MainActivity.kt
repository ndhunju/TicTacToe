package com.ndhunju.tictactoe

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    // Member Variables (Ideally should be inside ViewModel)
    private var playerX = "X"
    private var playerO = "O"
    private var currentPlayer = playerX
    private var playerXWins = 0
    private var playerOWins = 0

    // View Variables
    private lateinit var gridLayout: GridLayout
    private lateinit var textView11: TextView
    private lateinit var textView12: TextView
    private lateinit var textView13: TextView
    private lateinit var textView21: TextView
    private lateinit var textView22: TextView
    private lateinit var textView23: TextView
    private lateinit var textView31: TextView
    private lateinit var textView32: TextView
    private lateinit var textView33: TextView
    private lateinit var playerTurn: TextView
    private lateinit var playerXScore: TextView
    private lateinit var playerOScore: TextView

    private lateinit var col1: Array<TextView>
    private lateinit var col2: Array<TextView>
    private lateinit var col3: Array<TextView>
    private lateinit var row1: Array<TextView>
    private lateinit var row2: Array<TextView>
    private lateinit var row3: Array<TextView>
    private lateinit var diagonal1: Array<TextView>
    private lateinit var diagonal2: Array<TextView>

    private lateinit var cols: Array<Array<TextView>>
    private lateinit var rows: Array<Array<TextView>>
    private lateinit var diagonals: Array<Array<TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind all views
        gridLayout = findViewById(R.id.grid_layout)
        textView11 = findViewById(R.id.one_one)
        textView12 = findViewById(R.id.one_two)
        textView13 = findViewById(R.id.one_three)
        textView21 = findViewById(R.id.two_one)
        textView22 = findViewById(R.id.two_two)
        textView23 = findViewById(R.id.two_three)
        textView31 = findViewById(R.id.three_one)
        textView32 = findViewById(R.id.three_two)
        textView33 = findViewById(R.id.three_three)
        playerTurn = findViewById(R.id.player_turn)
        playerXScore = findViewById(R.id.player_x_score)
        playerOScore = findViewById(R.id.player_o_score)
        val reset = findViewById<View>(R.id.reset)

        // Get references to all columns
        col1 = arrayOf(textView11, textView21, textView31)
        col2 = arrayOf(textView12, textView22, textView32)
        col3 = arrayOf(textView13, textView23, textView33)
        cols = arrayOf(col1, col2, col3)

        // Get references to all rows
        row1 = arrayOf(textView11, textView12, textView13)
        row2 = arrayOf(textView21, textView22, textView23)
        row3 = arrayOf(textView31, textView32, textView33)
        rows = arrayOf(row1, row2, row3)

        // Get references to both diagonals
        diagonal1= arrayOf(textView11, textView22, textView33)
        diagonal2 = arrayOf(textView13, textView22, textView31)
        diagonals = arrayOf(diagonal1, diagonal2)

        // Set OnClickListener to all TextViews
        for (i in 0 until gridLayout.childCount) {
            gridLayout.getChildAt(i).setOnClickListener {
                makeMoveIfValid(it as TextView)
            }
        }

        // Set OnClickListener to Restart Button
        reset.setOnClickListener {
            reset()
        }

        // Show who's turn is it
        updatePlayerTurnText()
        updateScoreTexts()
    }

    /**
     * Makes the move if it is a valid move
     */
    private fun makeMoveIfValid(textView: TextView) {
        if (isValidMove(textView)) {
            makeMove(textView)
            announceWinnerOrTieIfExists()
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
        playerTurn.text = "$currentPlayer Turn"
    }

    private fun updateScoreTexts() {
        playerXScore.text = "$playerX - $playerXWins wins"
        playerOScore.text = "$playerO - $playerOWins wins"
    }

    private fun isValidMove(view: TextView): Boolean {
        // Check if there is already a mark in current view
        return view.text.isNullOrEmpty()
    }

    private fun makeMove(textView: TextView) {
        textView.text = SpannableString(currentPlayer).apply {
            // Use white color text for O and black for x
            setSpan(
                ForegroundColorSpan(if (currentPlayer == playerO) Color.WHITE else Color.BLACK),
                0,
                currentPlayer.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun announceWinnerOrTieIfExists() {
        val winner = getWinnerIfExist()
        if (winner != null) {
            if (winner == playerX) playerXWins++
            if (winner == playerO) playerOWins++
            updateScoreTexts()
            announce("Player $winner won! 🎉")
        } else if (isTie()) {
            announce("It is a tie!")
        }
    }

    /**
     * Displays alert dialog with [title] and a reset button
     */
    private fun announce(title: String) {
        // Show the winner in the alert dialog
        AlertDialog.Builder(this)
            .setTitle(title)
            .setPositiveButton("Play Again") { _, _ -> playAgain() }
            .show()
    }

    private fun playAgain() {
        // Reset all texts
        for (i in 0 until gridLayout.childCount) {
            (gridLayout.getChildAt(i) as TextView).text = null
        }
    }

    private fun reset() {
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
        cols.forEach { col ->
            if (hasSameNonEmptyValues(col)) {
                return col[0].text.toString()
            }
        }

        // Check if the same value are present in any row
        rows.forEach { row ->
            if (hasSameNonEmptyValues(row)) {
                return row[0].text.toString()
            }
        }

        // Check if the same value are present in any diagonal
        diagonals.forEach { diagonal ->
            if (hasSameNonEmptyValues(diagonal)) {
                return diagonal[0].text.toString()
            }
        }

        return null

    }

    private fun isTie(): Boolean {
        // Check if all the boxes are filled
        for (i in 0 until gridLayout.childCount) {
            val isNullOrEmpty = (gridLayout.getChildAt(i) as TextView).text.isNullOrEmpty()
            if (isNullOrEmpty) return false
        }

        return true
    }

    /**
     * Returns true if all the [TextView] in [textViews] have same text
     */
    private fun hasSameNonEmptyValues(textViews: Array<TextView>): Boolean {
        val firstValue = textViews.firstOrNull()?.text?.toString() ?: return false
        if (firstValue.isEmpty()) return false
        textViews.forEach { textView ->
            if ((textView.text.toString() == firstValue).not()) {
                return false
            }
        }

        return true
    }
}
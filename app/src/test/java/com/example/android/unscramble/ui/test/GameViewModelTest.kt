package com.example.android.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test



class GameViewModelTest {
    private val viewModel = GameViewModel()

    companion object{
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = 20
        private const val SCORE_ZERO = 0
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdateAndErrorFlagUnset(){
        // Arrange.
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        // Act.
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        // Assert.
        currentGameUiState = viewModel.uiState.value
        assertFalse(currentGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuiess_ErrorFlagSet(){
        // Arrange.
        val incorrectPlayerWord = "incorrectWord"

        // Act.
        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        // Assert.
        val currentGameUiState = viewModel.uiState.value
        assertEquals(SCORE_ZERO, currentGameUiState.score)
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_Init_FirstWordLoaded(){
        // Arrange.
        val gameUiState = viewModel.uiState.value

        // Act.
        val unscrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Assert.
        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        assertTrue(gameUiState.currentWordCount == 1)
        assertEquals(SCORE_ZERO, gameUiState.score)
        assertFalse(gameUiState.isGuessedWordWrong)
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdateCorrectly(){
        // Arrange.
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        // Act.
        repeat(MAX_NO_OF_WORDS){
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            assertEquals(expectedScore, currentGameUiState.score)
        }

        // Assert.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_PlayerSkipWord_ScoreNotIncreaseAndCurrentWordCountIncrease(){
        // Arrange.
        var currentGameUiState = viewModel.uiState.value
        val lastWordsCount = currentGameUiState.currentWordCount

        // Act.
        getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.skipWord()

        // Assert.
        currentGameUiState = viewModel.uiState.value
        assertEquals(lastWordsCount + 1, currentGameUiState.currentWordCount)
        assertEquals(SCORE_ZERO, currentGameUiState.score)
    }
}
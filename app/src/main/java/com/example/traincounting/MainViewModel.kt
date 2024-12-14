package com.example.traincounting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _level = MutableLiveData(1) // Текущий уровень
    val level: LiveData<Int> = _level

    var isInputMode: Boolean = (0..1).random() == 1

    private val _timeLimit = MutableLiveData(30) // Время на ответ (в секундах)
    val timeLimit: LiveData<Int> = _timeLimit

    fun generateNewQuestion() {
        val question = QuestionGenerator.generate(_level.value ?: 1, isInputMode)
        _currentQuestion.value = question
    }

    fun nextLevel() {
        val increaseDifficulty = listOf(true, false).random() // Случайно выбираем, увеличивать сложность или уменьшать время

        if (increaseDifficulty) {
            _level.value = (_level.value ?: 1) + 1 // Увеличиваем уровень
        } else {
            _timeLimit.value = (_timeLimit.value ?: 30).coerceAtLeast(5) - 5 // Уменьшаем время (минимум до 5 секунд)
        }

        isInputMode = (0..1).random() == 1
        generateNewQuestion()
    }

    fun resetGame() {
        _level.value = 1 // Сбрасываем уровень
        _timeLimit.value = 30 // Сбрасываем время
        generateNewQuestion() // Генерируем новый вопрос
    }
}

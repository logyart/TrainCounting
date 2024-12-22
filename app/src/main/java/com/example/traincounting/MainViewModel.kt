package com.example.traincounting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _level = MutableLiveData(1) // Текущий уровень
    val level: LiveData<Int> = _level

    private var difficultly = 1 // Сложность вопроса = количество операций

    var isInputMode: Boolean = (0..1).random() == 1

    private val _timeLimit = MutableLiveData(30) // Время на ответ (в секундах)
    val timeLimit: LiveData<Int> = _timeLimit

    fun generateNewQuestion() {
        val question = QuestionGenerator.generate(difficultly, isInputMode)
        _currentQuestion.value = question
    }

    fun nextLevel() {
        _level.value = (_level.value ?: 1) + 1

        val increaseDifficulty = listOf(true, false).random() // Случайно выбираем, увеличивать сложность или уменьшать время

        if (increaseDifficulty) {
            difficultly++ // Увеличиваем сложность
        } else {
            _timeLimit.value = (_timeLimit.value ?: 30).coerceAtLeast(5) - 5 // Уменьшаем время (минимум до 5 секунд)
            difficultly = listOf(1, 2, 3).random()
        }

        isInputMode = (0..1).random() == 1
        generateNewQuestion()
    }

    fun resetGame() {
        _level.value = 1 // Сбрасываем уровень
        difficultly = 1 // Сбрасываем сложность
        _timeLimit.value = 30 // Сбрасываем время
        generateNewQuestion() // Генерируем новый вопрос
    }
}

package com.example.traincounting

import kotlin.random.Random

object QuestionGenerator {

    // Генерация вопроса
    fun generate(difficultly: Int, isInputMode: Boolean): Question {
        val system = listOf(2, 8, 16).random() // Выбор системы счисления
        val numOperations = (1..3).random().coerceAtMost(difficultly) // Количество операций, максимум 3
        val expressionParts = mutableListOf<String>()

        val result = Random.nextInt(1, difficultly * 10)
        var currentResult = result
        expressionParts.add(Integer.toString(result, system).uppercase())

        repeat(numOperations) {
            val nextNum = Random.nextInt(1, difficultly * 10)
            val operation = listOf("+", "-").random()

            currentResult = when (operation) {
                "+" -> currentResult + nextNum
                "-" -> currentResult - nextNum
                else -> currentResult
            }

            expressionParts.add(operation)
            expressionParts.add(Integer.toString(nextNum, system).uppercase())
        }

        val resultConverted = Integer.toString(currentResult, system).uppercase()
        var questionText = expressionParts.joinToString(" ")
        val expression = "$questionText = $resultConverted"
        if (isInputMode) {
            return Question("$questionText =", expression, resultConverted, system)
        }

        val comparisonOp = listOf("=", "<", ">").random()
        val comparisonResult = listOf(
            currentResult,
            currentResult + Random.nextInt(1, difficultly + 1),
            currentResult - Random.nextInt(1, difficultly + 1),
        ).random()

        val comparisonConverted = Integer.toString(comparisonResult, system).uppercase()
        questionText= "$questionText $comparisonOp $comparisonConverted"
        val isCorrectQuestion = evaluateComparison(currentResult, comparisonResult, comparisonOp)

        return Question(questionText, expression, resultConverted, system, isCorrectQuestion)
    }

    // Проверка выражения
    private fun evaluateComparison(result: Int, comparisonResult: Int,
                                   comparisonOperation: String): Boolean {
        return when (comparisonOperation) {
            "=" -> result == comparisonResult
            "<" -> result < comparisonResult
            ">" -> result > comparisonResult
            else -> false
        }
    }
}

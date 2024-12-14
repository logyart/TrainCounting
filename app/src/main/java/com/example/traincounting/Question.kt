package com.example.traincounting

data class Question(
    val text: String,
    val expression: String,
    val result: String,
    val system: Int,
    val isCorrectQuestion: Boolean = false,
)

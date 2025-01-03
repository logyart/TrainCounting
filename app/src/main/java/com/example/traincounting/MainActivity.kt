package com.example.traincounting

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.traincounting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var timer: CountDownTimer

    private var maxLives = 3
    private var lives = maxLives

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLives(maxLives) // Инициализация сердечек
        observeViewModel()
        setupListeners()

        viewModel.generateNewQuestion() // Генерируем первый вопрос
    }

    private fun observeViewModel() {
        viewModel.currentQuestion.observe(this) { question ->
            binding.questionText.text = question.text
            binding.systemText.text = getString(R.string.system_text, question.system)

            updateUiMode()
            startTimer(viewModel.timeLimit.value?.times(1000L) ?: 30_000L)
        }

        viewModel.level.observe(this) { level ->
            binding.levelText.text = getString(R.string.level_name, level)
        }
    }

    private fun setupListeners() {
        binding.yesButton.setOnClickListener { handleYesNoAnswer(true) }
        binding.noButton.setOnClickListener { handleYesNoAnswer(false) }
        binding.checkButton.setOnClickListener { handleUserInput() }
        binding.nextButton.setOnClickListener { showNextQuestion() }
    }

    private fun handleYesNoAnswer(userAnswer: Boolean) {
        val currentQuestion = viewModel.currentQuestion.value ?: return

        if (userAnswer == currentQuestion.isCorrectQuestion) {
            showFeedback(
                getString(R.string.correct_text) + ": " + currentQuestion.expression,
                R.color.green
            )
        } else {
            showFeedback(
                getString(R.string.incorrect_text) + ": " + currentQuestion.expression,
                R.color.red
            )
            removeLife()
        }

        finishCurrentRound()
    }

    private fun handleUserInput() {
        val currentQuestion = viewModel.currentQuestion.value ?: return
        val userInput = binding.userInput.text.toString().uppercase()

        if (userInput.isEmpty()) {
            showErrorDialog(getString(R.string.error_input_text))
            return
        }

        binding.userInput.isEnabled = false

        if (userInput == currentQuestion.result) {
            showFeedback(
                getString(R.string.correct_text) + ": " + currentQuestion.expression,
                R.color.green
            )

        } else {
            showFeedback(
                getString(R.string.incorrect_text) + ": " + currentQuestion.expression,
                R.color.red
            )
            removeLife()
        }

        finishCurrentRound()
    }


    private fun finishCurrentRound() {
        binding.checkButton.isEnabled = false
        binding.userInput.isEnabled = false
        binding.yesButton.isEnabled = false
        binding.noButton.isEnabled = false
        binding.nextButton.visibility = android.view.View.VISIBLE
        timer.cancel()
    }

    private fun showNextQuestion() {
        viewModel.nextLevel()
        binding.nextButton.visibility = android.view.View.INVISIBLE
        binding.answerFeedbackText.visibility = android.view.View.INVISIBLE
        updateUiMode()
    }

    private fun updateUiMode() {
        if (viewModel.isInputMode) {
            binding.inputContainer.visibility = android.view.View.VISIBLE
            binding.yesNoContainer.visibility = android.view.View.INVISIBLE
            binding.userInput.text.clear()
            binding.checkButton.isEnabled = true
            binding.userInput.isEnabled = true
        } else {
            binding.inputContainer.visibility = android.view.View.INVISIBLE
            binding.yesNoContainer.visibility = android.view.View.VISIBLE
            binding.yesButton.isEnabled = true
            binding.noButton.isEnabled = true
        }
    }

    private fun startTimer(timeMillis: Long) {
        if (::timer.isInitialized) {
            timer.cancel()
        }

        timer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.timerText.text = getString(R.string.timer_name, secondsLeft)
            }

            override fun onFinish() {
                val currentQuestion = viewModel.currentQuestion.value ?: return
                showFeedback(
                    getString(R.string.lose_timer_text) + ": " + currentQuestion.expression,
                    R.color.red
                )
                removeLife()
                finishCurrentRound()
            }
        }

        timer.start()
    }

    private fun setupLives(maxLives: Int) {
        binding.livesContainer.removeAllViews()
        lives = maxLives

        for (i in 0 until maxLives) {
            val heart = ImageView(this)
            heart.setImageResource(R.drawable.ic_heart)
            heart.layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                marginEnd = 8
            }
            binding.livesContainer.addView(heart)
        }
    }

    private fun removeLife() {
        if (lives > 0) {
            lives--
            binding.livesContainer.removeViewAt(binding.livesContainer.childCount - 1)
        }
        if (lives == 0) {
            showGameOverDialog(getString(R.string.lose_errors_text))
        }
    }

    private fun showFeedback(message: String, colorRes: Int) {
        binding.answerFeedbackText.apply {
            text = message
            setTextColor(resources.getColor(colorRes, theme))
            visibility = android.view.View.VISIBLE
        }
    }

    private fun showGameOverDialog(title: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(R.string.restart_text)
            .setCancelable(false) // Запрещаем закрытие по пустому экрану
            .setPositiveButton(R.string.restart_btn) { _, _ ->
                restartGame()
            }
            .setNegativeButton(R.string.exit_btn) { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
            .create()
            .show()
    }

    private fun showErrorDialog(
        message: String,
        title: String = getString(R.string.error_name),
        okBtn: String = getString(R.string.ok_btn)
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(okBtn) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun restartGame() {
        setupLives(maxLives)
        viewModel.resetGame()
        binding.answerFeedbackText.visibility = android.view.View.INVISIBLE
        binding.nextButton.visibility = android.view.View.INVISIBLE
    }
}

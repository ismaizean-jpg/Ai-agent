package com.example.aiagent

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var input: EditText
    private lateinit var output: TextView
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "🤖 AI AGENT"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setPadding(0, 10, 0, 20)

        output = TextView(this)
        output.text = """
            Level 109 AI Agent Ready
            
            Ask me something or use voice input.
        """.trimIndent()
        output.textSize = 17f
        output.setPadding(0, 10, 0, 20)

        input = EditText(this)
        input.hint = "Type your task..."
        input.minLines = 2

        val runButton = Button(this)
        runButton.text = "🚀 RUN AGENT"

        runButton.setOnClickListener {
            runAgent(input.text.toString())
        }

        val voiceButton = Button(this)
        voiceButton.text = "🎤 VOICE INPUT"

        voiceButton.setOnClickListener {
            startVoiceInput()
        }

        root.addView(title)

        root.addView(
            output,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(input)
        root.addView(runButton)
        root.addView(voiceButton)

        setContentView(root)
    }

    private fun runAgent(task: String) {

        val text = task.trim()

        if (text.isEmpty()) {
            output.text = "⚠️ Please enter a task."
            return
        }

        val result = calculate(text)

        if (result != null) {

            output.text = """
                🤖 AI AGENT
                
                🎯 Task:
                $text
                
                🧮 Answer:
                $result
                
                🔊 AI:
                The answer is $result
            """.trimIndent()

            tts.speak(
                "The answer is $result",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "agent_answer"
            )

        } else {

            output.text = """
                🤖 AI AGENT
                
                🎯 Task:
                $text
                
                ✅ Task received.
                
                🧠 Advanced Gemini processing
                will be connected through the
                secure backend.
            """.trimIndent()
        }
    }

    private fun calculate(text: String): String? {

        var expression = text.lowercase(Locale.US)

        expression = expression
            .replace("multiplied by", "*")
            .replace("multiply by", "*")
            .replace("times", "*")
            .replace("plus", "+")
            .replace("minus", "-")
            .replace("divided by", "/")
            .replace("divide by", "/")
            .replace("calculate", "")
            .replace("what is", "")
            .replace(" ", "")

        val match = Regex(
            """\d+(?:[\+\-\*/]\d+)+"""
        ).find(expression) ?: return null

        val exp = match.value

        return try {

            val parts = exp.split(
                Regex("""(?=[\+\-\*/])|(?<=[\+\-\*/])""")
            )

            var result = parts[0].toDouble()
            var index = 1

            while (index < parts.size) {

                val operator = parts[index]
                val number = parts[index + 1].toDouble()

                result = when (operator) {

                    "+" -> result + number
                    "-" -> result - number
                    "*" -> result * number
                    "/" -> result / number

                    else -> return null
                }

                index += 2
            }

            if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }

        } catch (e: Exception) {
            null
        }
    }

    private fun startVoiceInput() {

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak your task"
        )

        try {
            startActivityForResult(intent, 1001)
        } catch (e: Exception) {
            output.text =
                "⚠️ Voice input is not available."
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == 1001 &&
            resultCode == RESULT_OK
        ) {

            val results =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

            val spokenText =
                results?.firstOrNull()

            if (spokenText != null) {

                input.setText(spokenText)

                runAgent(spokenText)
            }
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language = Locale.US
        }
    }

    override fun onDestroy() {

        tts.stop()
        tts.shutdown()

        super.onDestroy()
    }
}

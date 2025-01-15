package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.English
import jal.voca.quiz.Quiz
import jal.voca.quiz.QuizContext
import jal.voca.quiz.QuizItem

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Result(globalContext!!)
                }
            }
        }
    }

    @Composable
    fun Result(context: QuizContext, modifier: Modifier = Modifier) {
        Column(modifier = modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp).fillMaxSize()) {
            Text("Result: " + context.score.score + "/" + context.score.total)
            if (context.failures.isNotEmpty()) {
                Text("Failures:")
                for (failure in context.failures) {
                    Text("  " + failure.question + " -> " + failure.answer)
                }
            }
            Button (onClick = {
                next()
            }) {
                Text("OK")
            }
        }
    }

    fun next() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun ResultPreview() {
        val item = QuizItem("What is the color of Napoleon's white horse?", "White", "")
        val context = QuizContext(Quiz(listOf(item)), English())
        context.nextItem()
        for (i in 1..9) {
            context.success()
        }
        context.failure()
        VocaTheme {
            Result(context)
        }
    }
}
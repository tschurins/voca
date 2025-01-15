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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.English
import jal.voca.quiz.AnswerResult
import jal.voca.quiz.Quiz
import jal.voca.quiz.QuizContext
import jal.voca.quiz.QuizItem

class AnswerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = globalContext!!
        val currentItem = context.currentItem!!

        enableEdgeToEdge()
        setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Answer(currentItem, context, context.currentAnswer!!)
                }
            }
        }
    }

    @Composable
    fun Answer(item: QuizItem, context: QuizContext, givenAnswer: String, modifier: Modifier = Modifier) {
        Column(modifier = modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp).fillMaxSize()) {
            Text(item.question)
            Text(item.moreInfo)

            val result = context.checkSuccess()
            when (result) {
                AnswerResult.SUCCESS -> {
                    Text(text = givenAnswer, color = Color(0xff006500))
                }
                AnswerResult.SUCCESS_HOMOPHONE -> {
                    Text(text = givenAnswer, color = Color(0xffb49600))
                    Text(text = item.answer)
                }
                AnswerResult.FAILURE -> {
                    Text(text = givenAnswer, color = Color.Red)
                    Text(text = item.answer)
                }
            }
            Button(onClick = {
                next(context)
            }) {
                Text("Next")
            }
        }
    }

    private fun next(context: QuizContext) {
        if (context.hasNextItem()) {
            context.nextItem()
            val intent = Intent(this, QuestionActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CorrectAnswerPreview() {
        val item = QuizItem("What is the color of Napoleon's white horse?", "White", "")
        val context = QuizContext(Quiz(listOf(item)), English())
        context.nextItem()
        context.setAnswer("White")
        VocaTheme {
            Answer(item, context, "White")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WrongAnswerPreview() {
        val item = QuizItem("What is the color of Napoleon's white horse?", "White", "")
        val context = QuizContext(Quiz(listOf(item)), English())
        context.nextItem()
        context.setAnswer("Black")
        VocaTheme {
            Answer(item, context, "Black")
        }
    }
}
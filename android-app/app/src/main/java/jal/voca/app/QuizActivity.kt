package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.English
import jal.voca.quiz.AnswerResult
import jal.voca.quiz.Quiz
import jal.voca.quiz.QuizContext
import jal.voca.quiz.QuizItem


class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Quiz(globalContext!!, Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun Quiz(context: QuizContext, modifier: Modifier = Modifier, initialState: State = State.QUESTION) {
        var state by remember { mutableStateOf(initialState) }

        when (state) {
            State.QUESTION -> {
                Question(context.currentItem!!, modifier) { answer ->
                    context.setAnswer(answer)
                    state = State.ANSWER
                }
            }
            State.ANSWER -> {
                Answer(context.currentItem!!, context, context.currentAnswer!!, modifier) {
                    if (context.hasNextItem()) {
                        context.nextItem()
                        state = State.QUESTION
                    } else {
                        state = State.RESULT
                    }
                }
            }
            State.RESULT -> {
                Result(context, modifier) {
                    backToMain()
                }
            }
        }
    }

    @Composable
    fun Question(item: QuizItem, modifier: Modifier = Modifier, nextAction: (String) -> Unit) {
        var answer by remember { mutableStateOf("") }

        // show keyboard https://stackoverflow.com/a/76759961
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(key1 = Unit, block = {
            focusRequester.requestFocus()
        })

        // react to enter: https://stackoverflow.com/a/78278443
        Column(modifier = modifier
            .fillMaxSize()) {
            Text(item.question, modifier = Modifier.padding(4.dp))
            TextField(
                value = answer,
                onValueChange = { answer = it.replace("\n", "") },
                label = { Text("Answer") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    autoCorrect = false,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        nextAction(answer)
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .onKeyEvent { event -> // For hardware keyboard
                        if (event.key == Key.Enter) {
                            nextAction(answer)

                            // to stop propagation of this event.
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    },
            )
            Button(onClick = {
                nextAction(answer)
            }, modifier = Modifier.padding(4.dp)) {
                Text("OK")
            }
        }
    }


    @Composable
    fun Answer(item: QuizItem, context: QuizContext, givenAnswer: String, modifier: Modifier = Modifier, nextAction: () -> Unit) {
        Column(modifier = modifier
                .fillMaxSize()) {
            Text(item.question, modifier = Modifier.padding(4.dp))
            Text(item.moreInfo, modifier = Modifier.padding(4.dp))

            val result = context.checkSuccess()
            when (result) {
                AnswerResult.SUCCESS -> {
                    Text(text = item.answer, color = Color(0xff006500), modifier = Modifier.padding(4.dp))
                }
                AnswerResult.SUCCESS_HOMOPHONE -> {
                    Text(text = givenAnswer, color = Color(0xffb49600), modifier = Modifier.padding(4.dp))
                    Text(text = item.answer, modifier = Modifier.padding(4.dp))
                }
                AnswerResult.FAILURE -> {
                    Text(text = givenAnswer, color = Color.Red, modifier = Modifier.padding(4.dp))
                    Text(text = item.answer, modifier = Modifier.padding(4.dp))
                }
            }
            Button(onClick = {
                nextAction()
            }, modifier = Modifier.padding(4.dp)) {
                Text("Next")
            }
        }
    }

    @Composable
    fun Result(context: QuizContext, modifier: Modifier = Modifier, nextAction: () -> Unit) {
        Column(modifier = modifier
                .fillMaxSize()) {
            Text("Result: " + context.score.score + "/" + context.score.total, modifier = Modifier.padding(4.dp))
            if (context.failures.isNotEmpty()) {
                Text("Failures:", modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp))
                for (failure in context.failures) {
                    Text("  " + failure.question + " -> " + failure.answer, modifier = Modifier.padding(start = 4.dp, end = 4.dp))
                }
            }
            Button (onClick = {
                nextAction()
            }, modifier = Modifier.padding(4.dp)) {
                Text("OK")
            }
        }
    }

    private fun backToMain() {
        // restart the main activity as top of the back stack.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun QuestionPreview() {
        val context = QuizContext(Quiz(listOf(QuizItem("What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        VocaTheme {
            Quiz(context = context, initialState = State.QUESTION)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CorrectAnswerPreview() {
        val context = QuizContext(Quiz(listOf(QuizItem("What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        context.setAnswer("White")
        VocaTheme {
            Quiz(context = context, initialState = State.ANSWER)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WrongAnswerPreview() {
        val context = QuizContext(Quiz(listOf(QuizItem("What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        context.setAnswer("Black")
        VocaTheme {
            Quiz(context = context, initialState = State.ANSWER)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ResultPreview() {
        val context = QuizContext(Quiz(listOf(QuizItem("What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        for (i in 1..9) {
            context.success()
        }
        context.failure()
        VocaTheme {
            Quiz(context = context, initialState = State.RESULT)
        }
    }

    enum class State {
        QUESTION, ANSWER, RESULT
    }
}
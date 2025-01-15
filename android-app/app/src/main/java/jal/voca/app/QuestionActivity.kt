package jal.voca.app

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.English
import jal.voca.quiz.Quiz
import jal.voca.quiz.QuizContext
import jal.voca.quiz.QuizItem


class QuestionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentItem = globalContext!!.currentItem!!

        enableEdgeToEdge()
        setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Question(currentItem, globalContext!!)
                }
            }
        }
    }

    @Composable
    fun Question(item: QuizItem, context: QuizContext, modifier: Modifier = Modifier) {
        var answer by remember { mutableStateOf("") }

        // show keyboard https://stackoverflow.com/a/76759961
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        //Need to hide keyboard when we navigate or pop back
        DisposableEffect(key1 = Unit, effect = {
            onDispose {
                keyboardController?.hide()
                focusRequester.freeFocus()
            }
        })

        LaunchedEffect(key1 = Unit, block = {
            focusRequester.requestFocus()
        })

        // react to enter: https://stackoverflow.com/a/78278443
        Column(modifier = modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxSize()) {
            Text(item.question)
            // Question panel
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
                        answerGiven(answer)
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .onKeyEvent { event -> // For hardware keyboard
                        if (event.key == Key.Enter) {
                            answerGiven(answer)

                            // to stop propagation of this event.
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    },
            )
            Button(onClick = {
                answerGiven(answer)
            }) {
                Text("OK")
            }
        }
    }

    private fun answerGiven(answer: String) {
        globalContext!!.setAnswer(answer)
        val intent = Intent(this, AnswerActivity::class.java)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun QuestionPreview() {
        val context = QuizContext(Quiz(listOf()), English())
        val item = QuizItem("What is the color of Napoleon's white horse?", "White", "")
        VocaTheme {
            Question(item, context)
        }
    }
}
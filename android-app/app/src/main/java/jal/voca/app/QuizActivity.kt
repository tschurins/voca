package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
    private var dbHelper: FavoritesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = FavoritesManager(this)

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
                Answer(context.currentItem!!, context, context.currentAnswer!!, modifier,
                    nextAction = {
                        if (context.hasNextItem()) {
                            context.nextItem()
                            state = State.QUESTION
                        } else {
                            state = State.RESULT
                        }
                    },
                    endAction = {
                        state = State.RESULT
                    }
                )
            }
            State.RESULT -> {
                Result(context, modifier) {
                    backToMenu()
                }
            }
        }
    }

    @Composable
    fun Question(item: QuizItem,
                 modifier: Modifier = Modifier,
                 nextAction: (String) -> Unit) {
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    nextAction(answer)
                }, modifier = Modifier.padding(4.dp)) {
                    Text("OK")
                }
                FavoriteButton(item = item, modifier = Modifier.padding(4.dp))
            }
        }
    }


    @Composable
    fun Answer(item: QuizItem, context: QuizContext,
               givenAnswer: String,
               modifier: Modifier = Modifier,
               nextAction: () -> Unit,
               endAction: () -> Unit) {
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
            Row (modifier = Modifier.fillMaxWidth()) {
                if (context.hasNextItem()) {
                    Button(onClick = {
                        nextAction()
                    }, modifier = Modifier.padding(4.dp)) {
                        Text("Next")
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    endAction()
                }, modifier = Modifier.padding(4.dp)) {
                    Text("End")
                }
                FavoriteButton(item = item, modifier = Modifier.padding(4.dp))
            }
        }
    }

    @Composable
    fun FavoriteButton(
        item: QuizItem,
        modifier: Modifier,
    ) {
        var stateF by remember { mutableStateOf(globalFavorites.isFavorite(item.id)) }

        Box(modifier = modifier.padding(horizontal = 10.dp)) {
            Button(
                onClick = {
                    stateF = !stateF
                    setFavorite(item, stateF)
                },
                shape = CircleShape,
                modifier = modifier.size(40.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                // Inner content including an icon and a text label
                Icon(
                    imageVector = Icons.Default.Star,
                    tint = if (stateF) Color.Yellow else Color.White,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(20.dp)
                )
            }

        }
    }

    @Composable
    fun Result(context: QuizContext, modifier: Modifier = Modifier, nextAction: () -> Unit) {
        Column(modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            Text(if (globalCategory == null) "All words" else globalCategory!!.name, modifier = Modifier.padding(4.dp))
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

    private fun setFavorite(item: QuizItem, favorite: Boolean) {
        if (favorite) {
            dbHelper?.addFavorite(item.id)
        } else {
            dbHelper?.removeFavorite(item.id)
        }
    }

    private fun backToMenu() {
        // restart the main activity as top of the back stack.
        val intent = Intent(this, if (globalCategoryLoader == null) MainActivity::class.java else CategoryMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        dbHelper?.close()
        super.onDestroy()
    }




    @Preview(showBackground = true)
    @Composable
    fun QuestionPreview() {
        globalFavorites.clear()
        val context = QuizContext(Quiz(listOf(QuizItem("id", "What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        VocaTheme {
            Quiz(context = context, initialState = State.QUESTION)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun QuestionPreviewFavorite() {
        val question = "What is the color of Napoleon's white horse?"
        val context = QuizContext(Quiz(listOf(QuizItem("id", question, "White", ""))), English())
        globalFavorites.setFavorite("id", true)
        context.nextItem()
        VocaTheme {
            Quiz(context = context, initialState = State.QUESTION)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CorrectAnswerPreview() {
        globalFavorites.clear()
        val context = QuizContext(Quiz(listOf(QuizItem("id", "What is the color of Napoleon's white horse?", "White", ""))), English())
        context.nextItem()
        context.setAnswer("White")
        VocaTheme {
            Quiz(context = context, initialState = State.ANSWER)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WrongAnswerPreview() {
        globalFavorites.clear()
        val context = QuizContext(Quiz(listOf(
            QuizItem("id1", "What is the color of Napoleon's white horse?", "White", ""),
            QuizItem("id2", "next", "a", ""),
        )), English())
        context.nextItem()
        context.setAnswer("Black")
        VocaTheme {
            Quiz(context = context, initialState = State.ANSWER)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ResultPreview() {
        val context = QuizContext(Quiz(listOf(QuizItem("id", "What is the color of Napoleon's white horse?", "White", ""))), English())
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
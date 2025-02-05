package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.Dictionary
import jal.voca.lang.WordCategory
import jal.voca.quiz.Quiz
import jal.voca.quiz.QuizConfig
import jal.voca.quiz.QuizContext

class CategoryMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
                setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Menu(
                        getGlobalDictionary(this.filesDir),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun startQuiz(targetToBase: Boolean, category: WordCategory?) {
        globalCategory = category
        val dico = getGlobalDictionary(this.filesDir)
        val quiz = Quiz.newQuiz(QuizConfig(
            dico = dico,
            words = category?.words?.associate { it to 1 },
            fromWordToTranslation = targetToBase,
            itemCount = globalConfiguration.itemCount
        ))
        val answerLanguage = if (targetToBase) dico.translationLanguage else dico.wordLanguage
        val context = QuizContext(quiz, answerLanguage)
        context.nextItem()

        globalContext = context
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }

    @Composable
    fun Menu(dictionary: Dictionary, modifier: Modifier = Modifier) {
        val density = LocalDensity.current

        var width by remember { mutableStateOf(0.dp) }
        var buttonModifier = Modifier
            .onGloballyPositioned { coordinates ->
                val coordinateWidthPx = coordinates.size.width
                val coordinateWidthDp = with(density) { coordinateWidthPx.toDp() }
                width = max(width, coordinateWidthDp)
            }

        if (width != 0.dp) {
            buttonModifier = buttonModifier.width(width)
        }

        Row(modifier = modifier
            .padding(4.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp).fillMaxWidth().weight(1f)
            ) {
                Button(modifier = buttonModifier, onClick = { startQuiz(true, null) }) {
                    Text(dictionary.wordLanguage.name + " -> " + dictionary.translationLanguage.name)
                }
                for (category in globalCategoryLoader!!(dictionary, globalSortOptions).values) {
                    Button(modifier = buttonModifier, onClick = { startQuiz(true, category) }) {
                        Text(category.name)
                    }
                }
                Button(modifier = buttonModifier, onClick = { backToMain() }) {
                    Text("Back")
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp).fillMaxWidth().weight(1f)
            ) {
                Button(modifier = buttonModifier, onClick = { startQuiz(false, null) }) {
                    Text(dictionary.translationLanguage.name + " -> " + dictionary.wordLanguage.name)
                }
                for (category in globalCategoryLoader!!(dictionary, globalSortOptions).values) {
                    Button(modifier = buttonModifier, onClick = { startQuiz(false, category) }) {
                        Text(category.name)
                    }
                }
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
    fun MenuPreview() {
        val dictionary = getGlobalDictionary()
        VocaTheme {
            Menu(dictionary)
        }
    }
}
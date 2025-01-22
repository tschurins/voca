package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
                setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Menu(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun Menu(modifier: Modifier = Modifier) {
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(4.dp).fillMaxSize()
        ) {
            Button(modifier = buttonModifier, onClick = { goToQuiz() }) {
                Text("Quizzes")
            }
            Button(modifier = buttonModifier, onClick = { goToConfig() }) {
                Text("Configuration")
            }
        }
    }

    private fun goToQuiz() {
        val intent = Intent(this, CategoryMenuActivity::class.java)
        startActivity(intent)
    }

    private fun goToConfig() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuPreview() {
        VocaTheme {
            Menu()
        }
    }
}
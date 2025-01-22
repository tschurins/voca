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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jal.voca.app.ui.theme.VocaTheme

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
                setContent {
            VocaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Configuration(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun Configuration(modifier: Modifier = Modifier) {
        var itemCount by remember { mutableStateOf("" + configuration.itemCount) }
        Column(modifier = modifier.padding(4.dp).fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp).fillMaxWidth()
            ) {
                Text("Items count:", modifier = Modifier.padding(4.dp))
                TextField(
                    value = itemCount,
                    onValueChange = { itemCount = it.replace("\n", "") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                )
            }
            Button(modifier = Modifier.padding(4.dp), onClick = {
                try {
                    val count = itemCount.toInt()
                    if (count > 0) {
                        configuration.itemCount = count
                    } else {
                        // not a valid count TODO do something smart
                    }
                } catch (nfe: NumberFormatException) {
                    // not a valid int TODO do something smart
                }
                backToMain()
            }) {
                Text("Save")
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
    fun ConfigurationPreview() {
        VocaTheme {
            Configuration()
        }
    }
}
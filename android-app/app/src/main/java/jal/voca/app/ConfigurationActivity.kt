package jal.voca.app

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.io.AllCsvWriter
import jal.voca.lang.io.DictionaryGoogleSheetReader
import jal.voca.lang.io.FullReader

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
        var itemCount by remember { mutableStateOf("" + globalConfiguration.itemCount) }
        var errorMessage by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp).fillMaxWidth()
            ) {
                Button(modifier = Modifier.padding(4.dp), onClick = {
                    class LoadFromGS : AsyncTask<String, Void, String>() {
                        override fun doInBackground(vararg params: String?) : String {
                            println("[voca] loading from GS")
                            val reader: FullReader
                            try {
                                reader = DictionaryGoogleSheetReader.greek()
                            } catch (ex: RuntimeException) {
                                return if (ex.message == null) "no message1?" else ex.message!!
                            }

                            return refreshDictionary(reader)
                        }

                        override fun onPostExecute(result: String) {
                            showDialog = false
                            errorMessage = result
                        }
                    }
                    showDialog = true
                    LoadFromGS().execute()

                }) {
                    Text("Refresh dictionary")
                }
                Text(errorMessage, modifier = Modifier.padding(4.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp).fillMaxWidth()
            ) {
                Button(modifier = Modifier.padding(4.dp), onClick = {
                    deleteAll()
                }) {
                    Text("Clear local files")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp).fillMaxWidth()
            ) {
                Button(modifier = Modifier.padding(4.dp), onClick = {
                    try {
                        val count = itemCount.toInt()
                        if (count > 0) {
                            globalConfiguration.itemCount = count
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
        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    private fun deleteAll() {
        val dir = getDictionaryDirectory(this.filesDir)
        AllCsvWriter().deleteAll(dir, "greek")
    }

    private fun refreshDictionary(reader: FullReader) : String {
        globalDictionary = null
        val libraryDico = getGlobalDictionary()
        globalDictionary = null

        val dir = getDictionaryDirectory(this.filesDir)
        println("[voca] writing to internal: " + dir.absolutePath)
        try {
            AllCsvWriter().writeDiff(reader, dir, libraryDico)
            println("[voca] reloading dictionary")
            getGlobalDictionary(this.filesDir)
            return "Done"
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
            return if (ex.message == null) "no message2? to " + dir.absolutePath else ex.message!!
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
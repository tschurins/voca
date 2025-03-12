package jal.voca.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import jal.voca.app.ui.theme.VocaTheme
import jal.voca.lang.Dictionary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            load()
            keepSplashScreen = false
        }
        globalConfiguration.readFrom(this.filesDir)

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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(4.dp).fillMaxSize()
        ) {
            Button(modifier = buttonModifier, onClick = { goToCategoryMenu() }) {
                Text("Word Categories")
            }
            Button(modifier = buttonModifier, onClick = { goToUnitMenu() }) {
                Text("Units")
            }
            if (!globalFavorites.isEmpty()) {
                Button(modifier = buttonModifier, onClick = { startFavoritesQuiz(dictionary, true) }) {
                    Text("Favorites -> " + dictionary.translationLanguage.name)
                }
                Button(modifier = buttonModifier, onClick = { startFavoritesQuiz(dictionary, false) }) {
                    Text("Favorites -> " + dictionary.wordLanguage.name)
                }
            }


            Spacer(Modifier.weight(1f))
            Button(modifier = buttonModifier, onClick = { goToConfig() }) {
                Text("Configuration")
            }
        }
    }

    private fun load() {
        getGlobalDictionary(this.filesDir)
        FavoritesLoader(this).loadFavorites()
    }

    private fun goToCategoryMenu() {
        globalSortOptions.numericPattern = null
        globalCategoryLoader = { dico, sort -> dico.getCategories().toSortedMap(sort.getComparator()) }
        val intent = Intent(this, CategoryMenuActivity::class.java)
        startActivity(intent)
    }

    private fun goToUnitMenu() {
        globalSortOptions.numericPattern = "S(\\d+)U(\\d+)"
        globalCategoryLoader = { dico, sort -> dico.getUnits().toSortedMap(sort.getComparator()) }
        val intent = Intent(this, CategoryMenuActivity::class.java)
        startActivity(intent)
    }

    private fun startFavoritesQuiz(dictionary: Dictionary, targetToBase: Boolean) {
        globalCategoryLoader = null
        val favoritesCategory = globalFavorites.getFavoritesCategory(dictionary)
        startQuiz(this, targetToBase, favoritesCategory)
    }

    private fun goToConfig() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuPreview() {
        globalFavorites.setFavorite("Q?", true)
        val dictionary = getGlobalDictionary()
        VocaTheme {
            Menu(dictionary)
        }
    }
}
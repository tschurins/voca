package jal.voca.app

import jal.voca.lang.Dictionary
import jal.voca.quiz.QuizContext

var globalDictionary: Dictionary? = null
var globalContext : QuizContext? = null

val configuration = AppConfiguration()

data class AppConfiguration(var itemCount: Int = 10)
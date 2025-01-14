package jal.voca.ui

import jal.voca.lang.*
import jal.voca.lang.io.DictionaryCsvReader
import jal.voca.quiz.*

import javafx.application.Application
import javafx.event.*
import javafx.geometry.Insets
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color

fun main() {
    QuizApp(false).main()
}


class QuizApp : Application {
    private val dictionary: Dictionary
    private val css: String

    constructor() {
        dictionary = DictionaryCsvReader().readGreekDictionary()
        css = this::class.java.getResource("styles.css").toExternalForm()
    }

    constructor(b: Boolean) {
        dictionary = Dictionary(English(), English(), listOf())
        css = this::class.java.getResource("styles.css").toExternalForm()
    }

    fun main() {
        Application.launch()
    }

    override fun start(stage: Stage) {
        stage.scene = startScene(stage)
        stage.width = 640.0
        stage.height = 480.0
        stage.show()
    }

    private fun startScene(stage: Stage): Scene {
        val toTranslationButtons = VBox()
        toTranslationButtons.spacing = 10.0
        val toTranslation = Button(dictionary.wordLanguage.name + " -> " + dictionary.translationLanguage.name)
        toTranslation.maxWidth = 10_000_000.0
        toTranslation.setOnAction { installQuiz(stage, Quiz.newQuiz(dictionary, null, true), dictionary.translationLanguage) }
        toTranslationButtons.children.add(toTranslation)

        val fromTranslationButtons = VBox()
        fromTranslationButtons.spacing = 10.0
        val fromTranslation = Button(dictionary.translationLanguage.name + " -> " + dictionary.wordLanguage.name)
        fromTranslation.maxWidth = 10_000_000.0
        fromTranslation.setOnAction { installQuiz(stage, Quiz.newQuiz(dictionary, null, false), dictionary.wordLanguage) }
        fromTranslationButtons.children.add(fromTranslation)

        for (category in dictionary.categories) {
            val w2t = Button(category.name)
            w2t.maxWidth = 10_000_000.0
            w2t.setOnAction { installQuiz(stage, Quiz.newQuiz(dictionary, category, true), dictionary.translationLanguage) }
            toTranslationButtons.children.add(w2t)

            val t2w = Button(category.name)
            t2w.maxWidth = 10_000_000.0
            t2w.setOnAction { installQuiz(stage, Quiz.newQuiz(dictionary, category, false), dictionary.wordLanguage) }
            fromTranslationButtons.children.add(t2w)
        }

        val hbox = HBox(toTranslationButtons, fromTranslationButtons)
        hbox.padding = Insets(15.0, 12.0, 15.0, 12.0)
        hbox.spacing = 10.0
        val scrollPane = ScrollPane(hbox)
        scrollPane.setFitToWidth(true)
        return configure(Scene(scrollPane))
    }

    private fun installQuiz(stage: Stage, quiz: Quiz, answerLanguage: Language) {
        playNextQuizItem(stage, QuizContext(quiz, answerLanguage))
    }

    private fun playNextQuizItem(stage: Stage, context: QuizContext) {
        val currentItem = context.nextItem()
        stage.scene = questionPanel(
            currentItem.question,
            { onAnswer(stage, context, currentItem, it) },
            { stage.scene = resultPanel(stage, context) }
        )
    }

    private fun questionPanel(question: String, onAnswer: (answer: String) -> Unit, backAction: () -> Unit) : Scene {
        val mainLayout = BorderPane()
        mainLayout.padding = Insets(15.0, 12.0, 15.0, 12.0)

        val label = Label(question)
        val answer = TextField()
        val vbox = VBox(label, answer)
        vbox.spacing = 10.0
        mainLayout.center = vbox

        val ok = Button("OK")
        ok.setOnAction { onAnswer(answer.text) }
        ok.setDefaultButton(true)
        val back = Button("Back")
        back.setOnAction { backAction() }
        val buttons = HBox(ok, back)
        buttons.spacing = 10.0
        mainLayout.bottom = buttons
        
        return configure(Scene(mainLayout))
    }

    private fun onAnswer(stage: Stage, context: QuizContext, currentItem: QuizItem, answer: String) {
        val convertedAnswer = context.setAnswer(answer)
        val result = context.checkSuccess()
        stage.scene = answerPanel(
            currentItem,
            result,
            convertedAnswer,
            { 
                if (context.hasNextItem()) {
                    playNextQuizItem(stage, context)
                } else {
                    stage.scene = resultPanel(stage, context)
                }
            },
            { stage.scene = resultPanel(stage, context) }
        )
    }

    private fun answerPanel(item: QuizItem, result: AnswerResult, givenAnswer: String, okAction: () -> Unit, backAction: () -> Unit) : Scene {
        val mainLayout = BorderPane()
        mainLayout.padding = Insets(15.0, 12.0, 15.0, 12.0)

        val question = Label(item.question)
        val moreInfo = Label(item.moreInfo)
        val vbox: VBox
        val answer = Label(item.answer)
        when (result) {
            AnswerResult.SUCCESS -> {
                answer.textFill = Color.color(0.0, 0.65, 0.0)
                vbox = VBox(question, answer, moreInfo)
            }
            AnswerResult.SUCCESS_HOMOPHONE -> {
                val givenAnswerL = Label(givenAnswer)
                givenAnswerL.textFill = Color.color(0.65, 0.65, 0.0)
                vbox = VBox(question, givenAnswerL, answer, moreInfo)
            }
            AnswerResult.FAILURE -> {
                val givenAnswerL = Label(givenAnswer)
                givenAnswerL.textFill = Color.color(1.0, 0.0, 0.0)
                vbox = VBox(question, givenAnswerL, answer, moreInfo)
            }
        }
        vbox.spacing = 10.0
        mainLayout.center = vbox

        val ok = Button("OK")
        ok.setOnAction { okAction() }
        ok.setDefaultButton(true)
        val back = Button("Back")
        back.setOnAction { backAction() }
        val buttons = HBox(ok, back)
        buttons.spacing = 10.0
        mainLayout.bottom = buttons

        return configure(Scene(mainLayout))
    }

    private fun resultPanel(stage: Stage, context: QuizContext): Scene {
        val mainLayout = BorderPane()
        mainLayout.padding = Insets(15.0, 12.0, 15.0, 12.0)
        mainLayout.top = Label("" + context.score.score + " / " + context.score.total)

        val failures = VBox()
        for (failure in context.failures) {
            failures.children.add(Label(failure.question + " -> " + failure.answer))
        }
        mainLayout.center = failures

        val back = Button("Back")
        back.setOnAction { stage.scene = startScene(stage) }
        val buttons = HBox(back)
        buttons.spacing = 10.0
        mainLayout.bottom = buttons

        return configure(Scene(mainLayout))
    }

    private fun configure(scene: Scene) : Scene {
        scene.getStylesheets().add(css);
        return scene
    }
}
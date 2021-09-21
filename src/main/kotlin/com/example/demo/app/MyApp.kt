package com.example.demo.app

import com.example.demo.view.MainView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MyApp: App(MainView::class, Styles::class){

    private val appController: AppController by inject()

    override fun start(stage: Stage) {
        //appController.fill()
        //appController.test()
        appController.testDiagram()
        //appController.testIntersection()
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
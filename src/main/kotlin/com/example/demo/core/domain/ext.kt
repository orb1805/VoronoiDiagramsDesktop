package com.example.demo.core.domain

import tornadofx.lineTo

public fun javafx.scene.shape.Path.lineToPoint(path: javafx.scene.shape.Path, point: Point): javafx.scene.shape.Path {
    return path.lineTo(point.x, -point.y)
}

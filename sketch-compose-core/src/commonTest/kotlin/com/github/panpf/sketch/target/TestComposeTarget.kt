package com.github.panpf.sketch.target

import androidx.compose.ui.graphics.painter.Painter

class TestComposeTarget : ComposeTarget {

    override var painter: Painter? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestComposeTarget
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return "TestComposeTarget"
    }
}
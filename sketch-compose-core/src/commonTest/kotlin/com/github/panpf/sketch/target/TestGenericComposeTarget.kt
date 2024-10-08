package com.github.panpf.sketch.target

import androidx.compose.ui.graphics.painter.Painter

class TestGenericComposeTarget(override val fitScale: Boolean = true) : GenericComposeTarget() {

    override var painter: Painter? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestGenericComposeTarget
        if (fitScale != other.fitScale) return false
        return true
    }

    override fun hashCode(): Int {
        return fitScale.hashCode()
    }

    override fun toString(): String {
        return "TestComposeTarget(fitScale=$fitScale)"
    }
}
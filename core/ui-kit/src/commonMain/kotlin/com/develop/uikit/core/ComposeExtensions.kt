package com.develop.uikit.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier =
    composed {
        this.then(
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick() }
            )
        )
    }

private fun Modifier.conditional(
    condition: Boolean,
    ifTrue: (Modifier.() -> Modifier)?,
    ifFalse: (Modifier.() -> Modifier)?,
): Modifier {
    return if (condition) {
        if (ifTrue != null) then(ifTrue(Modifier)) else this
    } else {
        if (ifFalse != null) then(ifFalse(Modifier)) else this
    }
}

fun Modifier.conditional(
    condition: Boolean,
    ifTrue: (Modifier.() -> Modifier)? = null,
): Modifier = this.conditional(condition, ifTrue, null)

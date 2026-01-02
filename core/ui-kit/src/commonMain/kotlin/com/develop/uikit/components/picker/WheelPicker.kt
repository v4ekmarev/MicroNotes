/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Adapted for MicroNotes project.
 */

package com.develop.uikit.components.picker

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirstOrNull
import com.develop.uikit.components.LocalContainerColor
import com.develop.uikit.components.ProvideTextStyle
import com.develop.uikit.core.InternalApi
import com.develop.uikit.core.LocalContentColor
import com.develop.uikit.core.haptic.HapticFeedbackTypes
import com.develop.uikit.core.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Stable
class PickerState(
    internal val infinite: Boolean = false,
    internal val initiallySelectedItemIndex: Int = 0,
) : ScrollableState {

    internal val lazyListState: LazyListState = LazyListState(
        firstVisibleItemIndex = if (infinite)
            INFINITE_OFFSET + initiallySelectedItemIndex
        else initiallySelectedItemIndex
    )

    internal val selectedItemHeight: Int by derivedStateOf {
        selectedItem?.size ?: 0
    }

    private val selectedItem by derivedStateOf {
        with(layoutInfo) {
            visibleItemsInfo.fastFirstOrNull {
                it.offset + it.size - viewportStartOffset > viewportSize.height / 2
            }
        }
    }

    internal var changedProgrammatically by mutableStateOf(false)

    val selectedItemIndex: Int by derivedStateOf {
        (if (infinite) {
            selectedItem?.index?.minus(INFINITE_OFFSET)
        } else selectedItem?.index) ?: initiallySelectedItemIndex
    }

    fun currentSelectedItem(itemsCount: Int): Int {
        return selectedItemIndex.modSign(itemsCount)
    }

    fun selectedItemState(itemsCount: Int): State<Int> {
        return derivedStateOf { selectedItemIndex.modSign(itemsCount) }
    }

    @Composable
    fun selectedItemIndex(itemsCount: Int): Int {
        return remember(itemsCount) {
            selectedItemState(itemsCount)
        }.value
    }

    override val canScrollBackward: Boolean
        get() = lazyListState.canScrollBackward

    override val canScrollForward: Boolean
        get() = lazyListState.canScrollForward

    override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float {
        return lazyListState.dispatchRawDelta(delta)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        changedProgrammatically = true
        lazyListState.scroll(scrollPriority, block)
    }

    val layoutInfo: LazyListLayoutInfo get() = lazyListState.layoutInfo
    val interactionSource: InteractionSource get() = lazyListState.interactionSource

    suspend fun scrollToItem(index: Int) {
        lazyListState.scrollToItem(if (infinite) INFINITE_OFFSET + index else index)
    }

    suspend fun animateScrollToItem(index: Int) =
        lazyListState.animateScrollToItem(index)

    companion object {
        fun Saver(): Saver<PickerState, *> = Saver(
            save = { listOf(it.infinite, it.selectedItemIndex) },
            restore = {
                PickerState(
                    infinite = it[0] as Boolean,
                    initiallySelectedItemIndex = it[1] as Int
                )
            }
        )
    }
}

@Composable
fun rememberPickerState(
    infinite: Boolean = true,
    initiallySelectedItemIndex: Int = 0
): PickerState {
    return rememberSaveable(
        initiallySelectedItemIndex,
        saver = PickerState.Saver()
    ) {
        PickerState(
            infinite = infinite,
            initiallySelectedItemIndex = initiallySelectedItemIndex
        )
    }
}

typealias PickerIndicator = DrawScope.(itemHeight: Float) -> Unit

@OptIn(InternalApi::class)
@Composable
fun <T : Any> WheelPicker(
    state: PickerState,
    items: List<T>,
    height: Dp = PickerDefaults.Height,
    modifier: Modifier = Modifier,
    indicator: PickerIndicator = PickerDefaults.indicator(),
    containerColor: Color = LocalContainerColor.current.takeOrElse {
        Theme.colorScheme.secondarySystemGroupedBackground
    },
    textStyle: TextStyle = PickerDefaults.textStyle,
    key: ((T) -> Any)? = null,
    withRotation: Boolean = false,
    rotationTransformOrigin: TransformOrigin = TransformOrigin.Center,
    enabled: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable (T) -> Unit
) {
    val density = LocalDensity.current
    val selectedItemHeight by remember { derivedStateOf { state.selectedItemHeight } }
    val paddingValues = remember(density, height, selectedItemHeight) {
        with(density) {
            PaddingValues(vertical = ((height.toPx() - selectedItemHeight) / 2).toDp())
        }
    }

    val haptic = LocalHapticFeedback.current

    var isInitial by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(100)
        isInitial = false
    }

    LaunchedEffect(state.selectedItemIndex(items.size)) {
        if (!isInitial && !state.changedProgrammatically) {
            haptic.performHapticFeedback(HapticFeedbackTypes.SelectionChanged)
        }
        state.changedProgrammatically = false
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            state.scrollToItem(state.selectedItemIndex.modSign(items.size))
        }
    }

    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalContentColor provides Theme.colorScheme.label.copy(alpha = .75f),
        LocalDensity provides Density(LocalDensity.current.density, 1f)
    ) {
        ProvideTextStyle(textStyle) {
            LazyColumn(
                modifier = modifier
                    .requiredHeight(height)
                    .background(containerColor)
                    .pickerForeground(
                        state = state,
                        containerColor = containerColor,
                    )
                    .pickerIndicator(state, indicator),
                state = state.lazyListState,
                contentPadding = paddingValues,
                userScrollEnabled = enabled,
                horizontalAlignment = horizontalAlignment,
                flingBehavior = rememberSnapFlingBehavior(state.lazyListState)
            ) {
                fun index(index: Int) =
                    if (state.infinite)
                        ((index - INFINITE_OFFSET) % items.size).let {
                            if (it >= 0) it else items.size - abs(it)
                        }
                    else index

                items(
                    count = if (state.infinite) Int.MAX_VALUE else items.size,
                    key = key?.run { { invoke(items[index(it)]) } }
                ) { index ->
                    Box(
                        modifier = Modifier
                            .heightIn(min = MinItemHeight)
                            .graphicsLayer {
                                if (withRotation) {
                                    rotationX = (15f * ((index - if (state.infinite) INFINITE_OFFSET else 0) -
                                            state.selectedItemIndex)).coerceIn(-60f, 60f)
                                    transformOrigin = rotationTransformOrigin
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    scope.launch {
                                        state.animateScrollToItem(index)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        content(items[index(index)])
                    }
                }
            }
        }
    }
}

@Composable
private fun Modifier.pickerForeground(
    state: PickerState,
    containerColor: Color,
): Modifier {
    val halfTransparentContainerColor = remember(containerColor) {
        containerColor.copy(alpha = .5f)
    }

    val transparentContainerColor = remember(containerColor) {
        containerColor.copy(alpha = 0f)
    }

    val topBrush = remember(containerColor, halfTransparentContainerColor, transparentContainerColor) {
        Brush.verticalGradient(
            0f to containerColor,
            .05f to containerColor,
            .25f to halfTransparentContainerColor,
            1f to transparentContainerColor
        )
    }

    val bottomBrush = remember(containerColor, halfTransparentContainerColor, transparentContainerColor) {
        Brush.verticalGradient(
            0f to transparentContainerColor,
            .75f to halfTransparentContainerColor,
            .95f to containerColor,
            1f to containerColor,
        )
    }

    return drawWithContent {
        drawContent()

        val itemHeight = state.selectedItemHeight
        val height = (size.height - itemHeight) / 2

        drawRect(
            topLeft = Offset.Zero,
            size = size.copy(height = height),
            brush = topBrush
        )
        drawRect(
            topLeft = Offset(0f, height + itemHeight),
            size = size.copy(height = height),
            brush = bottomBrush
        )
    }
}

fun Modifier.pickerIndicator(
    state: PickerState,
    indicator: PickerIndicator
) = drawWithContent {
    drawContent()
    translate(0f, (size.height - state.selectedItemHeight) / 2) {
        indicator(state.selectedItemHeight.toFloat())
    }
}

@Immutable
object PickerDefaults {

    val Height = 220.dp

    val textStyle: TextStyle
        @Composable
        get() = Theme.typography.title2.copy(
            letterSpacing = (-1).sp,
        )

    @Composable
    fun indicatorOld(
        color: Color = Theme.colorScheme.separator,
    ): DrawScope.(itemHeight: Float) -> Unit {
        return {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f)
            )
            drawLine(
                color = color,
                start = Offset(0f, it),
                end = Offset(size.width, it)
            )
        }
    }

    @Composable
    fun indicator(
        color: Color = PickerTokens.IndicatorColor,
        shape: Shape = PickerTokens.IndicatorShape,
        paddingValues: PaddingValues = PickerTokens.IndicatorPaddingValues
    ): PickerIndicator {
        return {
            val startPadding = paddingValues.calculateStartPadding(layoutDirection).toPx()
            val endPadding = paddingValues.calculateEndPadding(layoutDirection).toPx()
            val size = Size(size.width - startPadding - endPadding, it)

            translate(left = startPadding) {
                drawOutline(
                    shape.createOutline(size, layoutDirection, this),
                    color = color,
                )
            }
        }
    }
}

internal object PickerTokens {
    val IndicatorColor: Color
        @Composable get() = Theme.colorScheme.label.copy(alpha = .05f)

    val IndicatorPaddingValues: PaddingValues = PaddingValues(horizontal = 10.dp)

    val IndicatorShape: CornerBasedShape
        @Composable get() = Theme.shapes.small
}

internal val PickerMaxWidth = 500.dp
private val MinItemHeight = 32.dp
private const val INFINITE_OFFSET = Int.MAX_VALUE / 2

internal fun Int.modSign(o: Int): Int {
    return mod(o).let {
        if (it >= 0) it else this - it
    }
}

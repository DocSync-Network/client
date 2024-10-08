package org.dvir.docsync.doc.presentation.doc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.dvir.docsync.core.ui.BackgroundColor
import org.dvir.docsync.core.ui.SurfaceColor
import org.dvir.docsync.doc.domain.model.Character
import org.dvir.docsync.doc.domain.model.CharacterConfig


@Composable
fun CustomTextEditor(
    textFieldValue: MutableState<TextFieldValue>,
    savedSelection: MutableState<TextRange>,
    onTextAdd: (Character) -> Unit,
    onTextRemove: () -> Unit,
    onCursorChanged: (Int) -> Unit,
    onSelectionChanged: (IntRange) -> Unit,
    config: CharacterConfig,
    focusRequester: FocusRequester,
) {
    BasicTextField(
        value = textFieldValue.value,
        onValueChange = { newValue ->
            val oldText = textFieldValue.value.text
            val newText = newValue.text

            if (newValue.selection.start != newValue.selection.end) {
                val start = newValue.selection.start
                val end = newValue.selection.end

                val selectionRange = if (start < end) {
                    start..end
                } else {
                    end..start
                }

                savedSelection.value = TextRange(selectionRange.first, selectionRange.last)

                onSelectionChanged(selectionRange)
            } else {
                savedSelection.value = TextRange(newValue.selection.start)
                onCursorChanged(newValue.selection.start)
            }


            if (newText.length > oldText.length) {
                val cursorPosition = newValue.selection.start
                val character = when (val addedChar = newText[cursorPosition - 1]) {
                    '\n' -> Character.BreakLine
                    ' ' -> Character.Space
                    else -> Character.Visible(addedChar, config)
                }
                onTextAdd(character)
            } else if (newText.length < oldText.length) {
                onTextRemove()
            }
        },
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .focusRequester(focusRequester)
            .background(BackgroundColor)
            .padding(
                top = 32.dp,
            )
            .border(1.dp, SurfaceColor)
            .padding(24.dp),
    )
}

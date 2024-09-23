package org.dvir.docsync.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isHintVisible: Boolean,
    icon: ImageVector,
    isPassword: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {

    var isTextVisible by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusRequester.requestFocus() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 20.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = hint,
                tint = TextColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = if(isPassword) KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ) else KeyboardOptions.Default,
                    singleLine = true,
                    textStyle = TextStyle(
                        //fontFamily = fontFamily,
                        fontWeight = FontWeight.Light,
                        color = TextColor,
                        fontSize = MaterialTheme.typography.body1.fontSize
                    ),
                    visualTransformation = if(isPassword && !isTextVisible)
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                    modifier = Modifier
                        .onFocusChanged {
                            onFocusChange(it)
                        }
                        .focusRequester(focusRequester)
                )
                if(isHintVisible) {
                    Text(
                        text = hint,
                        color = TextColor,
                        // fontFamily = fontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                }
            }
            if(isPassword) {
                Icon(
                    imageVector = if(isTextVisible) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = if(isTextVisible) "Hide password"
                    else "Show password",
                    tint = TextColor,
                    modifier = Modifier.clickable {
                        isTextVisible = !isTextVisible
                    }
                )
            }
        }
    }
}
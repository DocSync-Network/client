package org.dvir.docsync.doc.presentation.doc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dvir.docsync.core.config.UserData
import org.dvir.docsync.core.navigation.Route
import org.dvir.docsync.core.ui.BackgroundColor
import org.dvir.docsync.core.ui.ErrorColor
import org.dvir.docsync.core.ui.PrimaryColor
import org.dvir.docsync.core.ui.SecondaryColor
import org.dvir.docsync.core.ui.TextColor
import org.dvir.docsync.core.ui.UiEvent
import org.dvir.docsync.doc.domain.cursor.CursorData
import org.dvir.docsync.doc.domain.model.CharacterConfig
import org.dvir.docsync.doc.domain.model.Document
import org.dvir.docsync.doc.domain.utils.Colors
import org.dvir.docsync.doc.domain.utils.CursorPosition.indexToPosition
import org.dvir.docsync.doc.presentation.components.DefaultDialog
import org.dvir.docsync.doc.presentation.doc.components.CustomTextEditor
import org.dvir.docsync.doc.presentation.doc.viewmodel.DocEvent
import org.dvir.docsync.doc.presentation.doc.viewmodel.DocViewModel
import org.dvir.docsync.doc.presentation.doc.viewmodel.EditEvent

@Composable
fun DocScreen(
    viewModel: DocViewModel,
    document: Document,
    onNavigateBack: () -> Unit
) {
    var snackBarColor by remember { mutableStateOf(PrimaryColor) }
    val snackBarHostState = remember { SnackbarHostState() }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    snackBarColor = ErrorColor
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEvent.ShowSnackBar -> {
                    snackBarColor = PrimaryColor
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEvent.Navigate -> {
                    if (event.to is Route.Home) {
                        onNavigateBack()
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = snackBarColor,
                        actionColor = BackgroundColor
                    )
                }
            )
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onDocEvent(
                                    DocEvent.CloseDocument
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Go Back",
                                tint = BackgroundColor
                            )
                        }
                        Text(
                            text = document.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = BackgroundColor
                        )
                        IconButton(
                            onClick = {
                                viewModel.onDocEvent(
                                    DocEvent.SaveDocument
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                                tint = BackgroundColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.onDocEvent(
                                        DocEvent.OpenColorDialog
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatColorText,
                                    contentDescription = "Change Color",
                                    tint = BackgroundColor
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEditEvent(
                                        EditEvent.ToggleUnderLine
                                    )
                                },
                                modifier = Modifier.background(
                                    color = if (viewModel.isUnderlined)
                                        SecondaryColor
                                    else PrimaryColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatUnderlined,
                                    contentDescription = "Underline",
                                    tint = if (viewModel.isUnderlined)
                                        TextColor
                                    else BackgroundColor
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEditEvent(
                                        EditEvent.ToggleItalic
                                    )
                                },
                                modifier = Modifier.background(
                                    color = if (viewModel.isItalic)
                                        SecondaryColor
                                    else PrimaryColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatItalic,
                                    contentDescription = "Italic",
                                    tint = if (viewModel.isItalic)
                                        TextColor
                                    else BackgroundColor
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEditEvent(
                                        EditEvent.ToggleBold
                                    )
                                },
                                modifier = Modifier.background(
                                    color = if (viewModel.isBold)
                                        SecondaryColor
                                    else PrimaryColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatBold,
                                    contentDescription = "Bold",
                                    tint = if (viewModel.isBold)
                                        TextColor
                                    else BackgroundColor
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.onEditEvent(
                                        EditEvent.DecreaseFontSize
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease Font Size",
                                    tint = BackgroundColor
                                )
                            }
                            Text(
                                text = viewModel.fontSize.toString(),
                                fontSize = 16.sp,
                                color = BackgroundColor
                            )
                            IconButton(
                                onClick = {
                                    viewModel.onEditEvent(
                                        EditEvent.IncreaseFontSize
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase Font Size",
                                    tint = BackgroundColor
                                )
                            }
                        }
                    }
                    if (UserData.username == document.owner) {
                        Text(
                            text = "Access",
                            fontSize = 16.sp,
                            color = BackgroundColor,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                viewModel.onDocEvent(
                                    DocEvent.OpenAccessDialog
                                )
                            }.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomTextEditor(
                onTextAdd = {
                    viewModel.onDocEvent(
                        DocEvent.AddCharacter(it)
                    )
                },
                onTextRemove = {
                    viewModel.onDocEvent(
                        DocEvent.RemoveCharacter
                    )
                },
                onCursorChanged = {
                    viewModel.onDocEvent(
                        DocEvent.UpdateCursor(
                            CursorData(
                                start = indexToPosition(document.content, it)
                            )
                        )
                    )
                },
                onSelectionChanged = {
                    viewModel.onDocEvent(
                        DocEvent.UpdateCursor(
                            CursorData(
                                start = indexToPosition(document.content, it.first),
                                end = indexToPosition(document.content, it.last)
                            )
                        )
                    )
                },
                config = CharacterConfig(
                    isBold = viewModel.isBold,
                    isItalic = viewModel.isItalic,
                    isUnderlined = viewModel.isUnderlined,
                    color = Colors.hexFromColor(viewModel.color),
                    fontSize = viewModel.fontSize
                ),
                focusRequester = focusRequester,
                textFieldValue = viewModel.textFieldValue,
                savedSelection = viewModel.savedSelection
            )
        }

        if (viewModel.isAccessDialogOpen) {
            DefaultDialog(
                header = "Add Access",
                label = "Username",
                primaryButtonText = "Add",
                textFieldValue = viewModel.accessDialogTextFieldValue,
                onTextFieldValueChange = { value ->
                    viewModel.onDocEvent(
                        DocEvent.OnAccessTextFieldValueChange(value)
                    )
                },
                onDismiss = {
                    viewModel.onDocEvent(
                        DocEvent.CloseAccessDialog
                    )
                },
                onAccept = {
                    viewModel.onDocEvent(
                        DocEvent.AddAccess
                    )
                }
            )
        }
    }
}

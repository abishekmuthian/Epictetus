/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abishekmuthian.epictetus.ui.common.chat

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abishekmuthian.epictetus.R
import com.abishekmuthian.epictetus.data.Task
import com.abishekmuthian.epictetus.ui.common.getTaskIconColor
import com.abishekmuthian.epictetus.ui.modelmanager.ModelManagerViewModel
import com.abishekmuthian.epictetus.ui.theme.bodyLargeNarrow

private const val TAG = "AGMessageInputText"

/**
 * Composable function to display a text input field for composing chat messages.
 *
 * This function renders a row containing a text field for message input and a send button. It
 * handles message composition, input validation, and sending messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputText(
  task: Task,
  modelManagerViewModel: ModelManagerViewModel,
  curMessage: String,
  isResettingSession: Boolean,
  inProgress: Boolean,
  modelInitializing: Boolean,
  @StringRes textFieldPlaceHolderRes: Int,
  onValueChanged: (String) -> Unit,
  onSendMessage: (List<ChatMessage>) -> Unit,
  modelPreparing: Boolean = false,
  onOpenPromptTemplatesClicked: () -> Unit = {},
  onStopButtonClicked: () -> Unit = {},
  showPromptTemplatesInMenu: Boolean = false,
  showStopButtonWhenInProgress: Boolean = false,
) {
  val modelManagerUiState by modelManagerViewModel.uiState.collectAsState()
  var showAddContentMenu by remember { mutableStateOf(false) }
  var showTextInputHistorySheet by remember { mutableStateOf(false) }


  Column {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.heightIn(min = 76.dp)) {
            Box(contentAlignment = Alignment.CenterStart) {
              // A plus button to show a popup menu to add stuff to the chat.
              IconButton(
                enabled = !inProgress && !isResettingSession,
                onClick = { showAddContentMenu = true },
                modifier = Modifier.offset(x = 16.dp).alpha(0.8f),
              ) {
                Icon(
                  Icons.Rounded.Add,
                  contentDescription = stringResource(R.string.cd_add_content_icon),
                  modifier = Modifier.size(28.dp),
                )
              }
              Row(
                modifier =
                  Modifier.fillMaxWidth()
                    .padding(12.dp)
                    .border(
                      1.dp,
                      MaterialTheme.colorScheme.outlineVariant,
                      RoundedCornerShape(28.dp),
                    ),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                DropdownMenu(
                  expanded = showAddContentMenu,
                  onDismissRequest = { showAddContentMenu = false },
                ) {
                  // Prompt templates.
                  if (showPromptTemplatesInMenu) {
                    DropdownMenuItem(
                      text = {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                          Icon(Icons.Rounded.PostAdd, contentDescription = null)
                          Text("Prompt templates")
                        }
                      },
                      onClick = {
                        onOpenPromptTemplatesClicked()
                        showAddContentMenu = false
                      },
                    )
                  }
                  // Prompt history.
                  DropdownMenuItem(
                    text = {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                      ) {
                        Icon(Icons.Rounded.History, contentDescription = null)
                        Text("Input history")
                      }
                    },
                    onClick = {
                      showAddContentMenu = false
                      showTextInputHistorySheet = true
                    },
                  )
                }

                // Text field.
                val cdPromptInput = stringResource(R.string.cd_prompt_input_text_field)
                TextField(
                  value = curMessage,
                  minLines = 1,
                  maxLines = 3,
                  onValueChange = onValueChanged,
                  colors =
                    TextFieldDefaults.colors(
                      unfocusedContainerColor = Color.Transparent,
                      focusedContainerColor = Color.Transparent,
                      focusedIndicatorColor = Color.Transparent,
                      unfocusedIndicatorColor = Color.Transparent,
                      disabledIndicatorColor = Color.Transparent,
                      disabledContainerColor = Color.Transparent,
                    ),
                  textStyle = bodyLargeNarrow,
                  modifier =
                    Modifier.weight(1f).padding(start = 36.dp).semantics {
                      contentDescription = cdPromptInput
                    },
                  placeholder = { Text(stringResource(textFieldPlaceHolderRes)) },
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (inProgress && showStopButtonWhenInProgress) {
                  if (!modelInitializing && !modelPreparing) {
                    IconButton(
                      onClick = onStopButtonClicked,
                      colors =
                        IconButtonDefaults.iconButtonColors(
                          containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                    ) {
                      Icon(
                        Icons.Rounded.Stop,
                        contentDescription = stringResource(R.string.cd_stop_icon),
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    }
                  }
                }
                // Send button. Only shown when text is not empty.
                else if (curMessage.isNotEmpty()) {
                  IconButton(
                    enabled = !inProgress && !isResettingSession,
                    onClick = {
                      var message = curMessage.trim()
                      onSendMessage(
                        listOf(ChatMessageText(content = message, side = ChatSide.USER))
                      )
                    },
                    colors =
                      IconButtonDefaults.iconButtonColors(
                        containerColor = getTaskIconColor(task = task)
                      ),
                  ) {
                    Icon(
                      Icons.AutoMirrored.Rounded.Send,
                      contentDescription = stringResource(R.string.cd_send_prompt_icon),
                      modifier = Modifier.offset(x = 2.dp),
                      tint = Color.White,
                    )
                  }
                }
                Spacer(modifier = Modifier.width(4.dp))
              }
            }
    }
  }

  // A bottom sheet to show the text input history to pick from.
  if (showTextInputHistorySheet) {
    TextInputHistorySheet(
      history = modelManagerUiState.textInputHistory,
      onDismissed = { showTextInputHistorySheet = false },
      onHistoryItemClicked = { item ->
        onSendMessage(
          listOf(ChatMessageText(content = item, side = ChatSide.USER))
        )
        modelManagerViewModel.promoteTextInputHistoryItem(item)
      },
      onHistoryItemDeleted = { item -> modelManagerViewModel.deleteTextInputHistory(item) },
      onHistoryItemsDeleteAll = { modelManagerViewModel.clearTextInputHistory() },
    )
  }

}


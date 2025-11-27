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

package com.abishekmuthian.epictetus.ui.llmchat

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Mms
import androidx.compose.runtime.Composable
import java.io.File
import java.io.FileOutputStream
import com.abishekmuthian.epictetus.R
import com.abishekmuthian.epictetus.customtasks.common.CustomTask
import com.abishekmuthian.epictetus.customtasks.common.CustomTaskDataForBuiltinTask
import com.abishekmuthian.epictetus.data.BuiltInTaskId
import com.abishekmuthian.epictetus.data.Category
import com.abishekmuthian.epictetus.data.Model
import com.abishekmuthian.epictetus.data.Task
import com.abishekmuthian.epictetus.data.createLlmChatConfigs
import com.abishekmuthian.epictetus.data.Accelerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

////////////////////////////////////////////////////////////////////////////////////////////////////
// AI Chat.

class LlmChatTask @Inject constructor() : CustomTask {
  override val task: Task =
    Task(
      id = BuiltInTaskId.LLM_CHAT,
      label = "Chat with Epictetus",
      category = Category.LLM,
      icon = Icons.Outlined.Forum,
      models = mutableListOf(createHardcodedEpictetusModel()),
      description = "Chat with on-device large language models",
      docUrl = "https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android",
      sourceCodeUrl =
        "https://github.com/google-ai-edge/gallery/blob/main/Android/src/app/src/main/java/com/google/ai/edge/gallery/ui/llmchat/LlmChatModelHelper.kt",
      textInputPlaceHolderRes = R.string.text_input_placeholder_llm_chat,
    )

  private fun createHardcodedEpictetusModel(): Model {
    val configs = createLlmChatConfigs(
      defaultMaxToken = 512,
      defaultTopK = 20,
      defaultTopP = 0.90f,
      defaultTemperature = 0.7f,
      accelerators = listOf(Accelerator.CPU, Accelerator.GPU)
    )

    return Model(
      name = "epictetus-gemma-3-270m-it",
      displayName = "Epictetus Gemma 3 270M",
      //description = "A philosophical chat model based on Epictetus teachings, built on Gemma 3 270M",
      localFileRelativeDirPathOverride = "models/",
      downloadFileName = "epictetus-gemma-3-270m-it.task",
      sizeInBytes = 270000000L, // Approximate size
      version = "local",
      configs = configs,
      bestForTaskIds = listOf(BuiltInTaskId.LLM_CHAT),
      showRunAgainButton = true,
      showBenchmarkButton = false,
      llmSupportImage = false,
      llmSupportAudio = false
    )
  }

  private fun copyModelFromAssets(context: Context, model: Model): Boolean {
    return try {
      val assetFileName = "models/${model.downloadFileName}"
      val externalDir = File(context.getExternalFilesDir(null), "models")

      // Create models directory if it doesn't exist
      if (!externalDir.exists()) {
        externalDir.mkdirs()
      }

      val targetFile = File(externalDir, model.downloadFileName)

      // Skip copying if file already exists
      if (targetFile.exists()) {
        return true
      }

      // Copy from assets to external storage
      context.assets.open(assetFileName).use { inputStream ->
        FileOutputStream(targetFile).use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }


  override fun initializeModelFn(
    context: Context,
    coroutineScope: CoroutineScope,
    model: Model,
    onDone: (String) -> Unit,
  ) {
    // Copy model from assets if it's the epictetus model
    if (model.name == "epictetus-gemma-3-270m-it") {
      val copySuccess = copyModelFromAssets(context, model)
      if (!copySuccess) {
        onDone("Failed to copy model from assets")
        return
      }
    }

    LlmChatModelHelper.initialize(
      context = context,
      model = model,
      supportImage = false,
      supportAudio = false,
      onDone = onDone,
    )
  }

  override fun cleanUpModelFn(
    context: Context,
    coroutineScope: CoroutineScope,
    model: Model,
    onDone: () -> Unit,
  ) {
    LlmChatModelHelper.cleanUp(model = model, onDone = onDone)
  }

  @Composable
  override fun MainScreen(data: Any) {
    val myData = data as CustomTaskDataForBuiltinTask
    LlmChatScreen(modelManagerViewModel = myData.modelManagerViewModel, navigateUp = myData.onNavUp)
  }
}

@Module
@InstallIn(SingletonComponent::class) // Or another component that fits your scope
internal object LlmChatTaskModule {
  @Provides
  @IntoSet
  fun provideTask(): CustomTask {
    return LlmChatTask()
  }
}


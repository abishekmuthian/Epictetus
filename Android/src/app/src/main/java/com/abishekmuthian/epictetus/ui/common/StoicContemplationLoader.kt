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

package com.abishekmuthian.epictetus.ui.common

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * A contemplative loading animation inspired by Stoic philosophy.
 * Features a central circle (representing wisdom/logos) surrounded by
 * three orbiting elements representing the Stoic virtues:
 * - Wisdom (σοφία)
 * - Justice (δικαιοσύνη)
 * - Courage/Fortitude (ἀνδρεία)
 * With a gentle breathing effect representing mindful contemplation.
 */
@Composable
fun StoicContemplationLoader(size: Dp = 32.dp) {
  val infiniteTransition = rememberInfiniteTransition(label = "stoic_contemplation")

  // Slow breathing effect for the center circle (wisdom/logos)
  val breathingScale by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = EaseInOut),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breathing"
  )

  // Slow rotation for virtue orbs
  val virtueRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = EaseInOut),
      repeatMode = RepeatMode.Restart
    ),
    label = "virtue_rotation"
  )

  // Gentle pulsing for virtue orbs
  val virtuePulse by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = EaseInOut),
      repeatMode = RepeatMode.Reverse
    ),
    label = "virtue_pulse"
  )

  val primaryColor = MaterialTheme.colorScheme.primary
  val secondaryColor = MaterialTheme.colorScheme.secondary
  val tertiaryColor = MaterialTheme.colorScheme.tertiary

  Box(
    modifier = Modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val center = this.center
      val radius = size.toPx() / 2f

      // Draw central wisdom circle (logos) with breathing effect
      drawCircle(
        color = primaryColor,
        radius = radius * 0.25f * breathingScale,
        center = center,
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
      )

      // Draw three virtue orbs orbiting the center
      rotate(degrees = virtueRotation, pivot = center) {
        // Wisdom virtue (σοφία) - top
        drawVirtueOrb(
          center = center,
          angle = 0f,
          orbitRadius = radius * 0.6f,
          orbRadius = radius * 0.08f * virtuePulse,
          color = primaryColor
        )

        // Justice virtue (δικαιοσύνη) - bottom right
        drawVirtueOrb(
          center = center,
          angle = 120f,
          orbitRadius = radius * 0.6f,
          orbRadius = radius * 0.08f * virtuePulse,
          color = secondaryColor
        )

        // Courage virtue (ἀνδρεία) - bottom left
        drawVirtueOrb(
          center = center,
          angle = 240f,
          orbitRadius = radius * 0.6f,
          orbRadius = radius * 0.08f * virtuePulse,
          color = tertiaryColor
        )
      }

      // Draw subtle connecting arcs representing harmony
      drawConnectingArcs(
        center = center,
        radius = radius * 0.45f,
        color = primaryColor.copy(alpha = 0.3f),
        breathingScale = breathingScale
      )
    }
  }
}

private fun DrawScope.drawVirtueOrb(
  center: androidx.compose.ui.geometry.Offset,
  angle: Float,
  orbitRadius: Float,
  orbRadius: Float,
  color: Color
) {
  val angleRad = angle * PI.toFloat() / 180f
  val orbCenter = androidx.compose.ui.geometry.Offset(
    x = center.x + cos(angleRad) * orbitRadius,
    y = center.y + sin(angleRad) * orbitRadius
  )

  drawCircle(
    color = color,
    radius = orbRadius,
    center = orbCenter,
    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
  )
}

private fun DrawScope.drawConnectingArcs(
  center: androidx.compose.ui.geometry.Offset,
  radius: Float,
  color: Color,
  breathingScale: Float
) {
  val path = Path()
  val adjustedRadius = radius * breathingScale

  // Draw three subtle arcs connecting the virtue positions
  for (i in 0 until 3) {
    val startAngle = i * 120f - 30f
    path.reset()
    path.arcTo(
      rect = androidx.compose.ui.geometry.Rect(
        center.x - adjustedRadius,
        center.y - adjustedRadius,
        center.x + adjustedRadius,
        center.y + adjustedRadius
      ),
      startAngleDegrees = startAngle,
      sweepAngleDegrees = 60f,
      forceMoveTo = true
    )

    drawPath(
      path = path,
      color = color,
      style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
    )
  }
}
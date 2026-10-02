package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.db.PlayerCustomizationEntity

@Composable
fun BlockyCharacterRenderer(
    customization: PlayerCustomizationEntity,
    modifier: Modifier = Modifier,
    showPedestal: Boolean = true,
    scaleFactor: Float = 1.0f
) {
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val baseFloorY = canvasHeight * 0.88f

            if (showPedestal) {
                drawRedPedestal(centerX, baseFloorY, canvasWidth)
            }

            // Draw player body with separated modular textures
            drawBlockyPlayer(
                centerX = centerX,
                bottomY = baseFloorY - 10f,
                customization = customization,
                textMeasurer = textMeasurer,
                scale = scaleFactor
            )
        }
    }
}

private fun DrawScope.drawRedPedestal(centerX: Float, floorY: Float, canvasWidth: Float) {
    val pedestalWidth = canvasWidth * 0.65f
    val pedestalHeight = 36f

    // Soft outer red glow
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF1744).copy(alpha = 0.5f), Color.Transparent),
            center = Offset(centerX, floorY),
            radius = pedestalWidth * 0.7f
        ),
        topLeft = Offset(centerX - pedestalWidth * 0.7f, floorY - pedestalHeight * 1.5f),
        size = Size(pedestalWidth * 1.4f, pedestalHeight * 3f)
    )

    // Pedestal 3D Base
    val pathBase = Path().apply {
        moveTo(centerX - pedestalWidth / 2f, floorY)
        lineTo(centerX - pedestalWidth * 0.45f, floorY + pedestalHeight)
        lineTo(centerX + pedestalWidth * 0.45f, floorY + pedestalHeight)
        lineTo(centerX + pedestalWidth / 2f, floorY)
        close()
    }
    drawPath(pathBase, color = Color(0xFF4A0000))
    drawPath(pathBase, color = Color(0xFFFF1744), style = Stroke(width = 2.5f))

    // Pedestal Top Oval
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF880000), Color(0xFF2B0000)),
            center = Offset(centerX, floorY)
        ),
        topLeft = Offset(centerX - pedestalWidth / 2f, floorY - pedestalHeight / 2f),
        size = Size(pedestalWidth, pedestalHeight)
    )
    drawOval(
        color = Color(0xFFFF5252),
        topLeft = Offset(centerX - pedestalWidth / 2f, floorY - pedestalHeight / 2f),
        size = Size(pedestalWidth, pedestalHeight),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawBlockyPlayer(
    centerX: Float,
    bottomY: Float,
    customization: PlayerCustomizationEntity,
    textMeasurer: TextMeasurer,
    scale: Float = 1.0f
) {
    val skinColor = parseHexColor(customization.skinColorHex, Color(0xFFE8B282))
    val hairColor = parseHexColor(customization.hairColorHex, Color(0xFF212121))
    val shirtColor = parseHexColor(customization.shirtColorHex, Color(0xFF68BBE3))
    val shortsColor = parseHexColor(customization.shortsColorHex, Color(0xFFFFFFFF))
    val socksColor = parseHexColor(customization.socksColorHex, Color(0xFF68BBE3))
    val glovesColor = if (customization.glovesColorHex == "none") null else parseHexColor(customization.glovesColorHex, Color(0xFF212121))
    val bootsColor = Color(0xFF151515)
    val bootsAccent = Color(0xFFE53935)

    val rotation = customization.rotationAngle // 0 = Front, 1 = 3/4, 2 = Profile/Side

    // Proportions
    val unit = 2.4f * scale
    val headW = 54f * unit
    val headH = 50f * unit
    val torsoW = 58f * unit
    val torsoH = 65f * unit
    val legW = 20f * unit
    val legH = 62f * unit
    val armW = 18f * unit
    val armH = 60f * unit

    val bootsH = 18f * unit
    val socksH = 26f * unit
    val shortsH = 34f * unit

    // Vertical Anchors
    val feetY = bottomY
    val hipsY = feetY - legH
    val waistY = hipsY
    val shouldersY = waistY - torsoH
    val headY = shouldersY - headH + (4f * unit)

    if (rotation == 2) {
        // =========================================================
        // --- PROFILE / SIDE VIEW (With Separated Textures) ---
        // =========================================================
        val sideTorsoW = 40f * unit
        val sideHeadW = 46f * unit

        // Left/Back Leg
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - 10f * unit, hipsY),
            size = Size(18f * unit, legH - bootsH)
        )
        // Back Sock with Double Top Hoop Stripes
        drawRect(
            color = socksColor,
            topLeft = Offset(centerX - 10f * unit, feetY - bootsH - socksH),
            size = Size(18f * unit, socksH)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - 10f * unit, feetY - bootsH - socksH + 2f * unit),
            size = Size(18f * unit, 4f * unit)
        )
        // Back Boot with Cleat Sole & Studs
        drawRoundRect(
            color = bootsColor,
            topLeft = Offset(centerX - 12f * unit, feetY - bootsH),
            size = Size(26f * unit, bootsH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRect(
            color = bootsAccent,
            topLeft = Offset(centerX - 12f * unit, feetY - 4f * unit),
            size = Size(26f * unit, 4f * unit)
        )

        // Front Leg (in front)
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - 6f * unit, hipsY + 4f * unit),
            size = Size(18f * unit, legH - bootsH)
        )
        // Front Sock with Double Top Hoop Stripes
        drawRect(
            color = socksColor,
            topLeft = Offset(centerX - 6f * unit, feetY - bootsH - socksH + 4f * unit),
            size = Size(18f * unit, socksH)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - 6f * unit, feetY - bootsH - socksH + 6f * unit),
            size = Size(18f * unit, 4f * unit)
        )
        // Front Boot with Cleat Sole, Studs & Laces
        drawRoundRect(
            color = bootsColor,
            topLeft = Offset(centerX - 8f * unit, feetY - bootsH + 4f * unit),
            size = Size(28f * unit, bootsH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRect(
            color = bootsAccent,
            topLeft = Offset(centerX - 8f * unit, feetY + 4f * unit - 4f * unit),
            size = Size(28f * unit, 4f * unit)
        )
        // Cleat Side Swoosh
        drawLine(
            color = Color.White,
            start = Offset(centerX - 4f * unit, feetY - bootsH + 10f * unit),
            end = Offset(centerX + 12f * unit, feetY - bootsH + 8f * unit),
            strokeWidth = 3f * unit
        )

        // Shorts (side) with Lateral Racing Stripe
        drawRect(
            color = shortsColor,
            topLeft = Offset(centerX - 14f * unit, hipsY - 2f * unit),
            size = Size(28f * unit, shortsH)
        )
        val sideStripeColor = if (shortsColor == Color.White) shirtColor else Color.White
        drawRect(
            color = sideStripeColor,
            topLeft = Offset(centerX - 2f * unit, hipsY - 2f * unit),
            size = Size(4f * unit, shortsH)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(centerX - 14f * unit, hipsY - 2f * unit),
            size = Size(28f * unit, shortsH),
            style = Stroke(width = 2.5f)
        )

        // Torso (side profile) with Side Athletic Panel
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX - sideTorsoW / 2f, shouldersY),
            size = Size(sideTorsoW, torsoH)
        )
        drawRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(centerX - 2f * unit, shouldersY + 4f * unit),
            size = Size(4f * unit, torsoH - 8f * unit)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(centerX - sideTorsoW / 2f, shouldersY),
            size = Size(sideTorsoW, torsoH),
            style = Stroke(width = 2.5f)
        )

        // Side Arm (Sleeve + Cuff + Glove / Hand)
        val sideArmW = 16f * unit
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX - 2f * unit, shouldersY + 4f * unit),
            size = Size(sideArmW, 16f * unit)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - 2f * unit, shouldersY + 20f * unit),
            size = Size(sideArmW, 4f * unit)
        )
        drawRect(
            color = glovesColor ?: skinColor,
            topLeft = Offset(centerX - 2f * unit, shouldersY + 24f * unit),
            size = Size(sideArmW, armH - 20f * unit)
        )
        if (glovesColor != null) {
            // Glove wrist strap
            drawRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(centerX - 2f * unit, shouldersY + 24f * unit),
                size = Size(sideArmW, 5f * unit)
            )
        }

        // Head (side profile)
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - sideHeadW / 2f, headY),
            size = Size(sideHeadW, headH)
        )
        // Ear Block
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - 4f * unit, headY + 18f * unit),
            size = Size(6f * unit, 10f * unit)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.2f),
            topLeft = Offset(centerX - 4f * unit, headY + 18f * unit),
            size = Size(6f * unit, 10f * unit),
            style = Stroke(width = 1.5f)
        )

        // Side Eye & Profile nose
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - sideHeadW / 2f + 4f * unit, headY + 20f * unit),
            size = Size(8f * unit, 8f * unit)
        )
        drawRect(
            color = Color(0xFF1E1E1E),
            topLeft = Offset(centerX - sideHeadW / 2f + 4f * unit, headY + 21f * unit),
            size = Size(5f * unit, 6f * unit)
        )

        // Hair & Beard in Profile
        drawHairstyle(
            centerX = centerX,
            headY = headY,
            headW = sideHeadW,
            headH = headH,
            unit = unit,
            styleId = customization.hairStyleId,
            hairColor = hairColor,
            isProfile = true
        )
    } else {
        // =========================================================
        // --- FRONT VIEW (With High-Definition Separated Textures) ---
        // =========================================================
        val legSpacing = 8f * unit

        // Left Leg
        val leftLegX = centerX - legW - legSpacing / 2f
        drawRect(
            color = skinColor,
            topLeft = Offset(leftLegX, hipsY),
            size = Size(legW, legH - bootsH)
        )
        // Left Sock with Double Hoop Top Stripes Texture
        drawRect(
            color = socksColor,
            topLeft = Offset(leftLegX, feetY - bootsH - socksH),
            size = Size(legW, socksH)
        )
        // Sock Double Hoop Stripes
        drawRect(
            color = Color.White,
            topLeft = Offset(leftLegX, feetY - bootsH - socksH + 2f * unit),
            size = Size(legW, 3f * unit)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(leftLegX, feetY - bootsH - socksH + 7f * unit),
            size = Size(legW, 3f * unit)
        )
        // Ankle Compression Seam
        drawLine(
            color = Color.Black.copy(alpha = 0.25f),
            start = Offset(leftLegX, feetY - bootsH - 4f * unit),
            end = Offset(leftLegX + legW, feetY - bootsH - 4f * unit),
            strokeWidth = 2f * unit
        )
        // Left Boot (Cleat Base + Sole Plate + Studs + Laces)
        drawRoundRect(
            color = bootsColor,
            topLeft = Offset(leftLegX - 3f * unit, feetY - bootsH),
            size = Size(legW + 6f * unit, bootsH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Cleat Sole Plate
        drawRect(
            color = bootsAccent,
            topLeft = Offset(leftLegX - 3f * unit, feetY - 4f * unit),
            size = Size(legW + 6f * unit, 4f * unit)
        )
        // Cleat White Laces
        for (l in 0..2) {
            drawLine(
                color = Color.White,
                start = Offset(leftLegX + 4f * unit, feetY - bootsH + (4f + l * 4f) * unit),
                end = Offset(leftLegX + legW - 4f * unit, feetY - bootsH + (4f + l * 4f) * unit),
                strokeWidth = 2f * unit
            )
        }

        // Right Leg
        val rightLegX = centerX + legSpacing / 2f
        drawRect(
            color = skinColor,
            topLeft = Offset(rightLegX, hipsY),
            size = Size(legW, legH - bootsH)
        )
        // Right Sock with Double Hoop Top Stripes
        drawRect(
            color = socksColor,
            topLeft = Offset(rightLegX, feetY - bootsH - socksH),
            size = Size(legW, socksH)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(rightLegX, feetY - bootsH - socksH + 2f * unit),
            size = Size(legW, 3f * unit)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(rightLegX, feetY - bootsH - socksH + 7f * unit),
            size = Size(legW, 3f * unit)
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.25f),
            start = Offset(rightLegX, feetY - bootsH - 4f * unit),
            end = Offset(rightLegX + legW, feetY - bootsH - 4f * unit),
            strokeWidth = 2f * unit
        )
        // Right Boot (Cleat Base + Sole Plate + Studs + Laces)
        drawRoundRect(
            color = bootsColor,
            topLeft = Offset(rightLegX - 3f * unit, feetY - bootsH),
            size = Size(legW + 6f * unit, bootsH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRect(
            color = bootsAccent,
            topLeft = Offset(rightLegX - 3f * unit, feetY - 4f * unit),
            size = Size(legW + 6f * unit, 4f * unit)
        )
        for (l in 0..2) {
            drawLine(
                color = Color.White,
                start = Offset(rightLegX + 4f * unit, feetY - bootsH + (4f + l * 4f) * unit),
                end = Offset(rightLegX + legW - 4f * unit, feetY - bootsH + (4f + l * 4f) * unit),
                strokeWidth = 2f * unit
            )
        }

        // -------------------------------------------------------------
        // Shorts (With Elastic Waistband & Lateral Racing Stripes)
        // -------------------------------------------------------------
        drawRect(
            color = shortsColor,
            topLeft = Offset(leftLegX - 3f * unit, hipsY - 4f * unit),
            size = Size(torsoW, shortsH)
        )
        // Drawstring Waistband
        drawRect(
            color = Color(0x33000000),
            topLeft = Offset(leftLegX - 3f * unit, hipsY - 4f * unit),
            size = Size(torsoW, 5f * unit)
        )
        // Left & Right Outer Racing Stripes on Shorts
        val shortsStripeColor = if (shortsColor == Color.White) shirtColor else Color.White
        drawRect(
            color = shortsStripeColor,
            topLeft = Offset(leftLegX - 3f * unit, hipsY - 4f * unit),
            size = Size(4f * unit, shortsH)
        )
        drawRect(
            color = shortsStripeColor,
            topLeft = Offset(rightLegX + legW - 1f * unit, hipsY - 4f * unit),
            size = Size(4f * unit, shortsH)
        )
        // Center Shorts division
        drawLine(
            color = Color.Black.copy(alpha = 0.35f),
            start = Offset(centerX, hipsY + 8f * unit),
            end = Offset(centerX, hipsY + shortsH - 4f * unit),
            strokeWidth = 3f * unit
        )
        // Hem stitching
        drawLine(
            color = Color.Black.copy(alpha = 0.2f),
            start = Offset(leftLegX - 3f * unit, hipsY + shortsH - 6f * unit),
            end = Offset(rightLegX + legW + 3f * unit, hipsY + shortsH - 6f * unit),
            strokeWidth = 1.5f * unit
        )

        // -------------------------------------------------------------
        // Torso / Jersey (With Athletic Mesh, Ribbed Collar & Graphics)
        // -------------------------------------------------------------
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX - torsoW / 2f, shouldersY),
            size = Size(torsoW, torsoH)
        )
        // Lateral Athletic Panels on Torso
        drawRect(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = Offset(centerX - torsoW / 2f, shouldersY + 6f * unit),
            size = Size(4.5f * unit, torsoH - 8f * unit)
        )
        drawRect(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = Offset(centerX + torsoW / 2f - 4.5f * unit, shouldersY + 6f * unit),
            size = Size(4.5f * unit, torsoH - 8f * unit)
        )

        // Ribbed Athletic Collar
        val collarPath = Path().apply {
            moveTo(centerX - 14f * unit, shouldersY)
            lineTo(centerX, shouldersY + 14f * unit)
            lineTo(centerX + 14f * unit, shouldersY)
            close()
        }
        drawPath(collarPath, color = Color.White)
        drawPath(collarPath, color = Color.Black.copy(alpha = 0.3f), style = Stroke(width = 1.5f * unit))

        // Special Jersey Graphic / Textures
        when (customization.specialJersey) {
            "VARZEA_3D" -> {
                // Electric Cyan Speed Emblem & "27"
                drawRect(
                    color = Color(0xFF00E5FF),
                    topLeft = Offset(centerX - 20f * unit, shouldersY + 20f * unit),
                    size = Size(40f * unit, 24f * unit)
                )
                val measured = textMeasurer.measure(
                    text = "VÁRZEA 27",
                    style = TextStyle(color = Color.Black, fontSize = (10f * scale).sp, fontWeight = FontWeight.Black)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "VÁRZEA 27",
                    topLeft = Offset(centerX - measured.size.width / 2f, shouldersY + 24f * unit),
                    style = TextStyle(color = Color.Black, fontSize = (10f * scale).sp, fontWeight = FontWeight.Black)
                )
            }
            "JESUS_TE_AMA" -> {
                // Bold White Banner & Cross
                drawRect(
                    color = Color.White,
                    topLeft = Offset(centerX - 24f * unit, shouldersY + 20f * unit),
                    size = Size(48f * unit, 22f * unit)
                )
                val measured = textMeasurer.measure(
                    text = "JESUS TE AMA",
                    style = TextStyle(color = Color(0xFFB71C1C), fontSize = (8f * scale).sp, fontWeight = FontWeight.Black)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "JESUS TE AMA",
                    topLeft = Offset(centerX - measured.size.width / 2f, shouldersY + 24f * unit),
                    style = TextStyle(color = Color(0xFFB71C1C), fontSize = (8f * scale).sp, fontWeight = FontWeight.Black)
                )
            }
            "FIRE" -> {
                // Layered Flame Gradient Texture
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFFF9100), Color(0xFFFFD600)),
                        startY = shouldersY + 20f * unit,
                        endY = shouldersY + torsoH
                    ),
                    topLeft = Offset(centerX - torsoW / 2f, shouldersY + 20f * unit),
                    size = Size(torsoW, torsoH - 20f * unit)
                )
            }
            "PRIME" -> {
                // Gold Metallic Honeycomb & Carbon Texture
                drawRect(
                    color = Color(0xFFFFD700),
                    topLeft = Offset(centerX - 16f * unit, shouldersY + 18f * unit),
                    size = Size(32f * unit, 26f * unit)
                )
                drawRect(
                    color = Color(0xFF212121),
                    topLeft = Offset(centerX - 12f * unit, shouldersY + 22f * unit),
                    size = Size(24f * unit, 18f * unit)
                )
                val primeText = textMeasurer.measure(
                    text = "PRIME",
                    style = TextStyle(color = Color(0xFFFFD700), fontSize = (8f * scale).sp, fontWeight = FontWeight.Black)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "PRIME",
                    topLeft = Offset(centerX - primeText.size.width / 2f, shouldersY + 24f * unit),
                    style = TextStyle(color = Color(0xFFFFD700), fontSize = (8f * scale).sp, fontWeight = FontWeight.Black)
                )
            }
            "CAMISA_10" -> {
                // Green #10 on Yellow/Gold Jersey
                val numTen = textMeasurer.measure(
                    text = "10",
                    style = TextStyle(color = Color(0xFF00C853), fontSize = (18f * scale).sp, fontWeight = FontWeight.Black)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "10",
                    topLeft = Offset(centerX - numTen.size.width / 2f, shouldersY + 18f * unit),
                    style = TextStyle(color = Color(0xFF00C853), fontSize = (18f * scale).sp, fontWeight = FontWeight.Black)
                )
            }
            "NINJA" -> {
                // Crossed red ninja belts
                drawLine(
                    color = Color(0xFFD50000),
                    start = Offset(centerX - torsoW / 2f, shouldersY + 10f * unit),
                    end = Offset(centerX + torsoW / 2f, shouldersY + torsoH - 10f * unit),
                    strokeWidth = 6f * unit
                )
                drawLine(
                    color = Color(0xFFD50000),
                    start = Offset(centerX + torsoW / 2f, shouldersY + 10f * unit),
                    end = Offset(centerX - torsoW / 2f, shouldersY + torsoH - 10f * unit),
                    strokeWidth = 6f * unit
                )
            }
            else -> {
                // Classic Várzea Athletic Team Crest on Left Chest
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(centerX - 18f * unit, shouldersY + 18f * unit),
                    size = Size(12f * unit, 14f * unit),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRect(
                    color = Color(0xFFD50000),
                    topLeft = Offset(centerX - 16f * unit, shouldersY + 20f * unit),
                    size = Size(8f * unit, 10f * unit)
                )
            }
        }

        drawRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(centerX - torsoW / 2f, shouldersY),
            size = Size(torsoW, torsoH),
            style = Stroke(width = 2.5f)
        )

        // -------------------------------------------------------------
        // Arms & Sleeves (With Cuff Ribbing & Glove Straps)
        // -------------------------------------------------------------
        // Left Arm
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX - torsoW / 2f - armW, shouldersY),
            size = Size(armW, 22f * unit)
        )
        // Sleeve Cuff
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - torsoW / 2f - armW, shouldersY + 20f * unit),
            size = Size(armW, 3f * unit)
        )
        // Forearm / Hand / Glove
        drawRect(
            color = glovesColor ?: skinColor,
            topLeft = Offset(centerX - torsoW / 2f - armW, shouldersY + 23f * unit),
            size = Size(armW, armH - 23f * unit)
        )
        if (glovesColor != null) {
            drawRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(centerX - torsoW / 2f - armW, shouldersY + 24f * unit),
                size = Size(armW, 5f * unit)
            )
        }
        drawRect(
            color = Color.Black.copy(alpha = 0.3f),
            topLeft = Offset(centerX - torsoW / 2f - armW, shouldersY),
            size = Size(armW, armH),
            style = Stroke(width = 2f)
        )

        // Right Arm
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX + torsoW / 2f, shouldersY),
            size = Size(armW, 22f * unit)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX + torsoW / 2f, shouldersY + 20f * unit),
            size = Size(armW, 3f * unit)
        )
        drawRect(
            color = glovesColor ?: skinColor,
            topLeft = Offset(centerX + torsoW / 2f, shouldersY + 23f * unit),
            size = Size(armW, armH - 23f * unit)
        )
        if (glovesColor != null) {
            drawRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(centerX + torsoW / 2f, shouldersY + 24f * unit),
                size = Size(armW, 5f * unit)
            )
        }
        drawRect(
            color = Color.Black.copy(alpha = 0.3f),
            topLeft = Offset(centerX + torsoW / 2f, shouldersY),
            size = Size(armW, armH),
            style = Stroke(width = 2f)
        )

        // -------------------------------------------------------------
        // Head & Expressive Face (Eyes, Pupils, Catchlights, Smirk)
        // -------------------------------------------------------------
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - headW / 2f, headY),
            size = Size(headW, headH)
        )
        // Ear Blocks
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - headW / 2f - 4f * unit, headY + 18f * unit),
            size = Size(4f * unit, 12f * unit)
        )
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX + headW / 2f, headY + 18f * unit),
            size = Size(4f * unit, 12f * unit)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(centerX - headW / 2f, headY),
            size = Size(headW, headH),
            style = Stroke(width = 2.5f)
        )

        // Expressive Face Details
        val eyeY = headY + 22f * unit
        // Left Eye
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - 18f * unit, eyeY),
            size = Size(10f * unit, 9f * unit)
        )
        drawRect(
            color = Color(0xFF1E1E1E),
            topLeft = Offset(centerX - 15f * unit, eyeY + 2f * unit),
            size = Size(6f * unit, 6f * unit)
        )
        // Specular eye catchlight
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX - 14f * unit, eyeY + 2f * unit),
            size = Size(2f * unit, 2f * unit)
        )
        // Eyebrows
        drawLine(
            color = hairColor,
            start = Offset(centerX - 20f * unit, eyeY - 4f * unit),
            end = Offset(centerX - 8f * unit, eyeY - 1f * unit),
            strokeWidth = 3.5f * unit
        )

        // Right Eye
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX + 8f * unit, eyeY),
            size = Size(10f * unit, 9f * unit)
        )
        drawRect(
            color = Color(0xFF1E1E1E),
            topLeft = Offset(centerX + 9f * unit, eyeY + 2f * unit),
            size = Size(6f * unit, 6f * unit)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(centerX + 10f * unit, eyeY + 2f * unit),
            size = Size(2f * unit, 2f * unit)
        )
        // Eyebrows
        drawLine(
            color = hairColor,
            start = Offset(centerX + 8f * unit, eyeY - 1f * unit),
            end = Offset(centerX + 20f * unit, eyeY - 4f * unit),
            strokeWidth = 3.5f * unit
        )

        // Smirk mouth
        val mouthPath = Path().apply {
            moveTo(centerX - 4f * unit, headY + 38f * unit)
            lineTo(centerX + 14f * unit, headY + 35f * unit)
            lineTo(centerX + 10f * unit, headY + 41f * unit)
            close()
        }
        drawPath(mouthPath, color = Color.White)
        drawPath(mouthPath, color = Color(0xFF212121), style = Stroke(width = 1.5f * unit))

        // Hair Rendering with Separated Textures
        drawHairstyle(
            centerX = centerX,
            headY = headY,
            headW = headW,
            headH = headH,
            unit = unit,
            styleId = customization.hairStyleId,
            hairColor = hairColor,
            isProfile = false
        )
    }
}

private fun DrawScope.drawHairstyle(
    centerX: Float,
    headY: Float,
    headW: Float,
    headH: Float,
    unit: Float,
    styleId: Int,
    hairColor: Color,
    isProfile: Boolean
) {
    when (styleId) {
        0 -> {
            // Bald / Careca: Textured razor stubble & hairline contour
            val stubbleH = 4f * unit
            drawRect(
                color = hairColor.copy(alpha = 0.25f),
                topLeft = Offset(centerX - headW / 2f, headY),
                size = Size(headW, stubbleH)
            )
        }
        1 -> {
            // Buzzcut / Degradê: Textured fine grid buzz with temple fade
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 1.5f * unit, headY - 5f * unit),
                size = Size(headW + 3f * unit, 16f * unit)
            )
            // Temple fade texture
            drawRect(
                color = hairColor.copy(alpha = 0.5f),
                topLeft = Offset(centerX - headW / 2f, headY + 8f * unit),
                size = Size(headW, 5f * unit)
            )
        }
        2 -> {
            // Long Dreads: Braided weave texture with Gold bead rings
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 2f * unit, headY - 6f * unit),
                size = Size(headW + 4f * unit, 18f * unit)
            )
            // Left dreads with gold beads
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 5f * unit, headY + 10f * unit),
                size = Size(9f * unit, 38f * unit)
            )
            // Gold bead ring
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(centerX - headW / 2f - 6f * unit, headY + 28f * unit),
                size = Size(11f * unit, 5f * unit)
            )
            // Right dreads with gold beads
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX + headW / 2f - 4f * unit, headY + 10f * unit),
                size = Size(9f * unit, 38f * unit)
            )
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(centerX + headW / 2f - 5f * unit, headY + 28f * unit),
                size = Size(11f * unit, 5f * unit)
            )
        }
        3 -> {
            // Spiky Voxels / Espetado: Multi-spike voxels with highlighted blonde tips
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 2f * unit, headY - 14f * unit),
                size = Size(headW + 4f * unit, 22f * unit)
            )
            for (i in -2..2) {
                // Spike body
                drawRect(
                    color = hairColor,
                    topLeft = Offset(centerX + i * 10f * unit - 4f * unit, headY - 22f * unit),
                    size = Size(8f * unit, 14f * unit)
                )
                // Highlighted blonde tip
                drawRect(
                    color = Color(0xFFFFD600),
                    topLeft = Offset(centerX + i * 10f * unit - 3f * unit, headY - 26f * unit),
                    size = Size(6f * unit, 5f * unit)
                )
            }
        }
        4 -> {
            // Fade com Risco Lateral / Razor Slash Fade
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 1.5f * unit, headY - 7f * unit),
                size = Size(headW + 3f * unit, 18f * unit)
            )
            // White razor slash line
            drawLine(
                color = Color.White,
                start = Offset(centerX - headW / 4f, headY + 6f * unit),
                end = Offset(centerX - headW / 2f + 2f * unit, headY + 1f * unit),
                strokeWidth = 3f * unit
            )
        }
        5 -> {
            // Athletic Headband + Beard (Matches Reference Screenshot!)
            // Hair top volume
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 2f * unit, headY - 10f * unit),
                size = Size(headW + 4f * unit, 18f * unit)
            )
            // Athletic Woven Headband (White with ribbed weave lines)
            val bandY = headY + 6f * unit
            val bandH = 9f * unit
            drawRect(
                color = Color.White,
                topLeft = Offset(centerX - headW / 2f - 3f * unit, bandY),
                size = Size(headW + 6f * unit, bandH)
            )
            // Headband cyan brand emblem
            drawRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(centerX - 4f * unit, bandY + 2f * unit),
                size = Size(8f * unit, 5f * unit)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(centerX - headW / 2f - 3f * unit, bandY),
                size = Size(headW + 6f * unit, bandH),
                style = Stroke(width = 1.5f)
            )
            // Trim Beard & Mustache with hair grain
            val beardH = 15f * unit
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f + 2f * unit, headY + headH - beardH),
                size = Size(headW - 4f * unit, beardH)
            )
        }
        6 -> {
            // Modern Mohawk: Bronze/Dyed Center Crest with clean temples
            drawRect(
                color = Color(0xFFC47C35),
                topLeft = Offset(centerX - 12f * unit, headY - 18f * unit),
                size = Size(24f * unit, 26f * unit)
            )
            // Crest highlight
            drawRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(centerX - 6f * unit, headY - 22f * unit),
                size = Size(12f * unit, 6f * unit)
            )
            drawRect(
                color = Color(0xFF5D4037),
                topLeft = Offset(centerX - headW / 2f, headY - 2f * unit),
                size = Size(headW, 10f * unit)
            )
        }
        7 -> {
            // Striped Yellow/Black Band with Afro Curls
            drawRect(
                color = hairColor,
                topLeft = Offset(centerX - headW / 2f - 3f * unit, headY - 12f * unit),
                size = Size(headW + 6f * unit, 22f * unit)
            )
            // Striped yellow & black band
            val bandY = headY + 7f * unit
            val bandH = 9f * unit
            drawRect(
                color = Color(0xFFFFD600),
                topLeft = Offset(centerX - headW / 2f - 3f * unit, bandY),
                size = Size(headW + 6f * unit, bandH)
            )
            for (k in -3..3) {
                drawLine(
                    color = Color.Black,
                    start = Offset(centerX + k * 8f * unit, bandY),
                    end = Offset(centerX + k * 8f * unit + 4f * unit, bandY + bandH),
                    strokeWidth = 3f * unit
                )
            }
        }
        8 -> {
            // Textured Curly Ginger/Auburn Waves
            drawRect(
                color = Color(0xFFD84315),
                topLeft = Offset(centerX - headW / 2f - 2f * unit, headY - 14f * unit),
                size = Size(headW + 4f * unit, 22f * unit)
            )
            // Wave highlights
            drawRect(
                color = Color(0xFFFF7043),
                topLeft = Offset(centerX - 14f * unit, headY - 18f * unit),
                size = Size(28f * unit, 6f * unit)
            )
        }
    }
}

fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val clean = hex.removePrefix("#")
        val longVal = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or longVal)
        } else if (clean.length == 8) {
            Color(longVal)
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

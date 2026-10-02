package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.PlayerCustomizationEntity
import com.example.ui.components.BlockyCharacterRenderer
import com.example.ui.components.parseHexColor
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HairEditScreen(
    viewModel: GameViewModel,
    customization: PlayerCustomizationEntity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0202))
    ) {
        // Red glowing background
        Image(
            painter = painterResource(id = R.drawable.bg_varzea_edit),
            contentDescription = "Hair Edit Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0xAA1E0000)),
                        radius = 850f
                    )
                )
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Arrow Button (Image 4 Top Left)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6D6D6))
                    .clickable { viewModel.navigateBack() }
                    .testTag("hair_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Main Layout: Left (PELE + 3x3 Grid of Hairstyles) and Center/Right (Player on Pedestal)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 46.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: 3x3 Grid of Hairstyles (Image 4)
            Column(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                // PELE & CABELO Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                ) {
                    Text(
                        text = "PELE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    val skinColors = listOf(
                        "#59351F" to "Escura",
                        "#E8B282" to "Clara"
                    )
                    skinColors.forEach { (hex, _) ->
                        val isSelected = customization.skinColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(24.dp, 16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(parseHexColor(hex, Color.Gray))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .clickable { viewModel.setSkinColor(hex) }
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "COR",
                        color = Color(0xFFFFD54F),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    val hairColors = listOf(
                        "#1E1E1E", "#4E342E", "#FDD835", "#D84315", "#00E5FF"
                    )
                    hairColors.forEach { hex ->
                        val isSel = customization.hairColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(18.dp, 16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(parseHexColor(hex, Color.Black))
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) Color(0xFF00E5FF) else Color(0xFF64748B),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .clickable { viewModel.setHairColor(hex) }
                        )
                    }
                }

                // 3x3 Grid of 9 Hairstyles
                val hairStyles = (0..8).toList()
                val chunked = hairStyles.chunked(3)

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chunked.forEach { rowIds ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowIds.forEach { styleId ->
                                val isSelected = customization.hairStyleId == styleId
                                HairStyleTile(
                                    styleId = styleId,
                                    skinColorHex = customization.skinColorHex,
                                    hairColorHex = customization.hairColorHex,
                                    isSelected = isSelected,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    onClick = { viewModel.setHairStyle(styleId) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Center / Right Side: Player standing on red pedestal + Yellow Rotate Button (Image 4)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Yellow Rotating Arrows (Image 4: centered above character)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable { viewModel.rotatePlayer() }
                            .testTag("hair_rotate_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Girar",
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Player on Pedestal
                    BlockyCharacterRenderer(
                        customization = customization,
                        showPedestal = true,
                        scaleFactor = 1.1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HairStyleTile(
    styleId: Int,
    skinColorHex: String,
    hairColorHex: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val skinColor = parseHexColor(skinColorHex, Color(0xFFE8B282))
    val hairColor = parseHexColor(hairColorHex, Color(0xFF1E1E1E))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xBB1E293B))
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .testTag("hair_style_$styleId"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Blocky head square
            val headSize = w * 0.62f
            drawRoundRect(
                color = skinColor,
                topLeft = Offset(cx - headSize / 2f, cy - headSize / 2f + 4f),
                size = Size(headSize, headSize),
                cornerRadius = CornerRadius(3f, 3f)
            )
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(cx - headSize / 2f, cy - headSize / 2f + 4f),
                size = Size(headSize, headSize),
                cornerRadius = CornerRadius(3f, 3f),
                style = Stroke(width = 1.5f)
            )

            // Small eyes
            val eyeY = cy + 2f
            drawRect(
                color = Color.White,
                topLeft = Offset(cx - headSize * 0.28f, eyeY),
                size = Size(headSize * 0.18f, headSize * 0.16f)
            )
            drawRect(
                color = Color(0xFF1E1E1E),
                topLeft = Offset(cx - headSize * 0.24f, eyeY + 1f),
                size = Size(headSize * 0.12f, headSize * 0.12f)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(cx + headSize * 0.10f, eyeY),
                size = Size(headSize * 0.18f, headSize * 0.16f)
            )
            drawRect(
                color = Color(0xFF1E1E1E),
                topLeft = Offset(cx + headSize * 0.12f, eyeY + 1f),
                size = Size(headSize * 0.12f, headSize * 0.12f)
            )

            // Hairstyle on the head icon
            val hairTopY = cy - headSize / 2f + 2f
            when (styleId) {
                0 -> {
                    // Bald: subtle razor hairline stubble
                    drawRect(
                        color = hairColor.copy(alpha = 0.25f),
                        topLeft = Offset(cx - headSize / 2f, hairTopY + 2f),
                        size = Size(headSize, headSize * 0.15f)
                    )
                }
                1 -> {
                    // Short buzzcut with fade
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 1f, hairTopY - 3f),
                        size = Size(headSize + 2f, headSize * 0.32f)
                    )
                    drawRect(
                        color = hairColor.copy(alpha = 0.5f),
                        topLeft = Offset(cx - headSize / 2f, hairTopY + headSize * 0.22f),
                        size = Size(headSize, headSize * 0.12f)
                    )
                }
                2 -> {
                    // Long dreads with gold beads
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY - 3f),
                        size = Size(headSize + 4f, headSize * 0.35f)
                    )
                    // Side dreads
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 4f, hairTopY + 4f),
                        size = Size(headSize * 0.2f, headSize * 0.7f)
                    )
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(cx - headSize / 2f - 5f, hairTopY + headSize * 0.5f),
                        size = Size(headSize * 0.24f, 4f)
                    )
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx + headSize / 2f - headSize * 0.1f, hairTopY + 4f),
                        size = Size(headSize * 0.2f, headSize * 0.7f)
                    )
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(cx + headSize / 2f - headSize * 0.12f, hairTopY + headSize * 0.5f),
                        size = Size(headSize * 0.24f, 4f)
                    )
                }
                3 -> {
                    // Spiky top dreads with highlighted tips
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY - 6f),
                        size = Size(headSize + 4f, headSize * 0.45f)
                    )
                    for (i in -1..1) {
                        drawRect(
                            color = hairColor,
                            topLeft = Offset(cx + i * 8f - 3f, hairTopY - 10f),
                            size = Size(6f, 6f)
                        )
                        drawRect(
                            color = Color(0xFFFFD600),
                            topLeft = Offset(cx + i * 8f - 2f, hairTopY - 12f),
                            size = Size(4f, 3f)
                        )
                    }
                }
                4 -> {
                    // Fade with razor line
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 1f, hairTopY - 4f),
                        size = Size(headSize + 2f, headSize * 0.32f)
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(cx - 2f, hairTopY + 2f),
                        end = Offset(cx - headSize / 2f + 3f, hairTopY + 1f),
                        strokeWidth = 2.5f
                    )
                }
                5 -> {
                    // White headband + beard (Signature!)
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY - 5f),
                        size = Size(headSize + 4f, headSize * 0.28f)
                    )
                    // Woven Headband
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY + headSize * 0.16f),
                        size = Size(headSize + 4f, headSize * 0.18f)
                    )
                    drawRect(
                        color = Color(0xFF00E5FF),
                        topLeft = Offset(cx - 3f, hairTopY + headSize * 0.19f),
                        size = Size(6f, 3f)
                    )
                    // Beard
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f + 1f, cy + headSize * 0.24f),
                        size = Size(headSize - 2f, headSize * 0.28f)
                    )
                }
                6 -> {
                    // Modern brown mohawk with dyed tips
                    drawRect(
                        color = Color(0xFFC47C35),
                        topLeft = Offset(cx - headSize * 0.25f, hairTopY - 8f),
                        size = Size(headSize * 0.5f, headSize * 0.45f)
                    )
                    drawRect(
                        color = Color(0xFFFFD54F),
                        topLeft = Offset(cx - headSize * 0.15f, hairTopY - 10f),
                        size = Size(headSize * 0.3f, 4f)
                    )
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(cx - headSize / 2f, hairTopY),
                        size = Size(headSize, headSize * 0.2f)
                    )
                }
                7 -> {
                    // Striped yellow/black band + curls
                    drawRect(
                        color = hairColor,
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY - 6f),
                        size = Size(headSize + 4f, headSize * 0.35f)
                    )
                    drawRect(
                        color = Color(0xFFFFD600),
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY + headSize * 0.14f),
                        size = Size(headSize + 4f, headSize * 0.18f)
                    )
                    for (i in -1..1) {
                        drawLine(
                            color = Color.Black,
                            start = Offset(cx + i * 8f, hairTopY + headSize * 0.14f),
                            end = Offset(cx + i * 8f + 3f, hairTopY + headSize * 0.32f),
                            strokeWidth = 2f
                        )
                    }
                }
                8 -> {
                    // Ginger/bronze curls with layered highlight
                    drawRect(
                        color = Color(0xFFD84315),
                        topLeft = Offset(cx - headSize / 2f - 2f, hairTopY - 6f),
                        size = Size(headSize + 4f, headSize * 0.42f)
                    )
                    drawRect(
                        color = Color(0xFFFF7043),
                        topLeft = Offset(cx - headSize * 0.3f, hairTopY - 8f),
                        size = Size(headSize * 0.6f, 4f)
                    )
                }
            }
        }
    }
}

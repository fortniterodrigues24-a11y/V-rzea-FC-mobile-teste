package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserSettingsEntity
import com.example.ui.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    settings: UserSettingsEntity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1D1F))
    ) {
        // Top Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            // Back Arrow Button (Image 2 Top Left: Circle with black arrow)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6D6D6))
                    .clickable { viewModel.navigateBack() }
                    .testTag("settings_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // SALVAR Button (Image 2 Top Center: Pill shape, dark blue with white border)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF002244))
                    .border(2.5.dp, Color.White, RoundedCornerShape(24.dp))
                    .clickable { viewModel.saveAllSettings() }
                    .testTag("salvar_button")
                    .padding(horizontal = 42.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SALVAR",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        // Two Main Columns (OPÇÕES and GRÁFICOS)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 76.dp, bottom = 20.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Left Column: OPÇÕES
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF7E8085), RoundedCornerShape(4.dp))
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // OPÇÕES Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(Color(0xFFC7C9CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OPÇÕES",
                        color = Color(0xFF1E1E1E),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "TEMPO DE JOGO",
                    color = Color(0xFF111111),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Time Option Buttons
                val timeOptions = listOf("1:00", "1:30", "2:00")
                timeOptions.forEach { time ->
                    val isSelected = settings.gameDuration == time
                    SettingsOptionButton(
                        text = time,
                        isSelected = isSelected,
                        testTag = "time_option_$time",
                        onClick = { viewModel.setGameDuration(time) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Right Column: GRÁFICOS & TORCIDA
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF7E8085), RoundedCornerShape(4.dp))
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // GRÁFICOS Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(Color(0xFFC7C9CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GRÁFICOS",
                        color = Color(0xFF1E1E1E),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "GRÁFICOS",
                    color = Color(0xFF111111),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Graphics Quality Buttons
                val graphicsOptions = listOf("BAIXO", "MÉDIO", "ALTO")
                graphicsOptions.forEach { quality ->
                    val isSelected = settings.graphicsQuality == quality
                    SettingsOptionButton(
                        text = quality,
                        isSelected = isSelected,
                        testTag = "graphic_option_$quality",
                        onClick = { viewModel.setGraphicsQuality(quality) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TORCIDA",
                    color = Color(0xFF111111),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Torcida Yes/No Buttons
                SettingsOptionButton(
                    text = "SIM",
                    isSelected = settings.torcidaEnabled,
                    testTag = "torcida_sim",
                    onClick = { viewModel.setTorcidaEnabled(true) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsOptionButton(
                    text = "NÃO",
                    isSelected = !settings.torcidaEnabled,
                    testTag = "torcida_nao",
                    onClick = { viewModel.setTorcidaEnabled(false) }
                )
            }
        }
    }
}

@Composable
private fun SettingsOptionButton(
    text: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(135.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isSelected) Color(0xFF1E2024) else Color(0xFF383A40))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF23252A),
                shape = RoundedCornerShape(3.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFFE0E0E0),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

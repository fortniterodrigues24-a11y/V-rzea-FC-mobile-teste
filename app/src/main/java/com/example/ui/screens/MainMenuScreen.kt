package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.PlayerCustomizationEntity
import com.example.data.db.UserSettingsEntity
import com.example.ui.components.BlockyCharacterRenderer
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    settings: UserSettingsEntity,
    customization: PlayerCustomizationEntity
) {
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var showEspeciaisDialog by remember { mutableStateOf(false) }
    var showLeagueDialog by remember { mutableStateOf(false) }
    var showEstadioDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030A1C))
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_varzea_menu),
            contentDescription = "Background Menu",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x9900081C),
                            Color(0x4400081C),
                            Color(0xAA00081C)
                        )
                    )
                )
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings Button (Gear icon) - Image 1 Top Left
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x991A1E2E))
                    .border(2.dp, Color(0xFF4A5568), CircleShape)
                    .clickable { viewModel.navigateTo(ScreenState.SETTINGS) }
                    .testTag("settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Top Center Coin Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC001D4A))
                    .border(1.5.dp, Color(0xFF00B4D8), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0096C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Moedas",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${settings.coins}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Empty spacer for layout balance
            Spacer(modifier = Modifier.size(44.dp))
        }

        // Main Content: Split into Left (Hero Character + Title) and Right (6 Action Tiles)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: VÁRZEA FC 27 Logo and Hero Character
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Interactive 3D blocky character renderer
                BlockyCharacterRenderer(
                    customization = customization.copy(rotationAngle = 0),
                    showPedestal = false,
                    scaleFactor = 1.15f,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp)
                )

                // Big Dynamic Title Logo "VÁRZEA FC 27"
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 6.dp)
                ) {
                    Box {
                        // Cyan drop shadow / glow
                        Text(
                            text = "VÁRZEA FC 27",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp, start = 2.dp)
                        )
                        // Foreground white/cyan metallic gradient text
                        Text(
                            text = "VÁRZEA FC 27",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Side: 3 Columns matching Image 1 layout
            Row(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: UPGRADE (top), EDITAR (middle), ESTÁDIO (bottom)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    // UPGRADE Tile (Purple)
                    MenuTile(
                        title = "UPGRADE",
                        accentColor = Color(0xFFA855F7),
                        bgColor = Color(0xFF2E1065),
                        heightDp = 64,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowUp,
                                contentDescription = "Upgrade",
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showUpgradeDialog = true }
                    )

                    // EDITAR Tile (Red) - Direct navigation to PlayerEditScreen
                    MenuTile(
                        title = "EDITAR",
                        accentColor = Color(0xFFEF4444),
                        bgColor = Color(0xFF7F1D1D),
                        heightDp = 64,
                        testTag = "editar_button",
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(Color(0xFFDC2626), RoundedCornerShape(4.dp))
                                    .border(1.5.dp, Color(0xFFFF8A80), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✂", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.navigateTo(ScreenState.PLAYER_EDIT) }
                    )

                    // ESTÁDIO Tile (Green)
                    MenuTile(
                        title = "ESTÁDIO",
                        accentColor = Color(0xFF10B981),
                        bgColor = Color(0xFF064E3B),
                        heightDp = 64,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Estádio",
                                tint = Color(0xFF86EFAC),
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showEstadioDialog = true }
                    )
                }

                // Column 2: ESPECIAIS (top), BIG LEAGUE (tall card in middle/bottom)
                Column(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    // ESPECIAIS Tile (Cyan / Purple Gift)
                    MenuTile(
                        title = "ESPECIAIS",
                        accentColor = Color(0xFFC084FC),
                        bgColor = Color(0xFF4A044E),
                        heightDp = 64,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Especiais",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showEspeciaisDialog = true }
                    )

                    // BIG LEAGUE Tile (Tall card matching Image 1)
                    TallMenuTile(
                        title = "BIG LEAGUE",
                        accentColor = Color(0xFFF59E0B),
                        bgColor = Color(0xFF1E3A8A),
                        heightDp = 136,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showLeagueDialog = true }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("⚽", fontSize = 28.sp)
                            Text(
                                text = "BIG",
                                color = Color(0xFFFF9800),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "LEAGUE",
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Column 3: ALEATÓRIO (Tall card with gray silhouette and question mark '?')
                Column(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    TallMenuTile(
                        title = "ALEATÓRIO",
                        accentColor = Color(0xFF94A3B8),
                        bgColor = Color(0xFF0F172A),
                        heightDp = 208,
                        testTag = "aleatorio_match_button",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.navigateTo(ScreenState.MATCH_FIELD) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .border(2.dp, Color(0xFF94A3B8), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "?",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "ALEATÓRIO",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs for secondary actions
    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = { Text("UPGRADE DE HABILIDADES", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("Melhore os atributos do seu craque usando moedas!")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("VELOCIDADE: ${customization.speed}", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { viewModel.upgradeSpeed() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("+1 (50 Moedas)")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("CHUTE: ${customization.kick}", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { viewModel.upgradeKick() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("+1 (50 Moedas)")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("FECHAR")
                }
            }
        )
    }

    if (showEspeciaisDialog) {
        AlertDialog(
            onDismissRequest = { showEspeciaisDialog = false },
            title = { Text("PACOTES ESPECIAIS", fontWeight = FontWeight.Black) },
            text = {
                Text("Desbloqueie camisas clássicas do futebol de várzea: 'VARZEA 3D', 'JESUS TE AMA', 'FIRE', 'SALMO 91' e muito mais na tela de Edição!")
            },
            confirmButton = {
                TextButton(onClick = {
                    showEspeciaisDialog = false
                    viewModel.navigateTo(ScreenState.PLAYER_EDIT)
                }) {
                    Text("IR PARA EDIÇÃO")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEspeciaisDialog = false }) {
                    Text("FECHAR")
                }
            }
        )
    }

    if (showLeagueDialog) {
        AlertDialog(
            onDismissRequest = { showLeagueDialog = false },
            title = { Text("BIG LEAGUE VÁRZEA", fontWeight = FontWeight.Black) },
            text = {
                Text("Temporada 2027: Disputa contra os melhores times da várzea brasileira! Jogue no modo ALEATÓRIO para acumular vitórias e moedas!")
            },
            confirmButton = {
                TextButton(onClick = {
                    showLeagueDialog = false
                    viewModel.navigateTo(ScreenState.MATCH_FIELD)
                }) {
                    Text("JOGAR AGORA")
                }
            }
        )
    }

    if (showEstadioDialog) {
        AlertDialog(
            onDismissRequest = { showEstadioDialog = false },
            title = { Text("ESTÁDIO DA VÁRZEA", fontWeight = FontWeight.Black) },
            text = {
                Text("Campo oficial de terra batida e asfalto com arquibancadas lotadas. Configure a Torcida nas Configurações para personalizar a experiência!")
            },
            confirmButton = {
                TextButton(onClick = {
                    showEstadioDialog = false
                    viewModel.navigateTo(ScreenState.MATCH_FIELD)
                }) {
                    Text("ENTRAR EM CAMPO")
                }
            }
        )
    }
}

@Composable
fun MenuTile(
    title: String,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    heightDp: Int = 68,
    testTag: String? = null,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(heightDp.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(bgColor.copy(alpha = 0.95f), bgColor.copy(alpha = 0.7f))
                )
            )
            .border(2.dp, accentColor, RoundedCornerShape(8.dp))
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TallMenuTile(
    title: String,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    heightDp: Int = 136,
    testTag: String? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(heightDp.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(bgColor.copy(alpha = 0.95f), Color(0xFF030712))
                )
            )
            .border(2.dp, accentColor, RoundedCornerShape(8.dp))
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.PlayerCustomizationEntity
import com.example.data.db.UserSettingsEntity
import com.example.ui.components.BlockyCharacterRenderer
import com.example.ui.components.parseHexColor
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.ScreenState

@Composable
fun PlayerEditScreen(
    viewModel: GameViewModel,
    customization: PlayerCustomizationEntity,
    settings: UserSettingsEntity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0202))
    ) {
        // Red crystal locker background
        Image(
            painter = painterResource(id = R.drawable.bg_varzea_edit),
            contentDescription = "Edit Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark red vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0xAA1A0000)),
                        radius = 800f
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
            // Back Arrow Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6D6D6))
                    .clickable { viewModel.navigateBack() }
                    .testTag("edit_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Coin Currency
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

            Spacer(modifier = Modifier.size(42.dp))
        }

        // Center Character on Pedestal + Yellow Rotate Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Yellow Rotation Arrows Button (Image 3: circular yellow arrows above character)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .clickable { viewModel.rotatePlayer() }
                        .testTag("rotate_character_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Girar Personagem",
                        tint = Color(0xFFFFD600),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // The blocky character standing on red pedestal
                BlockyCharacterRenderer(
                    customization = customization,
                    showPedestal = true,
                    scaleFactor = 1.05f,
                    modifier = Modifier
                        .size(width = 280.dp, height = 230.dp)
                )
            }
        }

        // Right Floating HUD Box: VELOCIDADE 65 / CHUTE 65 (Image 3)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .width(170.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC051838))
                .border(2.dp, Color(0xFF00B4D8), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Silhouette avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF263238)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Avatar",
                        tint = Color(0xFF90A4AE),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VELOCIDADE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${customization.speed}",
                            color = Color(0xFFFF5252),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CHUTE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${customization.kick}",
                            color = Color(0xFFFF5252),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Left Customization Panel (Image 3)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp, top = 44.dp, bottom = 8.dp)
                .fillMaxHeight()
                .width(280.dp)
        ) {
            // Header Row: PELE + ESPECIAIS + CABELOS Button!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PELE",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )

                // Skin Color Swatches
                val skinColors = listOf(
                    "#59351F" to "Escura",
                    "#E8B282" to "Clara",
                    "#B57849" to "Média"
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

                // ESPECIAIS button badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF6B21A8))
                        .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ESPECIAIS",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CABELOS Button (Explicitly requested by user!)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0284C7))
                        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(ScreenState.HAIR_EDIT) }
                        .testTag("cabelos_button")
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "CABELOS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body Customization & Special Jerseys (Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Special Jerseys Grid (3 columns)
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val specialJerseys = listOf(
                        Triple("NINJA", "NINJA NO DRIBL", Color(0xFFDC2626)),
                        Triple("VARZEA_3D", "VARZEA 3D", Color(0xFF2563EB)),
                        Triple("JESUS_TE_AMA", "JESUS TE AMA", Color(0xFFB91C1C)),
                        Triple("FIRE", "FIRE", Color(0xFFEA580C)),
                        Triple("PRIME", "PRIME", Color(0xFFCA8A04)),
                        Triple("DEUS_NO_CORACAO", "DEUS NO CORAÇÃO", Color(0xFF1D4ED8)),
                        Triple("SALMO_91", "SALMO 91", Color(0xFF1E293B)),
                        Triple("CAMISA_10", "10 BRASIL", Color(0xFF059669))
                    )

                    specialJerseys.chunked(2).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowItems.forEach { (key, title, barColor) ->
                                val isEquipped = customization.specialJersey == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0x991E293B))
                                        .border(
                                            width = if (isEquipped) 2.dp else 1.dp,
                                            color = if (isEquipped) Color(0xFF00E5FF) else Color(0xFF475569),
                                            shape = RoundedCornerShape(3.dp)
                                        )
                                        .clickable { viewModel.setSpecialJersey(key) }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(barColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isEquipped) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Equipado",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Desbloqueado",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = title,
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Customization slots (Shirt, Shorts, Gloves, Socks) with Color Palettes
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Shirt Slot & Colors
                    Text("CAMISA", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val shirtColors = listOf("#68BBE3", "#FFFFFF", "#1E1E1E", "#D32F2F", "#388E3C", "#FBC02D")
                    ColorPaletteRow(
                        colors = shirtColors,
                        selectedHex = customization.shirtColorHex,
                        onSelect = { viewModel.setShirtColor(it) }
                    )

                    // 2. Shorts Slot & Colors
                    Text("CALÇÃO", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val shortsColors = listOf("#FFFFFF", "#68BBE3", "#1E1E1E", "#D32F2F")
                    ColorPaletteRow(
                        colors = shortsColors,
                        selectedHex = customization.shortsColorHex,
                        onSelect = { viewModel.setShortsColor(it) }
                    )

                    // 3. Gloves Slot & Colors
                    Text("LUVAS", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // None option
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF374151))
                                .border(
                                    width = if (customization.glovesColorHex == "none") 2.dp else 1.dp,
                                    color = if (customization.glovesColorHex == "none") Color(0xFF00E5FF) else Color.Gray,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .clickable { viewModel.setGlovesColor("none") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "Sem luva",
                                tint = Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        val glovesColors = listOf("#1E1E1E", "#FFFFFF", "#D32F2F")
                        glovesColors.forEach { hex ->
                            val isSel = customization.glovesColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(parseHexColor(hex, Color.Gray))
                                    .border(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) Color(0xFF00E5FF) else Color.White,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .clickable { viewModel.setGlovesColor(hex) }
                            )
                        }
                    }

                    // 4. Socks Slot & Colors
                    Text("MEIÕES", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val socksColors = listOf("#68BBE3", "#FFFFFF", "#1E1E1E", "#D32F2F")
                    ColorPaletteRow(
                        colors = socksColors,
                        selectedHex = customization.socksColorHex,
                        onSelect = { viewModel.setSocksColor(it) }
                    )

                    // 5. Hair Color Slot & Colors
                    Text("COR DO CABELO", color = Color(0xFF90CAF9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val hairColors = listOf("#1E1E1E", "#4E342E", "#FDD835", "#D84315", "#ECEFF1", "#00E5FF")
                    ColorPaletteRow(
                        colors = hairColors,
                        selectedHex = customization.hairColorHex,
                        onSelect = { viewModel.setHairColor(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPaletteRow(
    colors: List<String>,
    selectedHex: String,
    onSelect: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        colors.forEach { hex ->
            val isSelected = selectedHex.equals(hex, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(parseHexColor(hex, Color.Gray))
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF718096),
                        shape = RoundedCornerShape(2.dp)
                    )
                    .clickable { onSelect(hex) }
            )
        }
    }
}

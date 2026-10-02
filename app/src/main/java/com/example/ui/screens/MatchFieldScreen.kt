package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PlayerCustomizationEntity
import com.example.data.db.UserSettingsEntity
import com.example.ui.graphics3d.Camera3D
import com.example.ui.graphics3d.Vec3
import com.example.ui.graphics3d.draw3DLine
import com.example.ui.graphics3d.draw3DPlayer
import com.example.ui.graphics3d.draw3DQuad
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.ScreenState
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class BallPossession { NONE, PLAYER, NPC }

@Composable
fun MatchFieldScreen(
    viewModel: GameViewModel,
    settings: UserSettingsEntity,
    customization: PlayerCustomizationEntity
) {
    val initialSeconds = when (settings.gameDuration) {
        "1:00" -> 60
        "2:00" -> 120
        else -> 48
    }

    var timeLeft by remember { mutableIntStateOf(initialSeconds) }
    var playerScore by remember { mutableIntStateOf(0) }
    var npcScore by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var showPauseMenu by remember { mutableStateOf(false) }
    var showGoalBanner by remember { mutableStateOf<String?>(null) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    var isSprinting by remember { mutableStateOf(false) }

    // Ball Possession System (pegar / roubar a bola)
    var ballPossession by remember { mutableStateOf(BallPossession.NONE) }
    var playerStealCooldown by remember { mutableIntStateOf(0) }
    var npcStealCooldown by remember { mutableIntStateOf(0) }

    // Expansive 3D Court Dimensions (Width: -200f..200f, Length: -320f..320f)
    var playerX by remember { mutableFloatStateOf(0f) }
    var playerZ by remember { mutableFloatStateOf(-90f) }
    var playerFacingYaw by remember { mutableFloatStateOf(0f) }
    var playerWalkTime by remember { mutableFloatStateOf(0f) }

    // REAL 3D ORBIT CAMERA CONTROLS (Turn view 360 degrees - Elevated Camera Angle)
    var cameraYaw by remember { mutableFloatStateOf(0f) }
    var cameraPitch by remember { mutableFloatStateOf(0.36f) }

    var npcX by remember { mutableFloatStateOf(24f) }
    var npcZ by remember { mutableFloatStateOf(110f) }
    var npcFacingYaw by remember { mutableFloatStateOf(3.14159f) }
    var npcWalkTime by remember { mutableFloatStateOf(0f) }

    var ballX by remember { mutableFloatStateOf(16f) }
    var ballY by remember { mutableFloatStateOf(0f) }
    var ballZ by remember { mutableFloatStateOf(20f) }
    var ballVx by remember { mutableFloatStateOf(0f) }
    var ballVy by remember { mutableFloatStateOf(0f) }
    var ballVz by remember { mutableFloatStateOf(0f) }

    var joystickDeltaX by remember { mutableFloatStateOf(0f) }
    var joystickDeltaY by remember { mutableFloatStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()

    // Countdown Timer
    LaunchedEffect(isPaused, timeLeft) {
        if (!isPaused && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        } else if (timeLeft <= 0 && !showGameOverDialog) {
            showGameOverDialog = true
            viewModel.rewardMatchCoins(50)
        }
    }

    // 60FPS 3D Physics Loop
    LaunchedEffect(isPaused) {
        while (!isPaused && timeLeft > 0) {
            delay(16L)

            // 1. Move Player relative to Camera Angle on Big Court
            val isMoving = joystickDeltaX != 0f || joystickDeltaY != 0f
            if (isMoving) {
                val baseSpeed = (customization.speed.toFloat() / 65f) * 2.8f
                val sprintMultiplier = if (isSprinting) 1.8f else 1.0f
                val speed = baseSpeed * sprintMultiplier

                val forwardX = sin(cameraYaw)
                val forwardZ = cos(cameraYaw)
                val rightX = cos(cameraYaw)
                val rightZ = -sin(cameraYaw)

                val moveX = joystickDeltaX * rightX - joystickDeltaY * forwardX
                val moveZ = joystickDeltaX * rightZ - joystickDeltaY * forwardZ

                playerX = (playerX + moveX * speed).coerceIn(-175f, 175f)
                playerZ = (playerZ + moveZ * speed).coerceIn(-295f, 295f)

                playerFacingYaw = atan2(moveX, moveZ)
                playerWalkTime += if (isSprinting) 0.65f else 0.35f
            } else {
                playerWalkTime = 0f
            }

            if (playerStealCooldown > 0) playerStealCooldown--
            if (npcStealCooldown > 0) npcStealCooldown--

            val dxPB = ballX - playerX
            val dzPB = ballZ - playerZ
            val distPB = sqrt(dxPB * dxPB + dzPB * dzPB)

            // 1. AUTOMATIC BALL CONTROL / TACKLE FOR PLAYER:
            // If player gets within 28f of the ball, player dominates the ball!
            if (playerStealCooldown == 0 && distPB < 28f && ballPossession != BallPossession.PLAYER) {
                ballPossession = BallPossession.PLAYER
                npcStealCooldown = 35
            }

            // 2. BALL BEHAVIOR BASED ON POSSESSION
            when (ballPossession) {
                BallPossession.PLAYER -> {
                    val forwardX = sin(playerFacingYaw)
                    val forwardZ = cos(playerFacingYaw)
                    val stride = if (isSprinting) 13f else 9.5f
                    ballX = playerX + forwardX * stride
                    ballY = 0f
                    ballZ = playerZ + forwardZ * stride
                    ballVx = forwardX * (if (isSprinting) 5f else 3f)
                    ballVz = forwardZ * (if (isSprinting) 5f else 3f)
                    ballVy = 0f

                    // NPC tries to mark player
                    val dxNpcP = playerX - npcX
                    val dzNpcP = playerZ - npcZ
                    val distNpcP = sqrt(dxNpcP * dxNpcP + dzNpcP * dzNpcP)
                    if (distNpcP > 20f) {
                        npcX += (dxNpcP / distNpcP) * 1.5f
                        npcZ += (dzNpcP / distNpcP) * 1.5f
                        npcFacingYaw = atan2(dxNpcP, dzNpcP)
                        npcWalkTime += 0.28f
                    }
                }
                BallPossession.NPC -> {
                    val forwardX = sin(npcFacingYaw)
                    val forwardZ = cos(npcFacingYaw)
                    ballX = npcX + forwardX * 10f
                    ballY = 0f
                    ballZ = npcZ + forwardZ * 10f

                    // NPC dribbles towards player's goal (-Z)
                    npcZ -= 1.4f
                    npcFacingYaw = 3.14159f
                    npcWalkTime += 0.28f

                    // If player gets close (tackle/steal), player takes the ball!
                    val distNpcPlayer = sqrt((playerX - npcX) * (playerX - npcX) + (playerZ - npcZ) * (playerZ - npcZ))
                    if (distNpcPlayer < 30f) {
                        ballPossession = BallPossession.PLAYER
                        npcStealCooldown = 40
                    }
                }
                BallPossession.NONE -> {
                    // NPC chases loose ball
                    val dxNpcBall = ballX - npcX
                    val dzNpcBall = ballZ - npcZ
                    val distNpcBall = sqrt(dxNpcBall * dxNpcBall + dzNpcBall * dzNpcBall)
                    if (distNpcBall > 12f) {
                        npcX += (dxNpcBall / distNpcBall) * 1.6f
                        npcZ += (dzNpcBall / distNpcBall) * 1.6f
                        npcFacingYaw = atan2(dxNpcBall, dzNpcBall)
                        npcWalkTime += 0.28f
                    } else if (npcStealCooldown == 0) {
                        ballPossession = BallPossession.NPC
                    }

                    // Free ball physics
                    ballX += ballVx
                    ballY += ballVy
                    ballZ += ballVz

                    if (ballY < 0f) {
                        ballVy += 0.85f
                    }
                    if (ballY >= 0f) {
                        ballY = 0f
                        if (ballVy > 1.4f) {
                            ballVy = -ballVy * 0.6f
                        } else {
                            ballVy = 0f
                        }
                        ballVx *= 0.96f
                        ballVz *= 0.96f
                    }
                }
            }

            // Wall Bounces on Big Court (X: -190f to 190f)
            if (ballX < -190f || ballX > 190f) {
                ballVx = -ballVx * 0.7f
                ballX = ballX.coerceIn(-190f, 190f)
            }

            // Far Goal Scoring - Enlarged Goal (Opponent Goal at Z >= 295f, width -70f..70f, height > -52f)
            if (ballZ >= 295f && ballX in -70f..70f && ballY > -52f) {
                playerScore += 1
                showGoalBanner = "GOOOOL DO VÁRZEA FC!"
                ballPossession = BallPossession.NONE
                ballX = 0f
                ballY = 0f
                ballZ = 0f
                ballVx = 0f
                ballVy = 0f
                ballVz = 0f
                playerX = 0f
                playerZ = -90f
                npcX = 24f
                npcZ = 110f
            }
            // Near Goal Scoring - Enlarged Goal (Your Goal at Z <= -295f, width -70f..70f, height > -52f)
            else if (ballZ <= -295f && ballX in -70f..70f && ballY > -52f) {
                npcScore += 1
                showGoalBanner = "GOL DO ADVERSÁRIO!"
                ballPossession = BallPossession.NONE
                ballX = 0f
                ballY = 0f
                ballZ = 0f
                ballVx = 0f
                ballVy = 0f
                ballVz = 0f
                playerX = 0f
                playerZ = -90f
                npcX = 24f
                npcZ = 110f
            }

            ballZ = ballZ.coerceIn(-315f, 315f)
        }
    }

    LaunchedEffect(showGoalBanner) {
        if (showGoalBanner != null) {
            delay(2500L)
            showGoalBanner = null
        }
    }

    // 3D Camera Orbit - Elevated view from higher up (mais pra cima) looking down at the player and court
    val camDist = 82f
    val camPos = Vec3(
        x = playerX - sin(cameraYaw) * cos(cameraPitch) * camDist,
        y = -sin(cameraPitch) * camDist - 34f, // Positioned higher up in the air
        z = playerZ - cos(cameraYaw) * cos(cameraPitch) * camDist
    )
    val camera = Camera3D(
        position = camPos,
        pitchRad = cameraPitch,
        yawRad = cameraYaw,
        fov = 360f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF86ACD8))
            // Drag anywhere to rotate camera in 360 degrees
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    cameraYaw += dragAmount.x * 0.006f
                    cameraPitch = (cameraPitch - dragAmount.y * 0.003f).coerceIn(0.20f, 0.65f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = size.width
            val sh = size.height

            // 1. Sky & Stadium Sunshade Rafters (Over wide arena)
            drawLarge3DStadiumRoofAndSky(camera, sw, sh)

            // 2. Huge Concrete Pitch Floor & Markings
            drawLarge3DConcretePitch(camera, sw, sh)

            // 3. 3D Stadium Architecture (Navy wall left, Red bleachers right, Crowd far, Ice-blue banners)
            drawLarge3DStadiumArchitecture(camera, sw, sh, settings.torcidaEnabled)

            // 4. 3D Goals (Far Goal with Red Net + Near Goal)
            drawLarge3DGoals(camera, sw, sh)

            // 5. 3D Opponent NPC (Scaled to fit court)
            val npcCustom = customization.copy(
                skinColorHex = "#E8B282",
                shirtColorHex = "#EC4899",
                shortsColorHex = "#EA580C",
                socksColorHex = "#2563EB",
                hairStyleId = 1
            )
            draw3DPlayer(
                worldPos = Vec3(npcX, 0f, npcZ),
                yawRad = npcFacingYaw,
                animTime = npcWalkTime,
                customization = npcCustom,
                camera = camera,
                screenW = sw,
                screenH = sh,
                scale = 1.0f
            )

            // 6. 3D Soccer Ball
            drawLarge3DSoccerBall(
                pos = Vec3(ballX, ballY, ballZ),
                camera = camera,
                screenW = sw,
                screenH = sh
            )

            // 7. 3D Player "VOCÊ" (Zoomed, clear and prominent)
            draw3DPlayer(
                worldPos = Vec3(playerX, 0f, playerZ),
                yawRad = playerFacingYaw,
                animTime = playerWalkTime,
                customization = customization,
                camera = camera,
                screenW = sw,
                screenH = sh,
                scale = 1.05f
            )
        }

        // Top Scoreboard (Screenshot detail)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(38.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0x882A2E38))
                    .border(1.dp, Color(0x66718096), RoundedCornerShape(3.dp))
                    .clickable {
                        isPaused = true
                        showPauseMenu = true
                    }
                    .testTag("match_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Pausa",
                    tint = Color(0xFFD1D5DB),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(Color(0xFF38BDF8))
                        .padding(horizontal = 26.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VOCÊ $playerScore - $npcScore NPC",
                        color = Color(0xFF032B56),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                        .background(Color.White)
                        .padding(horizontal = 30.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$timeLeft",
                        color = Color(0xFF10B981),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 14.dp, start = 18.dp, end = 18.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Analog Joystick
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .border(2.dp, Color(0x66FFFFFF), CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { },
                                onDragEnd = {
                                    joystickDeltaX = 0f
                                    joystickDeltaY = 0f
                                },
                                onDragCancel = {
                                    joystickDeltaX = 0f
                                    joystickDeltaY = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val maxDist = 44f
                                    val nx = (joystickDeltaX * maxDist + dragAmount.x).coerceIn(-maxDist, maxDist)
                                    val ny = (joystickDeltaY * maxDist + dragAmount.y).coerceIn(-maxDist, maxDist)
                                    joystickDeltaX = nx / maxDist
                                    joystickDeltaY = ny / maxDist
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (joystickDeltaX * 36).toInt(),
                                    (joystickDeltaY * 36).toInt()
                                )
                            }
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC00E5FF))
                            .border(2.dp, Color.White, CircleShape)
                    )
                }

                // Action Buttons: CORRER, DRIBLE, PASSE, CHUTE
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // CORRER (Sprint Button - explicitly requested)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(if (isSprinting) Color(0xFFFF9800) else Color(0xCC1E293B))
                            .border(2.dp, if (isSprinting) Color(0xFFFFD600) else Color.White, CircleShape)
                            .clickable { isSprinting = !isSprinting }
                            .testTag("correr_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚡", fontSize = 16.sp)
                            Text(
                                text = if (isSprinting) "CORRENDO" else "CORRER",
                                color = if (isSprinting) Color(0xFF1E1E1E) else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // PEGAR / DRIBLE Button (Explicitly requested system to take/grab the ball!)
                    val hasBall = ballPossession == BallPossession.PLAYER
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (hasBall) Color(0xCC0284C7) else Color(0xEE10B981))
                            .border(2.5.dp, if (hasBall) Color.White else Color(0xFFFFD600), CircleShape)
                            .clickable {
                                if (hasBall) {
                                    // Dribble boost
                                    val forwardX = sin(playerFacingYaw)
                                    val forwardZ = cos(playerFacingYaw)
                                    ballVx += forwardX * 7f
                                    ballVz += forwardZ * 7f
                                } else {
                                    // Instant take / grab the ball!
                                    val dx = ballX - playerX
                                    val dz = ballZ - playerZ
                                    val dist = sqrt(dx * dx + dz * dz)
                                    if (dist < 60f) {
                                        ballPossession = BallPossession.PLAYER
                                        playerStealCooldown = 0
                                        npcStealCooldown = 40
                                    } else {
                                        // Rush towards the ball
                                        val dirX = dx / dist
                                        val dirZ = dz / dist
                                        playerFacingYaw = atan2(dx, dz)
                                        playerX += dirX * 18f
                                        playerZ += dirZ * 18f
                                        if (dist < 32f) {
                                            ballPossession = BallPossession.PLAYER
                                            playerStealCooldown = 0
                                            npcStealCooldown = 40
                                        }
                                    }
                                }
                            }
                            .testTag("pegar_drible_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (hasBall) "⚡" else "🧲", fontSize = 14.sp)
                            Text(
                                text = if (hasBall) "DRIBLE" else "PEGAR",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // PASSE Button
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC059669))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                val forwardX = sin(playerFacingYaw)
                                val forwardZ = cos(playerFacingYaw)
                                val dx = ballX - playerX
                                val dz = ballZ - playerZ
                                val dist = sqrt(dx * dx + dz * dz)
                                if (hasBall || dist < 36f) {
                                    ballPossession = BallPossession.NONE
                                    playerStealCooldown = 18
                                    ballVx = forwardX * 14f
                                    ballVy = -2f
                                    ballVz = forwardZ * 14f
                                }
                            }
                            .testTag("passe_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PASSE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // CHUTE Button (Large red button with soccer ball icon)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626))
                            .border(2.5.dp, Color(0xFFFFD600), CircleShape)
                            .clickable {
                                val kickPower = (customization.kick.toFloat() / 65f) * 26f
                                val dx = ballX - playerX
                                val dz = ballZ - playerZ
                                val dist = sqrt(dx * dx + dz * dz)
                                if (hasBall || dist < 38f) {
                                    val kickDirX = if (dist > 0.1f) dx / dist else sin(playerFacingYaw)
                                    val kickDirZ = if (dist > 0.1f) dz / dist else cos(playerFacingYaw)
                                    ballPossession = BallPossession.NONE
                                    playerStealCooldown = 22
                                    ballVx = kickDirX * kickPower * 0.92f
                                    ballVy = -16f
                                    ballVz = kickDirZ * kickPower * 0.92f
                                }
                            }
                            .testTag("chutar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Chutar",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "CHUTE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // GOOOOL Animated Banner
        AnimatedVisibility(
            visible = showGoalBanner != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xEE090D16))
                    .border(3.dp, Color(0xFFFFD600), RoundedCornerShape(12.dp))
                    .padding(horizontal = 32.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = showGoalBanner ?: "",
                    color = Color(0xFFFFD600),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        // Pause Menu Dialog
        if (showPauseMenu) {
            AlertDialog(
                onDismissRequest = {
                    showPauseMenu = false
                    isPaused = false
                },
                title = { Text("JOGO PAUSADO", fontWeight = FontWeight.Black) },
                text = {
                    Column {
                        Text("Placar Atual: VOCÊ $playerScore - $npcScore NPC")
                        Text("Tempo Restante: $timeLeft s")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPauseMenu = false
                            isPaused = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("CONTINUAR")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPauseMenu = false
                            isPaused = false
                            viewModel.navigateTo(ScreenState.MAIN_MENU)
                        }
                    ) {
                        Text("SAIR PARA O MENU")
                    }
                }
            )
        }

        // Game Over Dialog
        if (showGameOverDialog) {
            val isWin = playerScore > npcScore
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        if (isWin) "VITÓRIA HISTÓRICA!" else if (playerScore == npcScore) "EMPATE NA VÁRZEA!" else "FIM DE JOGO!",
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column {
                        Text("Resultado Final: VOCÊ $playerScore - $npcScore NPC", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+50 Moedas salvas no banco de dados!", color = Color(0xFF00B4D8), fontWeight = FontWeight.Black)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showGameOverDialog = false
                            viewModel.navigateTo(ScreenState.MAIN_MENU)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("VOLTAR AO MENU")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showGameOverDialog = false
                            timeLeft = initialSeconds
                            playerScore = 0
                            npcScore = 0
                            isPaused = false
                        }
                    ) {
                        Text("JOGAR NOVAMENTE")
                    }
                }
            )
        }
    }
}

/**
 * 1. Sky & Overhead 3D Metal Sunshade Slats
 */
private fun DrawScope.drawLarge3DStadiumRoofAndSky(camera: Camera3D, sw: Float, sh: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF86ACD8), Color(0xFFA5C5E6), Color(0xFFD6E4F0)),
            startY = 0f,
            endY = sh * 0.55f
        ),
        size = Size(sw, sh)
    )

    val courtW = 200f
    val halfL = 320f
    val slatH = -110f

    for (i in 0..6) {
        val y = slatH - i * 10f
        // Left slats
        draw3DLine(
            Vec3(-courtW, y, -halfL),
            Vec3(-courtW, y, halfL),
            color = Color(0xFFD8DEE9).copy(alpha = 0.85f),
            camera = camera,
            screenW = sw,
            screenH = sh,
            strokeWidth = 3.5f
        )
        // Right slats
        draw3DLine(
            Vec3(courtW, y, -halfL),
            Vec3(courtW, y, halfL),
            color = Color(0xFFD8DEE9).copy(alpha = 0.85f),
            camera = camera,
            screenW = sw,
            screenH = sh,
            strokeWidth = 3.5f
        )
        // Back slats
        draw3DLine(
            Vec3(-courtW, y, halfL),
            Vec3(courtW, y, halfL),
            color = Color(0xFFE2E8F0).copy(alpha = 0.9f),
            camera = camera,
            screenW = sw,
            screenH = sh,
            strokeWidth = 3f
        )
    }
}

/**
 * 2. High-Fidelity Textured Concrete Pitch & Regulation Futsal Markings
 */
private fun DrawScope.drawLarge3DConcretePitch(camera: Camera3D, sw: Float, sh: Float) {
    val courtW = 200f
    val halfL = 320f

    // 1. Grid of individual concrete slabs across the entire pitch
    val cols = 16
    val rows = 20
    val slabW = (courtW * 2f) / cols  // 25f per slab
    val slabL = (halfL * 2f) / rows   // 32f per slab

    for (c in 0 until cols) {
        val x1 = -courtW + c * slabW
        val x2 = x1 + slabW
        val midX = (x1 + x2) / 2f
        val isRunoffX = midX < -180f || midX > 180f

        for (r in 0 until rows) {
            val z1 = -halfL + r * slabL
            val z2 = z1 + slabL
            val midZ = (z1 + z2) / 2f
            val isRunoffZ = midZ < -300f || midZ > 300f
            val isRunoff = isRunoffX || isRunoffZ

            // Deterministic pseudo-random hash for realistic slab tone variation
            val h = ((c * 73856093) xor (r * 19349663)) and 0x7FFFFFFF
            val slabColor = if (!isRunoff) {
                // Interior concrete court tones (realistic light grey/silver court)
                when (h % 8) {
                    0 -> Color(0xFFA6ABB6) // Crisp light concrete
                    1 -> Color(0xFF9CA1AC) // Cool grey
                    2 -> Color(0xFFA1A6B1) // Silvery slab
                    3 -> Color(0xFF979CA6) // Medium grey
                    4 -> Color(0xFFA8ADB8) // Pale concrete
                    5 -> Color(0xFF9398A2) // Weathered concrete
                    6 -> Color(0xFF9EA3AE) // Clean slab
                    else -> Color(0xFF9196A0) // Dense concrete
                }
            } else {
                // Perimeter runoff asphalt / dark concrete border
                when (h % 3) {
                    0 -> Color(0xFF646973)
                    1 -> Color(0xFF5C616B)
                    else -> Color(0xFF6B707A)
                }
            }

            // Draw slab quad with expansion joint outline
            draw3DQuad(
                v1 = Vec3(x1, 0f, z1),
                v2 = Vec3(x2, 0f, z1),
                v3 = Vec3(x2, 0f, z2),
                v4 = Vec3(x1, 0f, z2),
                color = slabColor,
                camera = camera,
                screenW = sw,
                screenH = sh,
                outlineColor = Color(0xFF4C515D).copy(alpha = 0.65f),
                strokeWidth = 1.2f
            )

            // Beveled highlight line on north edge of the slab for authentic 3D slab depth
            draw3DLine(
                Vec3(x1 + 1f, 0f, z1 + 0.8f),
                Vec3(x2 - 1f, 0f, z1 + 0.8f),
                color = Color(0xFFBCC1CB).copy(alpha = 0.45f),
                camera = camera,
                screenW = sw,
                screenH = sh,
                strokeWidth = 1f
            )

            // Concrete aggregate micro-stippling (grit / surface texture)
            if (!isRunoff && (h % 2 == 0)) {
                val speckleOffsetX = ((h % 17) - 8.5f) * 0.8f
                val speckleOffsetZ = (((h / 17) % 21) - 10.5f) * 0.8f
                val pProj = camera.project(Vec3(midX + speckleOffsetX, 0f, midZ + speckleOffsetZ), sw, sh)
                if (pProj != null) {
                    val dotColor = if (h % 4 == 0) Color(0x33000000) else Color(0x33FFFFFF)
                    drawCircle(dotColor, radius = 1.6f * pProj.scale, center = pProj.screenPos)
                }
            }

            // Sneaker scuffs / high-wear rubber skid marks in action zones (center & penalty areas)
            if (!isRunoff && (midX * midX + midZ * midZ < 8000f || midZ > 180f || midZ < -180f)) {
                if (h % 5 == 0) {
                    val scuffAngle = (h % 31) * 0.1f
                    val scuffLen = 4f + (h % 5)
                    val sx1 = midX - sin(scuffAngle) * scuffLen
                    val sz1 = midZ - cos(scuffAngle) * scuffLen
                    val sx2 = midX + sin(scuffAngle) * scuffLen
                    val sz2 = midZ + cos(scuffAngle) * scuffLen
                    draw3DLine(
                        Vec3(sx1, 0f, sz1),
                        Vec3(sx2, 0f, sz2),
                        color = Color(0x30282828),
                        camera = camera,
                        screenW = sw,
                        screenH = sh,
                        strokeWidth = 1.4f
                    )
                }
            }
        }
    }

    // 2. Curb / Drainage Grate separating court runoff from advertising boards
    val curbColor = Color(0xFF383C45)
    draw3DLine(Vec3(-198f, 0f, -318f), Vec3(-198f, 0f, 318f), curbColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(198f, 0f, -318f), Vec3(198f, 0f, 318f), curbColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(-198f, 0f, 318f), Vec3(198f, 0f, 318f), curbColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(-198f, 0f, -318f), Vec3(198f, 0f, -318f), curbColor, camera, sw, sh, 2.5f)

    // 3. Crisp White Regulation Court Markings
    val lineColor = Color.White
    val fieldW = 180f
    val fieldL = 300f

    // Outer Boundary Touchlines
    draw3DLine(Vec3(-fieldW, 0f, -fieldL), Vec3(-fieldW, 0f, fieldL), lineColor, camera, sw, sh, 3.5f)
    draw3DLine(Vec3(fieldW, 0f, -fieldL), Vec3(fieldW, 0f, fieldL), lineColor, camera, sw, sh, 3.5f)

    // Goal Lines
    draw3DLine(Vec3(-fieldW, 0f, fieldL), Vec3(fieldW, 0f, fieldL), lineColor, camera, sw, sh, 3.5f)
    draw3DLine(Vec3(-fieldW, 0f, -fieldL), Vec3(fieldW, 0f, -fieldL), lineColor, camera, sw, sh, 3.5f)

    // Midfield Line
    draw3DLine(Vec3(-fieldW, 0f, 0f), Vec3(fieldW, 0f, 0f), lineColor, camera, sw, sh, 3.2f)

    // Center Circle (Radius 48f)
    var prevPt: Vec3? = null
    var firstPt: Vec3? = null
    val cr = 48f
    for (deg in 0..360 step 15) {
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val curPt = Vec3(cos(rad) * cr, 0f, sin(rad) * cr)
        if (firstPt == null) firstPt = curPt
        if (prevPt != null) {
            draw3DLine(prevPt, curPt, lineColor, camera, sw, sh, 3f)
        }
        prevPt = curPt
    }
    if (firstPt != null && prevPt != null) {
        draw3DLine(prevPt, firstPt, lineColor, camera, sw, sh, 3f)
    }

    // Center Kick-Off Spot
    val centerProj = camera.project(Vec3(0f, 0f, 0f), sw, sh)
    if (centerProj != null) {
        drawCircle(lineColor, radius = 4f * centerProj.scale, center = centerProj.screenPos)
    }

    // Far Penalty Box (Grande Área - Scaled for large goal: X in -125..125, Z from 300 to 205)
    draw3DLine(Vec3(-125f, 0f, fieldL), Vec3(-125f, 0f, 205f), lineColor, camera, sw, sh, 3.2f)
    draw3DLine(Vec3(125f, 0f, fieldL), Vec3(125f, 0f, 205f), lineColor, camera, sw, sh, 3.2f)
    draw3DLine(Vec3(-125f, 0f, 205f), Vec3(125f, 0f, 205f), lineColor, camera, sw, sh, 3.2f)

    // Far Goal Area (Pequena Área - Scaled for large goal: X in -85..85, Z from 300 to 255)
    draw3DLine(Vec3(-85f, 0f, fieldL), Vec3(-85f, 0f, 255f), lineColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(85f, 0f, fieldL), Vec3(85f, 0f, 255f), lineColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(-85f, 0f, 255f), Vec3(85f, 0f, 255f), lineColor, camera, sw, sh, 2.5f)

    // Far Penalty Spot (0, 235f)
    val farPenProj = camera.project(Vec3(0f, 0f, 235f), sw, sh)
    if (farPenProj != null) {
        drawCircle(lineColor, radius = 3.8f * farPenProj.scale, center = farPenProj.screenPos)
    }

    // Far Penalty Arc (Meia-lua)
    var prevArc: Vec3? = null
    val arcR = 32f
    for (deg in 130..230 step 10) {
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val pt = Vec3(sin(rad) * arcR, 0f, 235f + cos(rad) * arcR)
        if (prevArc != null) {
            draw3DLine(prevArc, pt, lineColor, camera, sw, sh, 2.5f)
        }
        prevArc = pt
    }

    // Near Penalty Box (Grande Área: X in -125..125, Z from -300 to -205)
    draw3DLine(Vec3(-125f, 0f, -fieldL), Vec3(-125f, 0f, -205f), lineColor, camera, sw, sh, 3.2f)
    draw3DLine(Vec3(125f, 0f, -fieldL), Vec3(125f, 0f, -205f), lineColor, camera, sw, sh, 3.2f)
    draw3DLine(Vec3(-125f, 0f, -205f), Vec3(125f, 0f, -205f), lineColor, camera, sw, sh, 3.2f)

    // Near Goal Area (Pequena Área: X in -85..85, Z from -300 to -255)
    draw3DLine(Vec3(-85f, 0f, -fieldL), Vec3(-85f, 0f, -255f), lineColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(85f, 0f, -fieldL), Vec3(85f, 0f, -255f), lineColor, camera, sw, sh, 2.5f)
    draw3DLine(Vec3(-85f, 0f, -255f), Vec3(85f, 0f, -255f), lineColor, camera, sw, sh, 2.5f)

    // Near Penalty Spot (0, -235f)
    val nearPenProj = camera.project(Vec3(0f, 0f, -235f), sw, sh)
    if (nearPenProj != null) {
        drawCircle(lineColor, radius = 3.8f * nearPenProj.scale, center = nearPenProj.screenPos)
    }

    // Near Penalty Arc (Meia-lua)
    var prevNearArc: Vec3? = null
    for (deg in -50..50 step 10) {
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val pt = Vec3(sin(rad) * arcR, 0f, -235f + cos(rad) * arcR)
        if (prevNearArc != null) {
            draw3DLine(prevNearArc, pt, lineColor, camera, sw, sh, 2.5f)
        }
        prevNearArc = pt
    }

    // 4 Corner Arcs (Escanteios)
    val cornerR = 14f
    drawCornerArc(Vec3(fieldW, 0f, fieldL), 180, 270, cornerR, camera, sw, sh)
    drawCornerArc(Vec3(-fieldW, 0f, fieldL), 270, 360, cornerR, camera, sw, sh)
    drawCornerArc(Vec3(fieldW, 0f, -fieldL), 90, 180, cornerR, camera, sw, sh)
    drawCornerArc(Vec3(-fieldW, 0f, -fieldL), 0, 90, cornerR, camera, sw, sh)
}

/**
 * Helper to draw 3D quarter-circle corner arcs
 */
private fun DrawScope.drawCornerArc(
    center: Vec3,
    startDeg: Int,
    endDeg: Int,
    radius: Float,
    camera: Camera3D,
    sw: Float,
    sh: Float
) {
    var prev: Vec3? = null
    for (deg in startDeg..endDeg step 15) {
        val rad = Math.toRadians(deg.toDouble()).toFloat()
        val pt = Vec3(center.x + cos(rad) * radius, 0f, center.z + sin(rad) * radius)
        if (prev != null) {
            draw3DLine(prev, pt, Color.White, camera, sw, sh, 2.5f)
        }
        prev = pt
    }
}

/**
 * 3. 3D Stadium Architecture (Navy wall left, Red bleachers right, Crowd center back, Ice-blue Várzea 27 hoardings)
 */
private fun DrawScope.drawLarge3DStadiumArchitecture(
    camera: Camera3D,
    sw: Float,
    sh: Float,
    torcidaEnabled: Boolean
) {
    val courtW = 200f
    val halfL = 320f
    val wallH = -95f

    // 1. LEFT NAVY WALL WITH SLATS / WINDOWS
    draw3DQuad(
        v1 = Vec3(-courtW, wallH, -halfL),
        v2 = Vec3(-courtW, wallH, halfL),
        v3 = Vec3(-courtW, 0f, halfL),
        v4 = Vec3(-courtW, 0f, -halfL),
        color = Color(0xFF1E2B45),
        camera = camera,
        screenW = sw,
        screenH = sh
    )
    for (step in 1..5) {
        val y = wallH * (step / 6f)
        draw3DLine(
            Vec3(-courtW, y, -halfL),
            Vec3(-courtW, y, halfL),
            color = Color.White.copy(alpha = 0.45f),
            camera = camera,
            screenW = sw,
            screenH = sh,
            strokeWidth = 2f
        )
    }

    // 2. RIGHT RED BLEACHERS
    draw3DQuad(
        v1 = Vec3(courtW + 70f, wallH, -halfL),
        v2 = Vec3(courtW + 70f, wallH, halfL),
        v3 = Vec3(courtW, 0f, halfL),
        v4 = Vec3(courtW, 0f, -halfL),
        color = Color(0xFFB91C1C),
        camera = camera,
        screenW = sw,
        screenH = sh
    )
    for (i in 1..4) {
        val frac = i / 5f
        val x = courtW + 70f * frac
        val y = wallH * frac
        draw3DLine(Vec3(x, y, -halfL), Vec3(x, y, halfL), Color(0xFF991B1B), camera, sw, sh, 2.5f)
    }

    // 3. BACK RED BLEACHERS WITH VIBRANT CROWD
    draw3DQuad(
        v1 = Vec3(-courtW, wallH, halfL + 75f),
        v2 = Vec3(courtW, wallH, halfL + 75f),
        v3 = Vec3(courtW, 0f, halfL),
        v4 = Vec3(-courtW, 0f, halfL),
        color = Color(0xFFDC2626),
        camera = camera,
        screenW = sw,
        screenH = sh
    )

    // Crowd spectator spectators in 3D (dense rows of fans in team shirts!)
    if (torcidaEnabled) {
        for (step in 1..5) {
            val frac = step / 6f
            val y = wallH * frac - 4f
            val z = halfL + 75f * frac
            for (col in -16..16) {
                val x = col * 12f
                val dotProj = camera.project(Vec3(x, y, z), sw, sh)
                if (dotProj != null) {
                    val dotColor = when ((col * 3 + step * 2) % 6) {
                        0 -> Color(0xFFFFD600) // Yellow
                        1 -> Color.White        // White
                        2 -> Color(0xFF2563EB) // Blue
                        3 -> Color(0xFF10B981) // Green
                        4 -> Color(0xFFEF4444) // Red
                        else -> Color(0xFF0F172A)
                    }
                    drawCircle(dotColor, radius = 3.5f * dotProj.scale, center = dotProj.screenPos)
                }
            }
        }
    }

    // 4. ICE-BLUE "VÁRZEA 27" ADVERTISING HOARDINGS (Detailed repeating banners matching Image 5)
    val bannerH = -22f

    // Helper to draw segmented hoardings with styled "VÁRZEA 27" graphics
    // Far Back Hoardings (Z = halfL)
    val farPanels = 8
    val farStep = (courtW * 2f) / farPanels
    for (i in 0 until farPanels) {
        val x1 = -courtW + i * farStep
        val x2 = x1 + farStep
        val pColor = if (i % 2 == 0) Color(0xFF7DD3FC) else Color(0xFF38BDF8)
        draw3DQuad(
            v1 = Vec3(x1, bannerH, halfL),
            v2 = Vec3(x2, bannerH, halfL),
            v3 = Vec3(x2, 0f, halfL),
            v4 = Vec3(x1, 0f, halfL),
            color = pColor,
            camera = camera,
            screenW = sw,
            screenH = sh,
            outlineColor = Color(0xFF0284C7),
            strokeWidth = 1.8f
        )
        // Top aluminum rail
        draw3DLine(Vec3(x1, bannerH, halfL), Vec3(x2, bannerH, halfL), Color.White, camera, sw, sh, 2.5f)
        // Stylized emblem in center of panel
        val midX = (x1 + x2) / 2f
        draw3DLine(
            Vec3(midX - 10f, bannerH * 0.5f, halfL - 0.2f),
            Vec3(midX + 10f, bannerH * 0.5f, halfL - 0.2f),
            Color(0xFFFFD600),
            camera,
            sw,
            sh,
            2.2f
        )
    }

    // Left Side Hoardings (X = -courtW)
    val sidePanels = 10
    val sideStep = (halfL * 2f) / sidePanels
    for (i in 0 until sidePanels) {
        val z1 = -halfL + i * sideStep
        val z2 = z1 + sideStep
        val pColor = if (i % 2 == 0) Color(0xFF38BDF8) else Color(0xFF7DD3FC)
        draw3DQuad(
            v1 = Vec3(-courtW, bannerH, z1),
            v2 = Vec3(-courtW, bannerH, z2),
            v3 = Vec3(-courtW, 0f, z2),
            v4 = Vec3(-courtW, 0f, z1),
            color = pColor,
            camera = camera,
            screenW = sw,
            screenH = sh,
            outlineColor = Color(0xFF0284C7),
            strokeWidth = 1.8f
        )
        draw3DLine(Vec3(-courtW, bannerH, z1), Vec3(-courtW, bannerH, z2), Color.White, camera, sw, sh, 2.5f)
    }

    // Right Side Hoardings (X = courtW)
    for (i in 0 until sidePanels) {
        val z1 = halfL - i * sideStep
        val z2 = z1 - sideStep
        val pColor = if (i % 2 == 0) Color(0xFF38BDF8) else Color(0xFF7DD3FC)
        draw3DQuad(
            v1 = Vec3(courtW, bannerH, z1),
            v2 = Vec3(courtW, bannerH, z2),
            v3 = Vec3(courtW, 0f, z2),
            v4 = Vec3(courtW, 0f, z1),
            color = pColor,
            camera = camera,
            screenW = sw,
            screenH = sh,
            outlineColor = Color(0xFF0284C7),
            strokeWidth = 1.8f
        )
        draw3DLine(Vec3(courtW, bannerH, z1), Vec3(courtW, bannerH, z2), Color.White, camera, sw, sh, 2.5f)
    }

    // Near Hoardings (Z = -halfL)
    for (i in 0 until farPanels) {
        val x1 = courtW - i * farStep
        val x2 = x1 - farStep
        val pColor = if (i % 2 == 0) Color(0xFF7DD3FC) else Color(0xFF38BDF8)
        draw3DQuad(
            v1 = Vec3(x1, bannerH, -halfL),
            v2 = Vec3(x2, bannerH, -halfL),
            v3 = Vec3(x2, 0f, -halfL),
            v4 = Vec3(x1, 0f, -halfL),
            color = pColor,
            camera = camera,
            screenW = sw,
            screenH = sh,
            outlineColor = Color(0xFF0284C7),
            strokeWidth = 1.8f
        )
        draw3DLine(Vec3(x1, bannerH, -halfL), Vec3(x2, bannerH, -halfL), Color.White, camera, sw, sh, 2.5f)
    }
}

/**
 * 4. 3D Large Stadium Goals with Full Red Net Geometry
 */
private fun DrawScope.drawLarge3DGoals(camera: Camera3D, sw: Float, sh: Float) {
    val goalW = 68f
    val goalH = -48f
    val halfL = 320f
    val netDepth = 36f

    // 1. Far Goal (Opponent Goal at Z = halfL)
    // Red Net Backdrop
    draw3DQuad(
        v1 = Vec3(-goalW, goalH, halfL + netDepth),
        v2 = Vec3(goalW, goalH, halfL + netDepth),
        v3 = Vec3(goalW, 0f, halfL + netDepth),
        v4 = Vec3(-goalW, 0f, halfL + netDepth),
        color = Color(0xFFDC2626),
        camera = camera,
        screenW = sw,
        screenH = sh,
        outlineColor = Color.White.copy(alpha = 0.5f),
        strokeWidth = 1.5f
    )
    // Left Side Net
    draw3DQuad(
        v1 = Vec3(-goalW, goalH, halfL),
        v2 = Vec3(-goalW, goalH, halfL + netDepth),
        v3 = Vec3(-goalW, 0f, halfL + netDepth),
        v4 = Vec3(-goalW, 0f, halfL),
        color = Color(0xFFB91C1C),
        camera = camera,
        screenW = sw,
        screenH = sh,
        outlineColor = Color.White.copy(alpha = 0.4f),
        strokeWidth = 1.2f
    )
    // Right Side Net
    draw3DQuad(
        v1 = Vec3(goalW, goalH, halfL + netDepth),
        v2 = Vec3(goalW, goalH, halfL),
        v3 = Vec3(goalW, 0f, halfL),
        v4 = Vec3(goalW, 0f, halfL + netDepth),
        color = Color(0xFFB91C1C),
        camera = camera,
        screenW = sw,
        screenH = sh,
        outlineColor = Color.White.copy(alpha = 0.4f),
        strokeWidth = 1.2f
    )
    // Top Net Roof
    draw3DQuad(
        v1 = Vec3(-goalW, goalH, halfL),
        v2 = Vec3(goalW, goalH, halfL),
        v3 = Vec3(goalW, goalH, halfL + netDepth),
        v4 = Vec3(-goalW, goalH, halfL + netDepth),
        color = Color(0xFFEF4444),
        camera = camera,
        screenW = sw,
        screenH = sh,
        outlineColor = Color.White.copy(alpha = 0.4f),
        strokeWidth = 1.2f
    )
    // Net Mesh Grid Lines
    for (step in 1..4) {
        val frac = step / 5f
        val y = goalH * frac
        draw3DLine(
            Vec3(-goalW, y, halfL + netDepth),
            Vec3(goalW, y, halfL + netDepth),
            Color.White.copy(alpha = 0.4f),
            camera,
            sw,
            sh,
            1.2f
        )
    }
    // White Posts & Crossbar (Sturdy 5f width lines)
    draw3DLine(Vec3(-goalW, 0f, halfL), Vec3(-goalW, goalH, halfL), Color.White, camera, sw, sh, 5f)
    draw3DLine(Vec3(goalW, 0f, halfL), Vec3(goalW, goalH, halfL), Color.White, camera, sw, sh, 5f)
    draw3DLine(Vec3(-goalW, goalH, halfL), Vec3(goalW, goalH, halfL), Color.White, camera, sw, sh, 5.5f)
    // Ground support bars
    draw3DLine(Vec3(-goalW, 0f, halfL), Vec3(-goalW, 0f, halfL + netDepth), Color(0xFFCBD5E1), camera, sw, sh, 3f)
    draw3DLine(Vec3(goalW, 0f, halfL), Vec3(goalW, 0f, halfL + netDepth), Color(0xFFCBD5E1), camera, sw, sh, 3f)
    draw3DLine(Vec3(-goalW, 0f, halfL + netDepth), Vec3(goalW, 0f, halfL + netDepth), Color(0xFF94A3B8), camera, sw, sh, 3f)

    // 2. Near Goal (Your Goal at Z = -halfL)
    draw3DQuad(
        v1 = Vec3(-goalW, goalH, -halfL - netDepth),
        v2 = Vec3(goalW, goalH, -halfL - netDepth),
        v3 = Vec3(goalW, 0f, -halfL - netDepth),
        v4 = Vec3(-goalW, 0f, -halfL - netDepth),
        color = Color(0xFFDC2626),
        camera = camera,
        screenW = sw,
        screenH = sh
    )
    draw3DLine(Vec3(-goalW, 0f, -halfL), Vec3(-goalW, goalH, -halfL), Color.White, camera, sw, sh, 5f)
    draw3DLine(Vec3(goalW, 0f, -halfL), Vec3(goalW, goalH, -halfL), Color.White, camera, sw, sh, 5f)
    draw3DLine(Vec3(-goalW, goalH, -halfL), Vec3(goalW, goalH, -halfL), Color.White, camera, sw, sh, 5.5f)
}

/**
 * 5. 3D Soccer Ball
 */
private fun DrawScope.drawLarge3DSoccerBall(pos: Vec3, camera: Camera3D, screenW: Float, screenH: Float) {
    val shadowProj = camera.project(Vec3(pos.x, 0f, pos.z), screenW, screenH)
    if (shadowProj != null) {
        val heightOffset = -pos.y
        val shadowScale = (1f - (heightOffset / 50f).coerceIn(0f, 0.5f))
        val sW = 14f * shadowProj.scale * shadowScale
        val sH = 6f * shadowProj.scale * shadowScale
        drawOval(
            color = Color(0x66000000),
            topLeft = Offset(shadowProj.screenPos.x - sW / 2f, shadowProj.screenPos.y - sH / 2f),
            size = Size(sW, sH)
        )
    }

    val ballProj = camera.project(pos, screenW, screenH)
    if (ballProj != null) {
        val r = 5.2f * ballProj.scale
        drawCircle(color = Color.White, radius = r, center = ballProj.screenPos)
        drawCircle(color = Color(0xFF374151), radius = r, center = ballProj.screenPos, style = Stroke(width = 1.2f))

        drawCircle(color = Color(0xFF1E1E1E), radius = r * 0.38f, center = ballProj.screenPos)
        drawCircle(color = Color(0xFF1E1E1E), radius = r * 0.22f, center = ballProj.screenPos + Offset(-r * 0.45f, -r * 0.35f))
        drawCircle(color = Color(0xFF1E1E1E), radius = r * 0.22f, center = ballProj.screenPos + Offset(r * 0.45f, -r * 0.35f))
    }
}

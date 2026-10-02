package com.example.ui.graphics3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.data.db.PlayerCustomizationEntity
import com.example.ui.components.parseHexColor
import kotlin.math.sin

/**
 * High-definition 3D Voxel Character Renderer with modular separated textures:
 * - Hairstyles (0..8) with unique voxel structures, dreadlocks, fades, headbands, and beards
 * - Fabric mesh jerseys with ribbed collars, badges, and special jersey decals
 * - Athletic shorts with side racing stripes & elastic waistband
 * - Ribbed soccer socks with classic double hoop stripes
 * - Molded cleats with sole plates, laces, and athletic side streaks
 * - Goalkeeper / athletic gloves with wristbands
 * - Expressive 3D face with eyes, pupils, eyebrows, and ears
 */
fun DrawScope.draw3DPlayer(
    worldPos: Vec3,
    yawRad: Float,
    animTime: Float,
    customization: PlayerCustomizationEntity,
    camera: Camera3D,
    screenW: Float,
    screenH: Float,
    scale: Float = 1.0f
) {
    val skinColor = parseHexColor(customization.skinColorHex, Color(0xFFE8B282))
    val hairColor = parseHexColor(customization.hairColorHex, Color(0xFF212121))
    val shirtColor = parseHexColor(customization.shirtColorHex, Color(0xFF68BBE3))
    val shortsColor = parseHexColor(customization.shortsColorHex, Color(0xFFFFFFFF))
    val socksColor = parseHexColor(customization.socksColorHex, Color(0xFF68BBE3))
    val hasGloves = customization.glovesColorHex != "none"
    val glovesColor = if (hasGloves) parseHexColor(customization.glovesColorHex, Color(0xFF1E1E1E)) else null
    val bootsColor = Color(0xFF181818)
    val bootsAccentColor = Color(0xFFE53935) // Red athletic accent streak

    // 0. Soft Ground Contact Shadow
    val footProjected = camera.project(Vec3(worldPos.x, 0f, worldPos.z), screenW, screenH)
    if (footProjected != null) {
        val shadowW = 32f * footProjected.scale * scale
        val shadowH = 14f * footProjected.scale * scale
        drawOval(
            color = Color(0x77000000),
            topLeft = Offset(footProjected.screenPos.x - shadowW / 2f, footProjected.screenPos.y - shadowH / 2f),
            size = Size(shadowW, shadowH)
        )
    }

    // Walking animation cycle
    val legSwing = sin(animTime) * 13f * scale
    val armSwing = -sin(animTime) * 11f * scale
    val u = 1.0f * scale

    // -------------------------------------------------------------
    // 1. LEGS, SOCKS & CLEATS (With Separated Textures)
    // -------------------------------------------------------------
    // Left Leg
    val leftLegPos = worldPos + Vec3(-4.5f * u, -12f * u, legSwing).rotateY(yawRad)
    draw3DBox(
        center = leftLegPos,
        size = Vec3(4.6f * u, 14f * u, 5.2f * u),
        baseColor = socksColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Left Sock Texture: Classic Double Hoop Top Stripes (White)
    val leftSockStripePos = leftLegPos + Vec3(0f, -4.5f * u, 0f)
    draw3DBox(
        center = leftSockStripePos,
        size = Vec3(4.9f * u, 2.2f * u, 5.5f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Left Boot / Cleat (Base + Laces + Sole)
    val leftBootPos = worldPos + Vec3(-4.5f * u, -3.2f * u, legSwing + 1.8f * u).rotateY(yawRad)
    draw3DBox(
        center = leftBootPos,
        size = Vec3(5.2f * u, 6.2f * u, 8.6f * u),
        baseColor = bootsColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Left Cleat Sole Plate
    val leftSolePos = leftBootPos + Vec3(0f, 2.6f * u, 0f)
    draw3DBox(
        center = leftSolePos,
        size = Vec3(5.4f * u, 1.2f * u, 8.8f * u),
        baseColor = bootsAccentColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Left Cleat Laces Decal (Top instep)
    val leftLacesPos = leftBootPos + Vec3(0f, -2.2f * u, -0.5f * u).rotateY(yawRad)
    draw3DBox(
        center = leftLacesPos,
        size = Vec3(3.2f * u, 0.8f * u, 4.5f * u),
        baseColor = Color(0xFFEEEEEE),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Right Leg
    val rightLegPos = worldPos + Vec3(4.5f * u, -12f * u, -legSwing).rotateY(yawRad)
    draw3DBox(
        center = rightLegPos,
        size = Vec3(4.6f * u, 14f * u, 5.2f * u),
        baseColor = socksColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Right Sock Texture: Classic Double Hoop Top Stripes (White)
    val rightSockStripePos = rightLegPos + Vec3(0f, -4.5f * u, 0f)
    draw3DBox(
        center = rightSockStripePos,
        size = Vec3(4.9f * u, 2.2f * u, 5.5f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Right Boot / Cleat (Base + Laces + Sole)
    val rightBootPos = worldPos + Vec3(4.5f * u, -3.2f * u, -legSwing + 1.8f * u).rotateY(yawRad)
    draw3DBox(
        center = rightBootPos,
        size = Vec3(5.2f * u, 6.2f * u, 8.6f * u),
        baseColor = bootsColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Right Cleat Sole Plate
    val rightSolePos = rightBootPos + Vec3(0f, 2.6f * u, 0f)
    draw3DBox(
        center = rightSolePos,
        size = Vec3(5.4f * u, 1.2f * u, 8.8f * u),
        baseColor = bootsAccentColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Right Cleat Laces Decal (Top instep)
    val rightLacesPos = rightBootPos + Vec3(0f, -2.2f * u, -0.5f * u).rotateY(yawRad)
    draw3DBox(
        center = rightLacesPos,
        size = Vec3(3.2f * u, 0.8f * u, 4.5f * u),
        baseColor = Color(0xFFEEEEEE),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // -------------------------------------------------------------
    // 2. SHORTS (With Lateral Racing Stripes & Waistband)
    // -------------------------------------------------------------
    val waistPos = worldPos + Vec3(0f, -22f * u, 0f)
    draw3DBox(
        center = waistPos,
        size = Vec3(14.5f * u, 10f * u, 8.8f * u),
        baseColor = shortsColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Shorts Texture: Elastic Waistband
    val waistbandPos = waistPos + Vec3(0f, -4.2f * u, 0f)
    draw3DBox(
        center = waistbandPos,
        size = Vec3(14.8f * u, 2f * u, 9.1f * u),
        baseColor = Color(0x33000000),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Shorts Texture: Left & Right Flank Athletic Racing Stripes
    val stripeColor = if (shortsColor == Color.White) shirtColor else Color.White
    val leftShortsStripePos = waistPos + Vec3(-7.4f * u, 0.5f * u, 0f).rotateY(yawRad)
    draw3DBox(
        center = leftShortsStripePos,
        size = Vec3(0.5f * u, 8f * u, 3f * u),
        baseColor = stripeColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    val rightShortsStripePos = waistPos + Vec3(7.4f * u, 0.5f * u, 0f).rotateY(yawRad)
    draw3DBox(
        center = rightShortsStripePos,
        size = Vec3(0.5f * u, 8f * u, 3f * u),
        baseColor = stripeColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // -------------------------------------------------------------
    // 3. TORSO & JERSEY TEXTURES (Separated Graphics & Collars)
    // -------------------------------------------------------------
    val torsoPos = worldPos + Vec3(0f, -34.5f * u, 0f)
    draw3DBox(
        center = torsoPos,
        size = Vec3(16f * u, 16.5f * u, 9.2f * u),
        baseColor = shirtColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )

    // Ribbed Collar Texture (Neck rim)
    val collarPos = torsoPos + Vec3(0f, -7.8f * u, -2.5f * u).rotateY(yawRad)
    draw3DBox(
        center = collarPos,
        size = Vec3(6.5f * u, 1.8f * u, 4.5f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Lateral Athletic Mesh Panels on Torso (Side stripes)
    val leftTorsoStripePos = torsoPos + Vec3(-8.1f * u, 0f, 0f).rotateY(yawRad)
    draw3DBox(
        center = leftTorsoStripePos,
        size = Vec3(0.4f * u, 14f * u, 4f * u),
        baseColor = Color.White.copy(alpha = 0.85f),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    val rightTorsoStripePos = torsoPos + Vec3(8.1f * u, 0f, 0f).rotateY(yawRad)
    draw3DBox(
        center = rightTorsoStripePos,
        size = Vec3(0.4f * u, 14f * u, 4f * u),
        baseColor = Color.White.copy(alpha = 0.85f),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Dynamic Special Jersey Chest Graphics in 3D
    val chestDecalFrontPos = torsoPos + Vec3(0f, -1.5f * u, -4.75f * u).rotateY(yawRad)
    when (customization.specialJersey) {
        "VARZEA_3D" -> {
            // Neon Cyan Cyber Number "27" & Chevron badge
            draw3DBox(
                center = chestDecalFrontPos,
                size = Vec3(9f * u, 5.5f * u, 0.4f * u),
                baseColor = Color(0xFF00E5FF),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            val chestCorePos = chestDecalFrontPos + Vec3(0f, 0f, -0.3f * u).rotateY(yawRad)
            draw3DBox(
                center = chestCorePos,
                size = Vec3(5.5f * u, 3f * u, 0.3f * u),
                baseColor = Color.White,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        "JESUS_TE_AMA" -> {
            // White Banner & Holy Cross emblem
            draw3DBox(
                center = chestDecalFrontPos,
                size = Vec3(11f * u, 4f * u, 0.4f * u),
                baseColor = Color.White,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            // Cross
            val crossVPos = chestDecalFrontPos + Vec3(0f, -0.2f * u, -0.3f * u).rotateY(yawRad)
            draw3DBox(
                center = crossVPos,
                size = Vec3(1.8f * u, 4.5f * u, 0.3f * u),
                baseColor = Color(0xFFB71C1C),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        "FIRE" -> {
            // Layered Flame Voxels rising from bottom
            val flamePos = torsoPos + Vec3(0f, 3.5f * u, -4.75f * u).rotateY(yawRad)
            draw3DBox(
                center = flamePos,
                size = Vec3(11f * u, 6f * u, 0.4f * u),
                baseColor = Color(0xFFFF9100),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            val innerFlamePos = flamePos + Vec3(0f, 1f * u, -0.3f * u).rotateY(yawRad)
            draw3DBox(
                center = innerFlamePos,
                size = Vec3(6f * u, 4f * u, 0.3f * u),
                baseColor = Color(0xFFFFEA00),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        "PRIME" -> {
            // Luxury Golden Honeycomb & Carbon Inset
            draw3DBox(
                center = chestDecalFrontPos,
                size = Vec3(8.5f * u, 8.5f * u, 0.4f * u),
                baseColor = Color(0xFFFFD700),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            val innerPrimePos = chestDecalFrontPos + Vec3(0f, 0f, -0.3f * u).rotateY(yawRad)
            draw3DBox(
                center = innerPrimePos,
                size = Vec3(5.5f * u, 5.5f * u, 0.3f * u),
                baseColor = Color(0xFF212121),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        "CAMISA_10" -> {
            // Green & Yellow Brasil #10 Badge
            draw3DBox(
                center = chestDecalFrontPos,
                size = Vec3(7.5f * u, 6.5f * u, 0.4f * u),
                baseColor = Color(0xFF00C853),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            val numberTenPos = chestDecalFrontPos + Vec3(0f, 0f, -0.3f * u).rotateY(yawRad)
            draw3DBox(
                center = numberTenPos,
                size = Vec3(4.5f * u, 4.5f * u, 0.3f * u),
                baseColor = Color(0xFFFFD600),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        "NINJA" -> {
            // Crossed red ninja ribbons across chest
            draw3DBox(
                center = chestDecalFrontPos,
                size = Vec3(13f * u, 2f * u, 0.4f * u),
                baseColor = Color(0xFFD50000),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        else -> {
            // Classic Várzea Athletic Shield Emblem on Left Chest
            val crestPos = torsoPos + Vec3(-4f * u, -2.5f * u, -4.75f * u).rotateY(yawRad)
            draw3DBox(
                center = crestPos,
                size = Vec3(3.6f * u, 4.2f * u, 0.4f * u),
                baseColor = Color.White,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            val crestInner = crestPos + Vec3(0f, 0.2f * u, -0.2f * u).rotateY(yawRad)
            draw3DBox(
                center = crestInner,
                size = Vec3(2.4f * u, 2.8f * u, 0.2f * u),
                baseColor = Color(0xFFD50000),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
    }

    // -------------------------------------------------------------
    // 4. ARMS & GLOVES (With Sleeves, Cuffs & Glove Padding)
    // -------------------------------------------------------------
    // Left Arm (Sleeve + Forearm/Glove)
    val leftArmPos = worldPos + Vec3(-10.8f * u, -32f * u, armSwing).rotateY(yawRad)
    // Upper Arm Sleeve
    val leftShoulderPos = leftArmPos + Vec3(0f, -4.5f * u, 0f)
    draw3DBox(
        center = leftShoulderPos,
        size = Vec3(4.8f * u, 6.5f * u, 5.2f * u),
        baseColor = shirtColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    // Sleeve White Cuff Band
    val leftCuffPos = leftShoulderPos + Vec3(0f, 3.2f * u, 0f)
    draw3DBox(
        center = leftCuffPos,
        size = Vec3(5.0f * u, 1.2f * u, 5.4f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Lower Arm (Hand / Glove)
    val leftHandPos = leftArmPos + Vec3(0f, 3.5f * u, 0f)
    val handColor = glovesColor ?: skinColor
    draw3DBox(
        center = leftHandPos,
        size = Vec3(4.5f * u, 8.5f * u, 4.8f * u),
        baseColor = handColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    if (hasGloves) {
        // Glove Wristband Strap in 3D
        val leftGloveStrap = leftHandPos + Vec3(0f, -2.8f * u, 0f)
        draw3DBox(
            center = leftGloveStrap,
            size = Vec3(4.8f * u, 1.8f * u, 5.2f * u),
            baseColor = Color(0xFF00E5FF),
            yawRad = yawRad,
            camera = camera,
            screenW = screenW,
            screenH = screenH,
            outlineColor = null
        )
    }

    // Right Arm (Sleeve + Forearm/Glove)
    val rightArmPos = worldPos + Vec3(10.8f * u, -32f * u, -armSwing).rotateY(yawRad)
    val rightShoulderPos = rightArmPos + Vec3(0f, -4.5f * u, 0f)
    draw3DBox(
        center = rightShoulderPos,
        size = Vec3(4.8f * u, 6.5f * u, 5.2f * u),
        baseColor = shirtColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    val rightCuffPos = rightShoulderPos + Vec3(0f, 3.2f * u, 0f)
    draw3DBox(
        center = rightCuffPos,
        size = Vec3(5.0f * u, 1.2f * u, 5.4f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    val rightHandPos = rightArmPos + Vec3(0f, 3.5f * u, 0f)
    draw3DBox(
        center = rightHandPos,
        size = Vec3(4.5f * u, 8.5f * u, 4.8f * u),
        baseColor = handColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )
    if (hasGloves) {
        val rightGloveStrap = rightHandPos + Vec3(0f, -2.8f * u, 0f)
        draw3DBox(
            center = rightGloveStrap,
            size = Vec3(4.8f * u, 1.8f * u, 5.2f * u),
            baseColor = Color(0xFF00E5FF),
            yawRad = yawRad,
            camera = camera,
            screenW = screenW,
            screenH = screenH,
            outlineColor = null
        )
    }

    // -------------------------------------------------------------
    // 5. HEAD & EXPRESSIVE 3D FACE (Eyes, Iris, Eyebrows, Ears)
    // -------------------------------------------------------------
    val headPos = worldPos + Vec3(0f, -48f * u, 0f)
    draw3DBox(
        center = headPos,
        size = Vec3(13.5f * u, 13.5f * u, 13.5f * u),
        baseColor = skinColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH
    )

    // Left Ear & Right Ear Voxels
    val leftEarPos = headPos + Vec3(-7f * u, 0f, 0f).rotateY(yawRad)
    draw3DBox(
        center = leftEarPos,
        size = Vec3(0.8f * u, 3f * u, 2f * u),
        baseColor = skinColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    val rightEarPos = headPos + Vec3(7f * u, 0f, 0f).rotateY(yawRad)
    draw3DBox(
        center = rightEarPos,
        size = Vec3(0.8f * u, 3f * u, 2f * u),
        baseColor = skinColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // 3D Eyes (Front face: -z is forward in character local space)
    val faceForwardOffset = -6.85f * u
    // Left Eye White Sclera
    val leftEyeWhite = headPos + Vec3(-3.2f * u, -0.5f * u, faceForwardOffset).rotateY(yawRad)
    draw3DBox(
        center = leftEyeWhite,
        size = Vec3(2.4f * u, 2.2f * u, 0.3f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Left Pupil / Iris
    val leftPupil = leftEyeWhite + Vec3(0.2f * u, 0f, -0.2f * u).rotateY(yawRad)
    draw3DBox(
        center = leftPupil,
        size = Vec3(1.3f * u, 1.8f * u, 0.2f * u),
        baseColor = Color(0xFF1E1E1E),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Right Eye White Sclera
    val rightEyeWhite = headPos + Vec3(3.2f * u, -0.5f * u, faceForwardOffset).rotateY(yawRad)
    draw3DBox(
        center = rightEyeWhite,
        size = Vec3(2.4f * u, 2.2f * u, 0.3f * u),
        baseColor = Color.White,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    // Right Pupil / Iris
    val rightPupil = rightEyeWhite + Vec3(-0.2f * u, 0f, -0.2f * u).rotateY(yawRad)
    draw3DBox(
        center = rightPupil,
        size = Vec3(1.3f * u, 1.8f * u, 0.2f * u),
        baseColor = Color(0xFF1E1E1E),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Eyebrows in 3D
    val leftBrow = headPos + Vec3(-3.2f * u, -2.4f * u, faceForwardOffset).rotateY(yawRad)
    draw3DBox(
        center = leftBrow,
        size = Vec3(3.0f * u, 0.8f * u, 0.3f * u),
        baseColor = hairColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )
    val rightBrow = headPos + Vec3(3.2f * u, -2.4f * u, faceForwardOffset).rotateY(yawRad)
    draw3DBox(
        center = rightBrow,
        size = Vec3(3.0f * u, 0.8f * u, 0.3f * u),
        baseColor = hairColor,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // Confident Street Smirk Mouth
    val mouthPos = headPos + Vec3(0.5f * u, 3.2f * u, faceForwardOffset).rotateY(yawRad)
    draw3DBox(
        center = mouthPos,
        size = Vec3(3.2f * u, 0.9f * u, 0.3f * u),
        baseColor = Color(0xFF3E2723),
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        outlineColor = null
    )

    // -------------------------------------------------------------
    // 6. HAIRSTYLES IN 3D (All 9 Styles With Dedicated Textures)
    // -------------------------------------------------------------
    draw3DHairstyle(
        headPos = headPos,
        hairColor = hairColor,
        styleId = customization.hairStyleId,
        yawRad = yawRad,
        camera = camera,
        screenW = screenW,
        screenH = screenH,
        u = u
    )
}

/**
 * Renders all 9 distinct 3D hairstyles with volumetric voxels, headbands, dreadlocks, fades, and beards.
 */
private fun DrawScope.draw3DHairstyle(
    headPos: Vec3,
    hairColor: Color,
    styleId: Int,
    yawRad: Float,
    camera: Camera3D,
    screenW: Float,
    screenH: Float,
    u: Float
) {
    when (styleId) {
        0 -> {
            // Bald: Subtle razor hairline edge texture
            val hairlinePos = headPos + Vec3(0f, -5.2f * u, -4.5f * u).rotateY(yawRad)
            draw3DBox(
                center = hairlinePos,
                size = Vec3(13.6f * u, 1.2f * u, 4.5f * u),
                baseColor = hairColor.copy(alpha = 0.25f),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        1 -> {
            // Buzzcut: Low-profile textured hair cap with temple fade
            val buzzCap = headPos + Vec3(0f, -6.0f * u, 0.5f * u).rotateY(yawRad)
            draw3DBox(
                center = buzzCap,
                size = Vec3(14.0f * u, 3.5f * u, 14.0f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
        }
        2 -> {
            // Long Dreads: Volumetric crown + Dangling dreadlock voxels with gold bead rings
            val dreadCrown = headPos + Vec3(0f, -6.5f * u, 0.5f * u).rotateY(yawRad)
            draw3DBox(
                center = dreadCrown,
                size = Vec3(14.5f * u, 4.5f * u, 14.5f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Left dangling dreads
            val leftDread = headPos + Vec3(-6.8f * u, 0f, 1f * u).rotateY(yawRad)
            draw3DBox(
                center = leftDread,
                size = Vec3(2.5f * u, 11f * u, 4f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Left Dread Gold Bead Ring
            val leftBead = leftDread + Vec3(0f, 3f * u, 0f)
            draw3DBox(
                center = leftBead,
                size = Vec3(2.8f * u, 1.6f * u, 4.3f * u),
                baseColor = Color(0xFFFFD700),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            // Right dangling dreads
            val rightDread = headPos + Vec3(6.8f * u, 0f, 1f * u).rotateY(yawRad)
            draw3DBox(
                center = rightDread,
                size = Vec3(2.5f * u, 11f * u, 4f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Right Dread Gold Bead Ring
            val rightBead = rightDread + Vec3(0f, 3f * u, 0f)
            draw3DBox(
                center = rightBead,
                size = Vec3(2.8f * u, 1.6f * u, 4.3f * u),
                baseColor = Color(0xFFFFD700),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        3 -> {
            // Spiky Voxels: Top cap with protruding 3D spikes and tip highlights
            val spikyBase = headPos + Vec3(0f, -6.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = spikyBase,
                size = Vec3(14.2f * u, 4f * u, 14.2f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Center High Spike
            val centerSpike = headPos + Vec3(0f, -9.5f * u, -1f * u).rotateY(yawRad)
            draw3DBox(
                center = centerSpike,
                size = Vec3(4f * u, 4.5f * u, 5f * u),
                baseColor = Color(0xFFFFD600), // Blonde highlighted tip
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Left Spike
            val leftSpike = headPos + Vec3(-3.8f * u, -8.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = leftSpike,
                size = Vec3(3.2f * u, 3.5f * u, 4f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Right Spike
            val rightSpike = headPos + Vec3(3.8f * u, -8.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = rightSpike,
                size = Vec3(3.2f * u, 3.5f * u, 4f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
        }
        4 -> {
            // Side Fade with Razor Slash: Comb-over hair cap with white razor stripe
            val fadeTop = headPos + Vec3(0f, -6.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = fadeTop,
                size = Vec3(14.0f * u, 4f * u, 14.0f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // White Razor Slash Line on Left Temple
            val razorSlash = headPos + Vec3(-6.9f * u, -3.5f * u, -2f * u).rotateY(yawRad)
            draw3DBox(
                center = razorSlash,
                size = Vec3(0.5f * u, 0.8f * u, 5.5f * u),
                baseColor = Color.White,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        5 -> {
            // Athletic Headband + Trim Beard (Signature Várzea Look!)
            // Main Hair Top
            val hairTop = headPos + Vec3(0f, -6.8f * u, 0.5f * u).rotateY(yawRad)
            draw3DBox(
                center = hairTop,
                size = Vec3(14.4f * u, 4.5f * u, 14.4f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Front hair volume voxels
            val frontVol = headPos + Vec3(0f, -7.5f * u, -2.5f * u).rotateY(yawRad)
            draw3DBox(
                center = frontVol,
                size = Vec3(9.5f * u, 3.2f * u, 6.5f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // White Woven Elastic Headband wrapping completely around head
            val headbandPos = headPos + Vec3(0f, -4.2f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = headbandPos,
                size = Vec3(14.6f * u, 2.8f * u, 14.6f * u),
                baseColor = Color.White,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = Color(0x33000000)
            )
            // Headband Brand Emblem in front
            val bandEmblem = headPos + Vec3(0f, -4.2f * u, -7.4f * u).rotateY(yawRad)
            draw3DBox(
                center = bandEmblem,
                size = Vec3(2.5f * u, 1.2f * u, 0.4f * u),
                baseColor = Color(0xFF00E5FF),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
            // Trim 3D Beard & Goatee around jawline
            val beardJaw = headPos + Vec3(0f, 4.2f * u, -1.5f * u).rotateY(yawRad)
            draw3DBox(
                center = beardJaw,
                size = Vec3(13.8f * u, 5.5f * u, 12.0f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
        }
        6 -> {
            // Modern Mohawk: Narrow central crest in 3D with dyed highlights
            val mohawkCenter = headPos + Vec3(0f, -8.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = mohawkCenter,
                size = Vec3(4.8f * u, 6f * u, 14.5f * u),
                baseColor = Color(0xFFC47C35), // Dyed bronze / gold
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Base dark stubble on temples
            val stubbleBase = headPos + Vec3(0f, -5.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = stubbleBase,
                size = Vec3(13.8f * u, 2f * u, 13.8f * u),
                baseColor = hairColor.copy(alpha = 0.6f),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        7 -> {
            // Striped Yellow/Black Band with Afro Curls
            val curlsTop = headPos + Vec3(0f, -7.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = curlsTop,
                size = Vec3(14.8f * u, 5f * u, 14.8f * u),
                baseColor = hairColor,
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Striped Headband (Yellow & Black stripes)
            val stripedBand = headPos + Vec3(0f, -4.5f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = stripedBand,
                size = Vec3(14.9f * u, 3f * u, 14.9f * u),
                baseColor = Color(0xFFFFD600), // Vibrant yellow
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            // Black stripes on band
            val bandStripeFront = headPos + Vec3(0f, -4.5f * u, -7.55f * u).rotateY(yawRad)
            draw3DBox(
                center = bandStripeFront,
                size = Vec3(3.5f * u, 3f * u, 0.3f * u),
                baseColor = Color(0xFF212121),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH,
                outlineColor = null
            )
        }
        8 -> {
            // Layered Wavy/Curly Locks: Rich textured ginger/auburn waves
            val wavyCrown = headPos + Vec3(0f, -7.2f * u, 0f).rotateY(yawRad)
            draw3DBox(
                center = wavyCrown,
                size = Vec3(14.6f * u, 5f * u, 14.6f * u),
                baseColor = Color(0xFFD84315),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
            val wavyHighlight = headPos + Vec3(0f, -8.2f * u, -2f * u).rotateY(yawRad)
            draw3DBox(
                center = wavyHighlight,
                size = Vec3(9.0f * u, 3f * u, 6.0f * u),
                baseColor = Color(0xFFFF7043),
                yawRad = yawRad,
                camera = camera,
                screenW = screenW,
                screenH = screenH
            )
        }
    }
}

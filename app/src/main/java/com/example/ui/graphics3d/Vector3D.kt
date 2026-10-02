package com.example.ui.graphics3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)

    fun rotateY(angleRad: Float): Vec3 {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vec3(
            x = x * cosA + z * sinA,
            y = y,
            z = -x * sinA + z * cosA
        )
    }

    fun rotateX(angleRad: Float): Vec3 {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vec3(
            x = x,
            y = y * cosA - z * sinA,
            z = y * sinA + z * cosA
        )
    }
}

data class Camera3D(
    val position: Vec3 = Vec3(0f, -80f, -140f),
    val pitchRad: Float = 0.32f, // Tilt looking down at the pitch
    val yawRad: Float = 0.0f,    // Turn left/right 360 degrees
    val fov: Float = 440f
) {
    fun project(point: Vec3, screenW: Float, screenH: Float): ProjectedPoint? {
        val rel = point - position

        // 1. Rotate by camera yaw (horizontal turn)
        val rotY = rel.rotateY(-yawRad)
        // 2. Rotate by camera pitch (vertical tilt)
        val rotX = rotY.rotateX(-pitchRad)

        // Near clipping plane
        if (rotX.z <= 6f) return null

        val scale = fov / rotX.z
        val screenX = screenW / 2f + rotX.x * scale
        val screenY = screenH / 2f + rotX.y * scale

        return ProjectedPoint(
            screenPos = Offset(screenX, screenY),
            depth = rotX.z,
            scale = scale
        )
    }
}

data class ProjectedPoint(
    val screenPos: Offset,
    val depth: Float,
    val scale: Float
)

/**
 * Draws a 3D Quad in world space.
 */
fun DrawScope.draw3DQuad(
    v1: Vec3,
    v2: Vec3,
    v3: Vec3,
    v4: Vec3,
    color: Color,
    camera: Camera3D,
    screenW: Float,
    screenH: Float,
    outlineColor: Color? = null,
    strokeWidth: Float = 1.5f
) {
    val p1 = camera.project(v1, screenW, screenH) ?: return
    val p2 = camera.project(v2, screenW, screenH) ?: return
    val p3 = camera.project(v3, screenW, screenH) ?: return
    val p4 = camera.project(v4, screenW, screenH) ?: return

    val path = Path().apply {
        moveTo(p1.screenPos.x, p1.screenPos.y)
        lineTo(p2.screenPos.x, p2.screenPos.y)
        lineTo(p3.screenPos.x, p3.screenPos.y)
        lineTo(p4.screenPos.x, p4.screenPos.y)
        close()
    }

    drawPath(path, color = color, style = Fill)
    if (outlineColor != null) {
        drawPath(path, color = outlineColor, style = Stroke(width = strokeWidth))
    }
}

/**
 * Draws a 3D line segment in world space.
 */
fun DrawScope.draw3DLine(
    v1: Vec3,
    v2: Vec3,
    color: Color,
    camera: Camera3D,
    screenW: Float,
    screenH: Float,
    strokeWidth: Float = 2f
) {
    val p1 = camera.project(v1, screenW, screenH) ?: return
    val p2 = camera.project(v2, screenW, screenH) ?: return
    drawLine(
        color = color,
        start = p1.screenPos,
        end = p2.screenPos,
        strokeWidth = strokeWidth * ((p1.scale + p2.scale) / 2f).coerceIn(0.5f, 3.5f)
    )
}

/**
 * Renders a 3D Box (Cuboid) with 6 shaded faces and sorted back-to-front by depth.
 */
fun DrawScope.draw3DBox(
    center: Vec3,
    size: Vec3, // width (x), height (y), depth (z)
    baseColor: Color,
    yawRad: Float = 0f,
    camera: Camera3D,
    screenW: Float,
    screenH: Float,
    outlineColor: Color? = Color(0x33000000)
) {
    val hx = size.x / 2f
    val hy = size.y / 2f
    val hz = size.z / 2f

    // 8 local vertices of the box
    val localVertices = listOf(
        Vec3(-hx, -hy, -hz), // 0: Top-left-front
        Vec3(hx, -hy, -hz),  // 1: Top-right-front
        Vec3(hx, hy, -hz),   // 2: Bottom-right-front
        Vec3(-hx, hy, -hz),  // 3: Bottom-left-front
        Vec3(-hx, -hy, hz),  // 4: Top-left-back
        Vec3(hx, -hy, hz),   // 5: Top-right-back
        Vec3(hx, hy, hz),    // 6: Bottom-right-back
        Vec3(-hx, hy, hz)    // 7: Bottom-left-back
    )

    // Transform vertices to world coordinates
    val worldVertices = localVertices.map { v ->
        val rotated = v.rotateY(yawRad)
        center + rotated
    }

    // 6 Faces defined by vertex indices
    data class FaceDef(val indices: List<Int>, val lightMultiplier: Float)
    val faces = listOf(
        FaceDef(listOf(0, 1, 2, 3), 0.95f), // Front
        FaceDef(listOf(5, 4, 7, 6), 0.70f), // Back
        FaceDef(listOf(4, 5, 1, 0), 1.15f), // Top
        FaceDef(listOf(3, 2, 6, 7), 0.50f), // Bottom
        FaceDef(listOf(4, 0, 3, 7), 0.85f), // Left
        FaceDef(listOf(1, 5, 6, 2), 0.78f)  // Right
    )

    // Project vertices
    val projected = worldVertices.map { camera.project(it, screenW, screenH) }

    // Sort faces back to front (Painter's algorithm)
    val sortedFaces = faces.mapNotNull { face ->
        val faceProjected = face.indices.map { projected[it] }
        if (faceProjected.any { it == null }) return@mapNotNull null

        val avgDepth = faceProjected.sumOf { it!!.depth.toDouble() }.toFloat() / 4f
        val points = faceProjected.map { it!!.screenPos }

        Triple(face, avgDepth, points)
    }.sortedByDescending { it.second }

    // Draw sorted faces
    for ((face, _, points) in sortedFaces) {
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            close()
        }

        val shadedColor = Color(
            red = (baseColor.red * face.lightMultiplier).coerceIn(0f, 1f),
            green = (baseColor.green * face.lightMultiplier).coerceIn(0f, 1f),
            blue = (baseColor.blue * face.lightMultiplier).coerceIn(0f, 1f),
            alpha = baseColor.alpha
        )

        drawPath(path, color = shadedColor, style = Fill)
        if (outlineColor != null) {
            drawPath(path, color = outlineColor, style = Stroke(width = 1.2f))
        }
    }
}

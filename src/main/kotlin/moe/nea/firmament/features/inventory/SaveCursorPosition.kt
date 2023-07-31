/*
 * SPDX-FileCopyrightText: 2023 Linnea Gräf <nea@nea.moe>
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package moe.nea.firmament.features.inventory

import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import net.minecraft.client.util.InputUtil
import moe.nea.firmament.features.FirmamentFeature
import moe.nea.firmament.gui.config.ManagedConfig
import moe.nea.firmament.util.MC
import moe.nea.firmament.util.TimeMark
import moe.nea.firmament.util.assertNotNullOr

object SaveCursorPosition : FirmamentFeature {
    override val identifier: String
        get() = "save-cursor-position"

    object TConfig : ManagedConfig(identifier) {
        val enable by toggle("enable") { true }
        val tolerance by duration("tolerance", 10.milliseconds, 5000.milliseconds) { 500.milliseconds }
    }

    override val config: TConfig
        get() = TConfig

    override fun onLoad() {

    }

    var savedPositionedP1: Pair<Double, Double>? = null
    var savedPosition: SavedPosition? = null

    data class SavedPosition(
        val middle: Pair<Double, Double>,
        val cursor: Pair<Double, Double>,
        val savedAt: TimeMark = TimeMark.now()
    )

    @JvmStatic
    fun saveCursorOriginal(positionedX: Double, positionedY: Double) {
        savedPositionedP1 = Pair(positionedX, positionedY)
    }

    @JvmStatic
    fun loadCursor(middleX: Double, middleY: Double): Pair<Double, Double>? {
        val lastPosition = savedPosition?.takeIf { it.savedAt.passedTime() < 1.seconds }
        savedPosition = null
        if (lastPosition != null &&
            (lastPosition.middle.first - middleX).absoluteValue < 1 &&
            (lastPosition.middle.second - middleY).absoluteValue < 1
        ) {
            InputUtil.setCursorParameters(
                MC.window.handle,
                InputUtil.GLFW_CURSOR_NORMAL,
                lastPosition.cursor.first,
                lastPosition.cursor.second
            )
            return lastPosition.cursor
        }
        return null
    }

    @JvmStatic
    fun saveCursorMiddle(middleX: Double, middleY: Double) {
        val cursorPos = assertNotNullOr(savedPositionedP1) { return }
        savedPosition = SavedPosition(Pair(middleX, middleY), cursorPos)
    }
}

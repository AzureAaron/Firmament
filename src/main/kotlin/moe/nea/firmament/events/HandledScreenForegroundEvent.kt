/*
 * SPDX-FileCopyrightText: 2023 Linnea Gräf <nea@nea.moe>
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package moe.nea.firmament.events

import net.minecraft.client.gui.screen.ingame.HandledScreen

data class HandledScreenForegroundEvent(
    val screen: HandledScreen<*>,
    val mouseX: Int, val mouseY: Int, val delta: Float
) : FirmamentEvent() {
    companion object : FirmamentEventBus<HandledScreenForegroundEvent>()
}

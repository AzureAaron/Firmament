/*
 * SPDX-FileCopyrightText: 2023 Linnea Gräf <nea@nea.moe>
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package moe.nea.firmament.keybindings

import org.lwjgl.glfw.GLFW
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import moe.nea.firmament.util.MC

@Serializable
data class SavedKeyBinding(
    val keyCode: Int,
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false,
) : IKeyBinding {
    constructor(keyCode: Int, mods: Triple<Boolean, Boolean, Boolean>) : this(
        keyCode,
        mods.first && keyCode != GLFW.GLFW_KEY_LEFT_SHIFT && keyCode != GLFW.GLFW_KEY_RIGHT_SHIFT,
        mods.second && keyCode != GLFW.GLFW_KEY_LEFT_CONTROL && keyCode != GLFW.GLFW_KEY_RIGHT_CONTROL,
        mods.third && keyCode != GLFW.GLFW_KEY_LEFT_ALT && keyCode != GLFW.GLFW_KEY_RIGHT_ALT,
    )

    constructor(keyCode: Int, mods: Int) : this(keyCode, getMods(mods))

    companion object {
        fun getMods(modifiers: Int): Triple<Boolean, Boolean, Boolean> {
            return Triple(
                modifiers and GLFW.GLFW_MOD_SHIFT != 0,
                modifiers and GLFW.GLFW_MOD_CONTROL != 0,
                modifiers and GLFW.GLFW_MOD_ALT != 0
            )
        }
    }

    fun hasShiftDown(): Boolean {
        return InputUtil.isKeyPressed(
            MinecraftClient.getInstance().window.handle,
            GLFW.GLFW_KEY_LEFT_SHIFT
        ) || InputUtil.isKeyPressed(
            MinecraftClient.getInstance().window.handle, GLFW.GLFW_KEY_RIGHT_SHIFT
        )
    }

    fun hasAltDown(): Boolean {
        return InputUtil.isKeyPressed(
            MinecraftClient.getInstance().window.handle,
            GLFW.GLFW_KEY_LEFT_ALT
        ) || InputUtil.isKeyPressed(
            MinecraftClient.getInstance().window.handle, GLFW.GLFW_KEY_RIGHT_ALT
        )
    }

    fun isPressed(): Boolean {
        val h = MC.window.handle
        if (!InputUtil.isKeyPressed(h, keyCode)) return false

        val ctrl = if (MinecraftClient.IS_SYSTEM_MAC) {
            InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_LEFT_SUPER)
                || InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_RIGHT_SUPER)
        } else InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_LEFT_CONTROL)
            || InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_RIGHT_CONTROL)
        val shift = InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_LEFT_SHIFT)
            || InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_RIGHT_SHIFT)
        val alt = InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_LEFT_ALT)
            || InputUtil.isKeyPressed(h, GLFW.GLFW_KEY_RIGHT_ALT)

        return (ctrl == this.ctrl) &&
            (alt == this.alt) &&
            (shift == this.shift)
    }

    override fun matches(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return keyCode == this.keyCode && getMods(modifiers) == Triple(shift, ctrl, alt)
    }

}

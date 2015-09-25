package org.spongepowered.cookbook

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.Texts
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

// Extension functions are functions written outside the class, but inside the
// class's scope. What that means is you can add methods to classes you don't
// control. Pretty nifty. These operate on Strings and Texts, to give them
// styling. For instance, "Green!".green().bold() returns a bold, green Text
// object which you can then use like any other. These functions can be chained
// as much as you like.
//
// In Kotlin, unary and binary operators are exposed as methods like plus(),
// etc. I've defined a Text.plus(String) function, a String.plus(Text)
// function, and a Text.plus(Text) function to easily combine multiple styles.
// This proves to be super useful. "Blue part".blue() + " white bit " + "red"
// .red() combines two Texts and a plain String into one Text.

fun Text.aqua(): Text = Texts.builder().color(TextColors.AQUA).append(this).toText()
fun Text.black(): Text = Texts.builder().color(TextColors.BLACK).append(this).toText()
fun Text.blue(): Text = Texts.builder().color(TextColors.BLUE).append(this).toText()
fun Text.darkAqua(): Text = Texts.builder().color(TextColors.DARK_AQUA).append(this).toText()
fun Text.darkBlue(): Text = Texts.builder().color(TextColors.DARK_BLUE).append(this).toText()
fun Text.darkGray(): Text = Texts.builder().color(TextColors.DARK_GRAY).append(this).toText()
fun Text.darkGreen(): Text = Texts.builder().color(TextColors.DARK_GREEN).append(this).toText()
fun Text.darkPurple(): Text = Texts.builder().color(TextColors.DARK_PURPLE).append(this).toText()
fun Text.darkRed(): Text = Texts.builder().color(TextColors.DARK_RED).append(this).toText()
fun Text.gold(): Text = Texts.builder().color(TextColors.GOLD).append(this).toText()
fun Text.gray(): Text = Texts.builder().color(TextColors.GRAY).append(this).toText()
fun Text.green(): Text = Texts.builder().color(TextColors.GREEN).append(this).toText()
fun Text.lightPurple(): Text = Texts.builder().color(TextColors.LIGHT_PURPLE).append(this).toText()
fun Text.red(): Text = Texts.builder().color(TextColors.RED).append(this).toText()
fun Text.yellow(): Text = Texts.builder().color(TextColors.YELLOW).append(this).toText()
fun Text.white(): Text = Texts.builder().color(TextColors.WHITE).append(this).toText()

fun Text.bold(): Text = Texts.builder().style(TextStyles.BOLD).append(this).toText()
fun Text.italic(): Text = Texts.builder().style(TextStyles.ITALIC).append(this).toText()
fun Text.obfuscated(): Text = Texts.builder().style(TextStyles.OBFUSCATED).append(this).toText()
fun Text.reset(): Text = Texts.builder().style(TextStyles.RESET).append(this).toText()
fun Text.strikethrough(): Text = Texts.builder().style(TextStyles.STRIKETHROUGH).append(this).toText()
fun Text.underline(): Text = Texts.builder().style(TextStyles.UNDERLINE).append(this).toText()

fun Text.plus(other: Text): Text = Texts.builder().append(this).append(other).toText()
fun Text.plus(other: String): Text = Texts.builder().append(this).append(Texts.of(other)).toText()

fun Text.click<T : ClickAction<*>>(action: T): Text = Texts.builder().append(this).onClick(action).toText()
fun Text.hover<T : HoverAction<*>>(action: T): Text = Texts.builder().append(this).onHover(action).toText()
fun Text.shiftClick<T : ShiftClickAction<*>>(action: T): Text = Texts.builder().append(this).onShiftClick(action).toText()

fun String.aqua(): Text = Texts.of(this).aqua()
fun String.black(): Text = Texts.of(this).black()
fun String.blue(): Text = Texts.of(this).blue()
fun String.darkAqua(): Text = Texts.of(this).darkAqua()
fun String.darkBlue(): Text = Texts.of(this).darkBlue()
fun String.darkGray(): Text = Texts.of(this).darkGray()
fun String.darkGreen(): Text = Texts.of(this).darkGreen()
fun String.darkPurple(): Text = Texts.of(this).darkPurple()
fun String.darkRed(): Text = Texts.of(this).darkRed()
fun String.gold(): Text = Texts.of(this).gold()
fun String.gray(): Text = Texts.of(this).gray()
fun String.green(): Text = Texts.of(this).green()
fun String.lightPurple(): Text = Texts.of(this).lightPurple()
fun String.red(): Text = Texts.of(this).red()
fun String.yellow(): Text = Texts.of(this).yellow()
fun String.white(): Text = Texts.of(this).white()

fun String.bold(): Text = Texts.of(this).bold()
fun String.italic(): Text = Texts.of(this).italic()
fun String.obfuscated(): Text = Texts.of(this).obfuscated()
fun String.reset(): Text = Texts.of(this).reset()
fun String.strikethrough(): Text = Texts.of(this).strikethrough()
fun String.underline(): Text = Texts.of(this).underline()

fun String.click<T : ClickAction<*>>(action: T): Text = Texts.of(this).click(action)
fun String.hover<T : HoverAction<*>>(action: T): Text = Texts.of(this).hover(action)
fun String.shiftClick<T : ShiftClickAction<*>>(action: T): Text = Texts.of(this).shiftClick(action)

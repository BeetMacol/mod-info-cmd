package com.beetmacol.mc.modinfocmd.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InfoPrinter {
	private final ServerCommandSource source;

	public InfoPrinter(ServerCommandSource source) {
		this.source = source;
	}

	public void line(Text text) {
		source.sendFeedback(text, false);
	}

	public void line(String text) {
		line(new LiteralText(text));
	}

	public void value(String parameter, String value) {
		if (value != null && !value.isEmpty())
			line(new LiteralText(parameter).styled(style -> style.withBold(true)).append(notStyled(": " + value)));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void value(String parameter, Optional<String> optionalValue) {
		optionalValue.ifPresent(value -> value(parameter, value));
	}

	public void link(String parameter, String value) {
		if (value != null && !value.isEmpty())
			line(new LiteralText(parameter).styled(style -> style.withBold(true)).append(notStyled(": ").append(new LiteralText(value).setStyle(linkStyle(value)))));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void link(String parameter, Optional<String> optionalValue) {
		optionalValue.ifPresent(value -> link(parameter, value));
	}

	public void listTitle(String text) {
		line(new LiteralText(text).styled(style -> style.withBold(true)).append(notStyled(":")));
	}

	public void listEntry(String text) {
		listEntry(new LiteralText(text));
	}

	public void listEntry(MutableText text) {
		line(new LiteralText("- ").styled(style -> style.withColor(Formatting.GRAY)).append(notStyled(text)));
	}

	public <T> void list(String title, Collection<T> collection, Function<T, @Nullable String> entryFormat) {
		listTexts(title, collection, t -> {
			String entryString = entryFormat.apply(t);
			return entryString != null ? new LiteralText(entryString) : null;
		});
	}

	public void list(String title, Collection<String> collection) {
		list(title, collection, s -> s);
	}

	public <T> void listTexts(String title, Collection<T> collection, Function<T, @Nullable MutableText> entryFormat) {
		List<MutableText> texts = new ArrayList<>();
		for (T entry : collection) {
			MutableText text = entryFormat.apply(entry);
			if (text != null)
				texts.add(text);
		}
		if (!texts.isEmpty()) {
			listTitle(title);
			for (MutableText text : texts)
				listEntry(text);
		}
	}

	public void listTexts(String title, Collection<MutableText> collection) {
		listTexts(title, collection, text -> text);
	}

	public static MutableText notStyled(String string) {
		return notStyled(new LiteralText(string));
	}

	public static MutableText notStyled(MutableText text) {
		return text.styled(style -> style.withUnderline(false).withBold(false).withItalic(false).withColor(Formatting.WHITE));
	}

	public static Style linkStyle(String link) {
		return Style.EMPTY.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
	}

	public static Style commandStyle(String command) {
		return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
	}

	public static Style clipboardStyle(String text) {
		return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text));
	}
}

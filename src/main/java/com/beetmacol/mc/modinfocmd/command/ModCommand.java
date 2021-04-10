package com.beetmacol.mc.modinfocmd.command;

import com.beetmacol.mc.modinfocmd.command.sideindependent.AnyCommandSource;
import com.beetmacol.mc.modinfocmd.command.sideindependent.UniversalCommand;
import com.beetmacol.mc.modinfocmd.command.sideindependent.UniversalCommandManager;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.beetmacol.mc.modinfocmd.ModInfoCmd.MODS;
import static com.beetmacol.mc.modinfocmd.ModInfoCmd.OUTSTANDING_CONTACT_TYPES;

@SuppressWarnings("SameParameterValue")
public class ModCommand extends UniversalCommand {
	public static final ModCommand INSTANCE = new ModCommand();

	public ModCommand() {
		super(UniversalCommandManager.literal("mod")
				.then(UniversalCommandManager.literal("list")
						.executes(context -> listMods(context.getSource()))
				)
				.then(UniversalCommandManager.literal("info")
						.then(UniversalCommandManager.argument("mod", StringArgumentType.string())
								.suggests(ModCommand::modSuggestions)
								.executes(context -> printModInfo(context.getSource(), getModId(context, "mod")))
						)
				)
		);
	}


	private static CompletableFuture<Suggestions> modSuggestions(CommandContext<AnyCommandSource> context, SuggestionsBuilder builder) {
		for (String modId : MODS.keySet())
			builder.suggest(modId);
		return builder.buildFuture();
	}


	private static final DynamicCommandExceptionType INVALID_MOD = new DynamicCommandExceptionType(id -> new LiteralText("Unknown mod '" + id + "'"));
	private static String getModId(CommandContext<AnyCommandSource> context, String argument) throws CommandSyntaxException {
		String modId = StringArgumentType.getString(context, argument);
		if (!MODS.containsKey(modId))
			throw INVALID_MOD.create(modId);
		return modId;
	}


	private static int printModInfo(AnyCommandSource source, String modId) {
		ModMetadata mod = MODS.get(modId);
		InfoPrinter printer = new InfoPrinter(source);
		printer.line(new LiteralText(mod.getName()).styled(style -> style.withBold(true).withFormatting(Formatting.UNDERLINE)).append(InfoPrinter.notStyled(" (" + modId + "):")));
		printer.value("Version", mod.getVersion().getFriendlyString());
		printer.value("Description", mod.getDescription());
		if (mod.getLicense().size() == 1)
			printer.value("License", Iterables.get(mod.getLicense(), 0));
		else if (mod.getLicense().size() > 1)
			printer.list("Licenses", mod.getLicense());
		for (Map.Entry<String, String> outstandingContactEntry : OUTSTANDING_CONTACT_TYPES.entrySet())
			printer.link(outstandingContactEntry.getValue(), mod.getContact().get(outstandingContactEntry.getKey()));
		printer.listTexts("Contact", mod.getContact().asMap().entrySet(), contactEntry -> {
			if (!OUTSTANDING_CONTACT_TYPES.containsKey(contactEntry.getKey())) {
				try {
					new URL(contactEntry.getValue()); // Will throw 'MalformedURLException' if the string is not a proper URL.
					// FIXME this URL detection doesn't work with URLs that have custom schemes like 'irc'
					return new LiteralText(contactEntry.getKey() + ": ").append(new LiteralText(contactEntry.getValue()).setStyle(InfoPrinter.linkStyle(contactEntry.getValue())));
				} catch (MalformedURLException exception) {
					return new LiteralText(contactEntry.getKey() + ": ").append(new LiteralText(contactEntry.getValue()).setStyle(InfoPrinter.clipboardStyle(contactEntry.getValue()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard")))));
				}
			}
			return null;
		});
		printer.list("Authors", mod.getAuthors(), Person::getName);
		printer.list("Contributors", mod.getContributors(), Person::getName);
		return 1;
	}

	private static int listMods(AnyCommandSource source) {
		if (MODS.isEmpty()) {
			source.sendFeedback(new LiteralText("There are no mods loaded"), false);
			return 0;
		}
		source.sendFeedback(new LiteralText("There are " + MODS.size() + " mods: ").append(Texts.join(MODS.keySet(), modId -> new LiteralText(modId).setStyle(InfoPrinter.commandStyle("mod info " + modId).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click for more information")))))), false);
		return MODS.size();
	}
}

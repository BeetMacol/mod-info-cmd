package com.beetmacol.mc.modinfocmd.command.sideindependent;

import com.beetmacol.mc.modinfocmd.ModInfoCmd;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CommandNodeConverter<S> {
	static final CommandNodeConverter<ServerCommandSource> SERVER_CONVERTER = new CommandNodeConverter<>();
	static final CommandNodeConverter<FabricClientCommandSource> CLIENT_CONVERTER = new CommandNodeConverter<>();

	CommandNode<S> convertNode(CommandNode<AnyCommandSource> node) {
		if (node instanceof LiteralCommandNode)
			return convertNode((LiteralCommandNode<AnyCommandSource>) node);
		else if (node instanceof ArgumentCommandNode)
			return convertNode((ArgumentCommandNode<AnyCommandSource, ?>) node);
		else
			throw new IllegalStateException("Illegal type of a command node, cannot convert to server command node");
	}

	LiteralCommandNode<S> convertNode(LiteralCommandNode<AnyCommandSource> node) {
		LiteralCommandNode<S> converted = new LiteralCommandNode<>(
				node.getLiteral(),
				convertCommand(node.getCommand()),
				convertRequirement(node.getRequirement()),
				convertRedirect(node.getRedirect()),
				convertRedirectModifier(node.getRedirectModifier()),
				node.isFork()
		);
		for (CommandNode<AnyCommandSource> child : node.getChildren()) {
			converted.addChild(convertNode(child));
		}
		return converted;
	}

	<T> ArgumentCommandNode<S, T> convertNode(ArgumentCommandNode<AnyCommandSource, T> node) {
		ArgumentCommandNode<S, T> converted = new ArgumentCommandNode<>(
				node.getName(),
				node.getType(),
				convertCommand(node.getCommand()),
				convertRequirement(node.getRequirement()),
				convertRedirect(node.getRedirect()),
				convertRedirectModifier(node.getRedirectModifier()),
				node.isFork(),
				convertCustomSuggestions(node.getCustomSuggestions())
		);
		for (CommandNode<AnyCommandSource> child : node.getChildren()) {
			converted.addChild(convertNode(child));
		}
		return converted;
	}

	@SuppressWarnings("unchecked")
	private CommandContext<S> convertContext(CommandContext<AnyCommandSource> context) {
		if (context == null)
			return null;
		return new CommandContext<>(
				(S) context.getSource().getSpecificSource(),
				context.getInput(),
				,
				convertCommand(context.getCommand()),
				((RootNodeHolder<S>) context.getRootNode()).get(),
				,
				context.getRange(),
				convertContext(context.getChild()),
				null,
				context.isForked()
		);
	}

	private Command<S> convertCommand(Command<AnyCommandSource> command) {
		return command != null ? context -> command.run(universalContext(context)) : null;
	}

	private Predicate<S> convertRequirement(Predicate<AnyCommandSource> requirement) {
		return requirement != null ? source -> requirement.test(universalSource(source)) : null;
	}

	private CommandNode<S> convertRedirect(CommandNode<AnyCommandSource> redirect) {
		return redirect != null ? convertNode(redirect) : null;
	}

	private RedirectModifier<S> convertRedirectModifier(RedirectModifier<AnyCommandSource> redirectModifier) {
		if (redirectModifier != null) {
			ModInfoCmd.LOGGER.error("Redirect modifiers are not supported by the universal command (side independent) nodes");
		}
		return null;
	}

	private SuggestionProvider<S> convertCustomSuggestions(SuggestionProvider<AnyCommandSource> customSuggestions) {
		return customSuggestions != null ? (context, builder) -> customSuggestions.getSuggestions(universalContext(context), builder) : null;
	}

	private AnyCommandSource universalSource(S source) {
		if (source instanceof ServerCommandSource) {
			return new AnyCommandSource((ServerCommandSource) source);
		} else if (source instanceof FabricClientCommandSource) {
			return new AnyCommandSource((FabricClientCommandSource) source);
		} else {
			throw new IllegalStateException("Unsupported source type");
		}
	}

	private CommandContext<AnyCommandSource> universalContext(CommandContext<S> context) {
		if (context == null)
			return null;
		return new CommandContext<>(
				universalSource(context.getSource()),
				context.getInput(),
				,
				universalCommand(context.getCommand()),
				new RootNodeHolder<S>((RootCommandNode<S>) context.getRootNode()),
				universalNodes(context.getNodes()),
				context.getRange(),
				universalContext(context.getChild()),
				null,
				context.isForked()
		);
	}

	private Command<AnyCommandSource> universalCommand(Command<S> command) {
		return command != null ? context -> command.run(convertContext(context)) : null;
	}

	private List<ParsedCommandNode<AnyCommandSource>> universalNodes(List<ParsedCommandNode<S>> nodes) {
		List<ParsedCommandNode<AnyCommandSource>> universalNodes = new ArrayList<>(nodes.size());
		for (ParsedCommandNode<S> node : nodes) {
			universalNodes.add()
		}
	}

	private static class RootNodeHolder<S> extends RootCommandNode<AnyCommandSource> {
		private final RootCommandNode<S> specificRootNode;

		RootNodeHolder(RootCommandNode<S> specificRootNode) {
			this.specificRootNode = specificRootNode;
		}

		public RootCommandNode<S> get() {
			return specificRootNode;
		}
	}
}

package com.beetmacol.mc.modinfocmd.command.sideindependent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class UniversalCommand {
	private final LiteralArgumentBuilder<AnyCommandSource> builder;

	public UniversalCommand(LiteralArgumentBuilder<AnyCommandSource> builder) {
		this.builder = builder;
	}

	public void registerServerVersion(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandNodeConverter.SERVER_CONVERTER.convertNode(builder.build()).createBuilder());
	}

	public void registerClientVersion(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(CommandNodeConverter.CLIENT_CONVERTER.convertNode(builder.build()).createBuilder());
	}
}

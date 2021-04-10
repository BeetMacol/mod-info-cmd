package com.beetmacol.mc.modinfocmd.command.sideindependent;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AnyCommandSource {
	private final ServerCommandSource serverSource;
	private final FabricClientCommandSource clientSource;

	AnyCommandSource(ServerCommandSource serverSource) {
		this.serverSource = serverSource;
		this.clientSource = null;
	}

	AnyCommandSource(FabricClientCommandSource clientSource) {
		this.serverSource = null;
		this.clientSource = clientSource;
	}

	@SuppressWarnings("ConstantConditions")
	public void sendFeedback(Text message, boolean broadcastToOps) {
		if (serverSource != null)
			serverSource.sendFeedback(message, broadcastToOps);
		else
			clientSource.sendFeedback(message);
	}

	@SuppressWarnings("ConstantConditions")
	public void sendError(Text message) {
		if (serverSource != null)
			serverSource.sendError(message);
		else
			clientSource.sendError(message);
	}

	public CommandSource getSpecificSource() {
		return serverSource != null ? serverSource : clientSource;
	}
}

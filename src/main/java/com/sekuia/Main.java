package com.sekuia;

import com.sekuia.command.HitAnimationCommand;
import com.sekuia.command.ScreenshakeCommand;
import com.sekuia.screenshake.ScreenshakeEffect;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.player.PlayerConnection;
import com.sekuia.event.PlayerPositionUpdateEvent;
import net.minestom.server.event.player.PlayerMoveEvent;

public class Main {
	public static void main(String[] args) {
		// server initialization
		MinecraftServer minecraftServer = MinecraftServer.init();

		// Instance creation
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

		instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 2, Block.GRASS_BLOCK));
		instanceContainer.setChunkSupplier(LightingChunk::new);

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
		globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
			final Player player = event.getPlayer();
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0, 2, 0));
		});

		// register the handling of the PlayerMoveEvent
		ScreenshakeEffect screenshakeEffect = new ScreenshakeEffect();
		PlayerPositionUpdateEvent playerPositionUpdateEvent = new PlayerPositionUpdateEvent(screenshakeEffect);
		playerPositionUpdateEvent.register(globalEventHandler);

		MinecraftServer.getCommandManager().register(new ScreenshakeCommand());
		MinecraftServer.getCommandManager().register(new HitAnimationCommand());

		minecraftServer.start("0.0.0.0", 25565);
	}
}
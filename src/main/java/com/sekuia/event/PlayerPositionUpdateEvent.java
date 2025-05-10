package com.sekuia.event;

import com.sekuia.screenshake.ScreenshakeState;
import net.minestom.server.event.Event;
import net.minestom.server.entity.Player;
import com.sekuia.screenshake.ScreenshakeEffect;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;

public class PlayerPositionUpdateEvent implements Event {
	private final ScreenshakeEffect screenshakeEffect;

	public PlayerPositionUpdateEvent(ScreenshakeEffect screenshakeEffect) {
		this.screenshakeEffect = screenshakeEffect;
	}

	public void register(GlobalEventHandler globalEventHandler) {
		globalEventHandler.addListener(PlayerMoveEvent.class, this::updateBasePosition);
	}

	public ScreenshakeEffect getScreenshakeEffect() {
		return screenshakeEffect;
	}

	private boolean hasActiveShake(Player player) {
		return screenshakeEffect.activeShakes.containsKey(player.getUuid());
	}

	public void updateBasePosition(PlayerMoveEvent event) {
		if (hasActiveShake(event.getPlayer())) {
			ScreenshakeState screenshakeState = screenshakeEffect.activeShakes.get(event.getPlayer().getUuid());
			if (screenshakeState != null) {
				screenshakeState.updateBasePosition();
			}
		}
	}
}

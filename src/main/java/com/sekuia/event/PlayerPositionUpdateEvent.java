package com.sekuia.event;

import net.minestom.server.event.Event;
import net.minestom.server.entity.Player;
import com.sekuia.screenshake.ScreenshakeEffect;

public class PlayerPositionUpdateEvent implements Event {
	private final ScreenshakeEffect screenshakeEffect;
	private final Player player;
	private boolean hasActiveShake;

	public PlayerPositionUpdateEvent(ScreenshakeEffect screenshakeEffect, Player player) {
		this.screenshakeEffect = screenshakeEffect;
		this.player = player;
		this.hasActiveShake = screenshakeEffect.activeShakes.containsKey(player.getUuid());
	}

	public ScreenshakeEffect getScreenshakeEffect() {
		return screenshakeEffect;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean hasActiveShake() {
		return hasActiveShake;
	}

	public void updateHasActiveShake() {
		this.hasActiveShake = screenshakeEffect.activeShakes.containsKey(player.getUuid());
	}

	public void updateBasePosition() {
		if (hasActiveShake) {
			screenshakeEffect.activeShakes.get(player.getUuid()).updateBasePosition();
		}
	}
}

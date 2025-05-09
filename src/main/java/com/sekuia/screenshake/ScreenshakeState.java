package com.sekuia.screenshake;

import net.minestom.server.entity.Player;

public class ScreenshakeState {
	private final Player player;
	private float baseYaw;
	private float basePitch;
	private final long startTime;
	private final long duration;
	private final float intensity;

	public ScreenshakeState(Player player, long duration, float intensity) {
		this.player = player;
		this.baseYaw = player.getPosition().yaw();
		this.basePitch = player.getPosition().pitch();
		this.startTime = System.currentTimeMillis();
		this.duration = (long)(duration * 1000);
		this.intensity = intensity;
	}

	public void updateBasePosition() {
		this.basePitch = player.getPosition().pitch();
		this.baseYaw = player.getPosition().yaw();
	}

	public Player getPlayer() {
		return player;
	}

	public float getBaseYaw() {
		return baseYaw;
	}

	public float getBasePitch() {
		return basePitch;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

	public float getIntensity() {
		return intensity;
	}
}

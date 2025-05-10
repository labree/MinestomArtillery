package com.sekuia.screenshake;

import net.minestom.server.entity.Player;

import java.util.concurrent.ScheduledFuture;

public class ScreenshakeState {
	private final Player player;
	private float baseYaw;
	private float basePitch;
	private float offsetYaw = 0;
	private float offsetPitch = 0;
	private final long startTime;
	private final long duration;
	private final float intensity;
	private float noisedYaw;
	private float noisedPitch;
	private ScheduledFuture<?> task;

	public ScreenshakeState(Player player, long duration, float intensity) {
		this.player = player;
		this.baseYaw = player.getPosition().yaw();
		this.basePitch = player.getPosition().pitch();
		this.noisedYaw = player.getPosition().yaw();
		this.noisedPitch = player.getPosition().pitch();
		this.startTime = System.currentTimeMillis();
		this.duration = (long)(duration * 1000);
		this.intensity = intensity;
	}

	public void updateBasePosition() {
		this.basePitch = player.getPosition().pitch() - offsetPitch;
		this.baseYaw = player.getPosition().yaw() - offsetYaw;
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

	public float getOffsetYaw() {
		return offsetYaw;
	}

	public float getOffsetPitch() {
		return offsetPitch;
	}

	public float getNoisedYaw() {
		return noisedYaw;
	}

	public float getNoisedPitch() {
		return noisedPitch;
	}

	public ScheduledFuture<?> getTask() {
		return task;
	}

	public void setTask(ScheduledFuture<?> task) {
		this.task = task;
	}

	public void setNoisedYaw(float noisedYaw) {
		this.noisedYaw = noisedYaw;
	}

	public void setNoisedPitch(float noisedPitch) {
		this.noisedPitch = noisedPitch;
	}

	public void setOffsetYaw(float offsetYaw) {
		this.offsetYaw = offsetYaw;
	}

	public void setOffsetPitch(float offsetPitch) {
		this.offsetPitch = offsetPitch;
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

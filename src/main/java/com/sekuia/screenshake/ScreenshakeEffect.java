package com.sekuia.screenshake;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PlayerRotationPacket;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScreenshakeEffect {
	public final Map<UUID, ScreenshakeState> activeShakes = new ConcurrentHashMap<>();
	private final SchedulerManager schedulerManager;
	private static final float MIN_STEPS = 1f;
	private static final float MAX_STEPS = 50f;
	private final Random random = new Random();
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);


	public ScreenshakeEffect() {
		this.schedulerManager = MinecraftServer.getSchedulerManager();
	}

	public void shakeScreen(float duration, float intensity, Player player) {
		ScreenshakeState screenshakeState = new ScreenshakeState(player, (long) (duration), intensity);
		activeShakes.put(player.getUuid(),screenshakeState);
		// Clamp intensity between 0.0 and 1.0 for step calculation
		final float clampedIntensity = Math.max(0.0f, Math.min(1.0f, intensity));
		
		// Calculate steps and interval
		int steps = calculateSteps(clampedIntensity);
		long intervalMs = calculateInterval(duration, steps);
		
		Pos originalPos = player.getPosition();
		
		// Create multiple layers of Perlin noise for more natural movement
		for (int i = 0; i < steps; i++) {
			final int step = i;
			schedulerManager.buildTask(() -> {
				// Use different time values for x and y to create varied movement
				float timeX = step * 0.1f;
				float timeY = step * 0.15f; // Different frequency for y-axis
				
				// Generate different noise values for yaw and pitch with different seeds
				// Apply the movement multiplier to make the shake more noticeable
				float yawNoise = improvedNoise(timeX, 0) * intensity;
				float pitchNoise = improvedNoise(timeY, 100) * intensity;
				
				// Add some random variation to the direction
				float randomFactor = 0.5f + random.nextFloat() * 0.5f; // Random factor between 0.5 and 1.0
				yawNoise *= randomFactor;
				pitchNoise *= (2.0f - randomFactor); // Inverse relationship to create more varied movement
				
				// Apply the noise to the original position
				float newYaw = originalPos.yaw() + yawNoise;
				float newPitch = originalPos.pitch() + pitchNoise;
				
				player.sendPacket(new PlayerRotationPacket(newYaw, newPitch));
			})
			.delay(TaskSchedule.millis(step * intervalMs))
			.schedule();
		}

		// Return to original position after all shakes are done
		schedulerManager.buildTask(() -> {
			player.sendPacket(new PlayerRotationPacket(originalPos.yaw(), originalPos.pitch()));
		})
		.delay(TaskSchedule.millis(steps * intervalMs))
		.schedule();
	}

	public void executeScreenShake(Player player, float duration, float intensity) {
		// Create and store shake for this current shake
		ScreenshakeState screenshakeState = new ScreenshakeState(player, (long) duration, intensity);
		activeShakes.put(player.getUuid(), screenshakeState);

		// Clamp intensity between 0.0 and 1.0 for step calculation
		final float clampedIntensity = Math.max(0.0f, Math.min(1.0f, intensity));

		// Calculate steps and interval
		int steps = calculateSteps(clampedIntensity);
		long intervalMs = calculateInterval(duration, steps);

		// Store the start time
		final long startTime = System.currentTimeMillis();

	    // Create the shake task
		Runnable shakeTask = () -> {
			// Check if we should stop the shake
			if (System.currentTimeMillis() - startTime >= duration * 1000) {
				// Return to original position
				player.sendPacket(new PlayerRotationPacket(
					screenshakeState.getBaseYaw(),
					screenshakeState.getBasePitch()
				));
				// Remove from active shakes
				activeShakes.remove(player.getUuid());
				// Cancel the task
				return;
			}
	
			// Get current base position from the state
			float baseYaw = screenshakeState.getBaseYaw();
			float basePitch = screenshakeState.getBasePitch();
			
			// Calculate time-based noise
			float timeX = (System.currentTimeMillis() - startTime) * 0.001f;
			float timeY = timeX * 1.5f;
			
			// Generate noise values
			float yawNoise = improvedNoise(timeX, 0) * intensity;
			float pitchNoise = improvedNoise(timeY, 100) * intensity;
			
			// Add random variation
			float randomFactor = 0.5f + random.nextFloat() * 0.5f;
			yawNoise *= randomFactor;
			pitchNoise *= (2.0f - randomFactor);
			
			// Apply to current base position
			float newYaw = baseYaw + yawNoise;
			float newPitch = basePitch + pitchNoise;
			
			// Send the packet
			player.sendPacket(new PlayerRotationPacket(newYaw, newPitch));	
		};

		// Schedule the shake task
		executor.scheduleAtFixedRate(shakeTask, 0, intervalMs, TimeUnit.MILLISECONDS);	
	}

	private int calculateSteps(float intensity) {
		// Linear interpolation between MIN_STEPS and MAX_STEPS based on intensity
		float steps = MIN_STEPS + (MAX_STEPS - MIN_STEPS) * intensity;
		// Ensure at least 1 step and round to nearest integer
		return Math.max(1, Math.round(steps));
	}

	private long calculateInterval(float duration, int steps) {
		// Convert duration from seconds to milliseconds and divide by steps
		return Math.round((duration * 1000) / steps);
	}

	// Improved noise function based on Ken Perlin's implementation
	private float improvedNoise(float x, float y) {
		// Convert to grid coordinates
		int X = (int) Math.floor(x) & 255;
		int Y = (int) Math.floor(y) & 255;
		
		// Get the fractional part
		x -= Math.floor(x);
		y -= Math.floor(y);
		
		// Compute fade curves
		float u = fade(x);
		float v = fade(y);
		
		// Hash coordinates
		int A = p[X] + Y;
		int AA = p[A];
		int AB = p[A + 1];
		int B = p[X + 1] + Y;
		int BA = p[B];
		int BB = p[B + 1];
		
		// Add blended results from 8 corners
		return lerp(v, lerp(u, grad(p[AA], x, y, 0),
				grad(p[BA], x - 1, y, 0)),
				lerp(u, grad(p[AB], x, y - 1, 0),
						grad(p[BB], x - 1, y - 1, 0)));
	}

	private float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private float lerp(float t, float a, float b) {
		return a + t * (b - a);
	}

	private float grad(int hash, float x, float y, float z) {
		int h = hash & 15;
		float u = h < 8 ? x : y;
		float v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	// Permutation table
	private static final int[] p = new int[512];
	static {
		int[] permutation = { 151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
				190,6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168,
				68,175,74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
				102,143,54,65,25,63,161,1,216,80,73,209,76,132,187,208,89,18,169,200,196,135,130,116,188,159,86,164,100,109,198,173,186,
				3,64,52,217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,223,183,170,213,
				119,248,152,2,44,154,163,70,221,153,101,155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,218,246,97,228,
				251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,235,249,14,239,107,49,192,214,31,181,199,106,157,184,84,204,176,
				115,121,50,45,127,4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180 };
		for (int i = 0; i < 256; i++) {
			p[256 + i] = p[i] = permutation[i];
		}
	}
}


package com.sekuia.command;

import com.sekuia.screenshake.ScreenshakeEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class ScreenshakeCommand extends Command {
	private final ScreenshakeEffect screenshakeEffect;

	public ScreenshakeCommand() {
		super("screenshake");
		screenshakeEffect = new ScreenshakeEffect();
		
		var intensityArg = ArgumentType.Float("intensity");
		var durationArg = ArgumentType.Float("duration");

		addSyntax((sender, context) -> {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
				return;
			}
			
			float intensity = context.get(intensityArg);
			float duration = context.get(durationArg);
			
			// Clamp values to valid range
			duration = Math.max(0.1f, Math.min(5.0f, duration));
			
			screenshakeEffect.executeScreenShake(player, duration, intensity);
			
			// Send feedback message
			sender.sendMessage(Component.text(
				String.format("Applied screenshake with intensity %.2f for %.2f seconds", intensity, duration),
				NamedTextColor.GREEN
			));
		}, intensityArg, durationArg);
	}
}

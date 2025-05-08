package com.sekuia.command;

import com.sekuia.screenshake.ScreenshakeEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.Objects;

public class ScreenshakeCommand extends Command {
	private final ScreenshakeEffect screenshakeEffect;

	public ScreenshakeCommand() {
		super("screenshake");
		screenshakeEffect = new ScreenshakeEffect();
		var intensityArg = ArgumentType.Float("intensity");

		addSyntax((sender, args) -> {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
				return;
			}
			float intensity = args.get(intensityArg);
			screenshakeEffect.shakeScreen(intensity, Objects.requireNonNull(((Player) sender).getPlayerConnection().getPlayer()));
		}, intensityArg);
	}
}

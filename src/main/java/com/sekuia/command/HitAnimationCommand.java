package com.sekuia.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.HitAnimationPacket;

public class HitAnimationCommand extends Command {

	public HitAnimationCommand() {
		super("hitanimation");

		var yaw = ArgumentType.Float("yaw");

		addSyntax(((sender, context) -> {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
				return;
			}
			float hitYaw = context.get(yaw);
			player.sendPacket(new HitAnimationPacket(player.getEntityId(), hitYaw));
		}), yaw);
	}
}

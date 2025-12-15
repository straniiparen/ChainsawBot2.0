package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Random;

public class CoinFlipCommand implements Command {
    private final Random random = new Random();

    @Override
    public String getName() {
        return "coinflip";
    }

    @Override
    public String getDescription() {
        return "Подбрасывает монетку и говорит результат";
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.build.SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        double chance = random.nextDouble();

        if (chance <= 0.001) {
            event.reply("🎰 Монетка встала на ребро!").queue();
        } else if (chance <= 0.5005) {
            event.reply("🪙 Решка").queue();
        } else {
            event.reply("🦅 Орёл").queue();
        }
    }
}
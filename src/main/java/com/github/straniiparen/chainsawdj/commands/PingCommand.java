package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PingCommand implements Command {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Пингует в ответ";
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.build.SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long startTime = System.currentTimeMillis();

        event.reply("Понг! 🏓").setEphemeral(true).queue(response -> {
            long ping = System.currentTimeMillis() - startTime;
            response.editOriginal(String.format(
                    "%s Понг! 🏓\nПинг бота: %dms\nОбщая задержка: %dms",
                    event.getUser().getAsMention(),
                    event.getJDA().getGatewayPing(),
                    ping
            )).queue();
        });
    }
}
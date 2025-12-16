package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ClearCommand implements Command {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Удаляет указанное количество сообщений";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        // Проверка прав
        if (member == null || !member.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("❌ У вас нет прав на управление сообщениями!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int amount = event.getOption("количество").getAsInt();

        // Проверка корректности количества
        if (amount < 2 || amount > 100) {
            event.reply("❌ Количество сообщений должно быть от 2 до 100!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Удаление сообщений
        TextChannel channel = event.getChannel().asTextChannel();
        channel.getHistory().retrievePast(amount).queue(messages -> {
            channel.purgeMessages(messages);

            event.reply("✅ Удалено **" + messages.size() + "** сообщений!")
                    .setEphemeral(true)
                    .queue();
        }, error -> {
            event.reply("❌ Не удалось удалить сообщения: " + error.getMessage())
                    .setEphemeral(true)
                    .queue();
        });
    }
}
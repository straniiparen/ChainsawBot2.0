package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SlowmodeCommand implements Command {
    @Override
    public String getName() {
        return "slowmode";
    }

    @Override
    public String getDescription() {
        return "Устанавливает медленный режим в канале";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        int seconds = event.getOption("секунды").getAsInt();

        // Проверка прав
        if (member == null || !member.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("❌ У вас нет прав на управление каналом!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Проверка корректности значения
        if (seconds < 0 || seconds > 21600) {
            event.reply("❌ Время должно быть от 0 до 21600 секунд (6 часов)!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Установка медленного режима
        TextChannel channel = event.getChannel().asTextChannel();

        channel.getManager().setSlowmode(seconds).queue(
                success -> {
                    String message;
                    if (seconds == 0) {
                        message = "✅ Медленный режим **отключен** в канале " + channel.getAsMention();
                    } else {
                        String time;
                        if (seconds < 60) {
                            time = seconds + " секунд";
                        } else if (seconds < 3600) {
                            time = (seconds / 60) + " минут";
                        } else {
                            time = (seconds / 3600) + " часов";
                        }
                        message = "✅ Медленный режим установлен на **" + time + "** в канале " + channel.getAsMention();
                    }
                    event.reply(message).queue();
                },
                error -> event.reply("❌ Не удалось установить медленный режим: " + error.getMessage())
                        .setEphemeral(true)
                        .queue()
        );
    }
}
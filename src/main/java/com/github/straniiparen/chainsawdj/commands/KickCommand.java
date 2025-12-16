package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class KickCommand implements Command {
    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Исключает участника с сервера";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        User targetUser = event.getOption("пользователь").getAsUser();
        String reason = event.getOption("причина") != null
                ? event.getOption("причина").getAsString()
                : "Причина не указана";

        // Проверка прав
        if (member == null || !member.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("❌ У вас нет прав на исключение участников!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Нельзя исключить себя
        if (targetUser.getId().equals(member.getId())) {
            event.reply("❌ Вы не можете исключить самого себя!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Нельзя исключить владельца сервера
        if (targetUser.getId().equals(event.getGuild().getOwnerId())) {
            event.reply("❌ Вы не можете исключить владельца сервера!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Получаем участника и исключаем
        event.getGuild().retrieveMember(targetUser).queue(targetMember -> {
            if (!event.getGuild().getSelfMember().canInteract(targetMember)) {
                event.reply("❌ Я не могу исключить этого пользователя (его роль выше моей)!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (!member.canInteract(targetMember)) {
                event.reply("❌ Вы не можете исключить этого пользователя (его роль выше вашей)!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            // Выполняем исключение
            event.getGuild().kick(targetMember)
                    .reason("Исключил: " + member.getUser().getAsTag() + ". Причина: " + reason)
                    .queue(
                            success -> event.reply("✅ Пользователь **" + targetUser.getAsTag() + "** успешно исключен!")
                                    .queue(),
                            error -> event.reply("❌ Не удалось исключить пользователя: " + error.getMessage())
                                    .setEphemeral(true)
                                    .queue()
                    );

        }, error -> {
            event.reply("❌ Пользователь не найден на сервере!")
                    .setEphemeral(true)
                    .queue();
        });
    }
}
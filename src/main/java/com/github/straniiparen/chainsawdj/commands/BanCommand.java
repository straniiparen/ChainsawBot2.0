package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.concurrent.TimeUnit;

public class BanCommand implements Command {
    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Блокирует участника на сервере";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.USER, "пользователь", "Пользователь для блокировки", true)
                .addOption(OptionType.STRING, "причина", "Причина блокировки", false)
                .addOption(OptionType.INTEGER, "дни", "Удалить сообщения за последние дни (0-7)", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        User targetUser = event.getOption("пользователь").getAsUser();
        String reason = event.getOption("причина") != null
                ? event.getOption("причина").getAsString()
                : "Причина не указана";

        int deleteDays;
        if (event.getOption("дни") != null) {
            deleteDays = Math.min(Math.max(event.getOption("дни").getAsInt(), 0), 7);
        } else {
            deleteDays = 0;
        }

        if (member == null || !member.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ У вас нет прав на блокировку участников!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Нельзя заблокировать себя
        if (targetUser.getId().equals(member.getId())) {
            event.reply("❌ Вы не можете заблокировать самого себя!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Нельзя заблокировать владельца сервера
        if (targetUser.getId().equals(event.getGuild().getOwnerId())) {
            event.reply("❌ Вы не можете заблокировать владельца сервера!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String finalReason = "Заблокировал: " + member.getUser().getAsTag() + ". Причина: " + reason;


        event.getGuild().retrieveMember(targetUser).queue(targetMember -> {
            if (!event.getGuild().getSelfMember().canInteract(targetMember)) {
                event.reply("❌ Я не могу заблокировать этого пользователя (его роль выше моей)!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (!member.canInteract(targetMember)) {
                event.reply("❌ Вы не можете заблокировать этого пользователя (его роль выше вашей)!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            // Блокируем с удалением сообщений
            if (deleteDays > 0) {
                event.getGuild().ban(targetMember, deleteDays, TimeUnit.DAYS)
                        .reason(finalReason)
                        .queue(
                                success -> {
                                    String message = "✅ Пользователь **" + targetUser.getAsTag() + "** успешно заблокирован!";
                                    message += "\n🗑️ Удалены сообщения за последние **" + deleteDays + "** дней";
                                    event.reply(message).queue();
                                },
                                error -> event.reply("❌ Не удалось заблокировать пользователя: " + error.getMessage())
                                        .setEphemeral(true)
                                        .queue()
                        );
            } else {
                // Блокируем без удаления сообщений
                event.getGuild().ban(targetMember, 0, TimeUnit.SECONDS)
                        .reason(finalReason)
                        .queue(
                                success -> event.reply("✅ Пользователь **" + targetUser.getAsTag() + "** успешно заблокирован!").queue(),
                                error -> event.reply("❌ Не удалось заблокировать пользователя: " + error.getMessage())
                                        .setEphemeral(true)
                                        .queue()
                        );
            }

        }, error -> {
            // Если пользователя нет на сервере, блокируем по ID
            if (deleteDays > 0) {
                event.getGuild().ban(targetUser, deleteDays, TimeUnit.DAYS)
                        .reason(finalReason)
                        .queue(
                                success -> {
                                    String message = "✅ Пользователь **" + targetUser.getAsTag() + "** успешно заблокирован (по ID)!";
                                    message += "\n🗑️ Удалены сообщения за последние **" + deleteDays + "** дней";
                                    event.reply(message).queue();
                                },
                                error2 -> event.reply("❌ Не удалось заблокировать пользователя: " + error2.getMessage())
                                        .setEphemeral(true)
                                        .queue()
                        );
            } else {
                event.getGuild().ban(targetUser, 0, TimeUnit.SECONDS)
                        .reason(finalReason)
                        .queue(
                                success -> event.reply("✅ Пользователь **" + targetUser.getAsTag() + "** успешно заблокирован (по ID)!").queue(),
                                error2 -> event.reply("❌ Не удалось заблокировать пользователя: " + error2.getMessage())
                                        .setEphemeral(true)
                                        .queue()
                        );
            }
        });
    }
}
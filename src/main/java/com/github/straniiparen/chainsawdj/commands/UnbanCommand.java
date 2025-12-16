package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UnbanCommand implements Command {
    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Снимает блокировку с пользователя";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "пользователь", "ID или тег пользователя (например: 123456789012345678 или User#1234)", true)
                .addOption(OptionType.STRING, "причина", "Причина разблокировки", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        String userInput = event.getOption("пользователь").getAsString();
        String reason = event.getOption("причина") != null
                ? event.getOption("причина").getAsString()
                : "Причина не указана";

        // Проверка прав
        if (member == null || !member.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ У вас нет прав на разблокировку участников!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String finalReason = "Разблокировал: " + member.getUser().getAsTag() + ". Причина: " + reason;

        // Отправляем сообщение о начале поиска
        event.deferReply().queue();

        // Получаем список забаненных пользователей
        event.getGuild().retrieveBanList().queue(bans -> {
            User targetUser = null;

            // Поиск по ID
            if (userInput.matches("\\d{17,20}")) {
                targetUser = findUserById(bans, userInput);
            }

            // Поиск по тегу (если не нашли по ID)
            if (targetUser == null) {
                targetUser = findUserByTag(bans, userInput);
            }

            // Если пользователь не найден в банах
            if (targetUser == null) {
                event.getHook().editOriginal("❌ Пользователь не найден в списке заблокированных!")
                        .queue();
                return;
            }

            // Выполняем разблокировку
            User finalTargetUser = targetUser;
            event.getGuild().unban(targetUser)
                    .reason(finalReason)
                    .queue(
                            success -> event.getHook().editOriginal("✅ Пользователь **" + finalTargetUser.getAsTag() + "** успешно разблокирован!").queue(),
                            error -> event.getHook().editOriginal("❌ Не удалось разблокировать пользователя: " + error.getMessage()).queue()
                    );

        }, error -> {
            event.getHook().editOriginal("❌ Не удалось получить список заблокированных пользователей: " + error.getMessage()).queue();
        });
    }

    // Поиск пользователя по ID
    private User findUserById(List<net.dv8tion.jda.api.entities.Guild.Ban> bans, String userId) {
        for (net.dv8tion.jda.api.entities.Guild.Ban ban : bans) {
            if (ban.getUser().getId().equals(userId)) {
                return ban.getUser();
            }
        }
        return null;
    }

    // Поиск пользователя по тегу или имени
    private User findUserByTag(List<net.dv8tion.jda.api.entities.Guild.Ban> bans, String userInput) {
        // Проверяем разные форматы ввода
        for (net.dv8tion.jda.api.entities.Guild.Ban ban : bans) {
            User user = ban.getUser();

            // Полный тег (Username#Discriminator)
            if (user.getAsTag().equalsIgnoreCase(userInput)) {
                return user;
            }

            // Только имя пользователя
            if (user.getName().equalsIgnoreCase(userInput)) {
                return user;
            }

            // Частичное совпадение имени
            if (user.getName().toLowerCase().contains(userInput.toLowerCase())) {
                return user;
            }

            // Частичное совпадение тега
            if (user.getAsTag().toLowerCase().contains(userInput.toLowerCase())) {
                return user;
            }
        }
        return null;
    }
}
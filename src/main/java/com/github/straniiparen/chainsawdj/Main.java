package com.github.straniiparen.chainsawdj;

import com.github.straniiparen.chainsawdj.bot.ChainsawDJ;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path tokenFile = Paths.get(System.getProperty("user.dir") + "/token.txt");
        try {
            System.out.println("Place token.txt in " + System.getProperty("user.dir"));
            String token = Files.readString(tokenFile).trim();
            new ChainsawDJ(token);
        } catch (Exception e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.example.Tambola;

import java.util.Random;

public class IdCreator {
    public String idGenerator(){
        System.out.println("Hello, World!");
        final String upperAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String nums = "0123456789";
        StringBuilder Id = new StringBuilder();

        Random random = new Random();

        while (Id.length() <= 5) {
            if (Id.length() < 2) {
                int value = getRandomNumber(random, 0, 26);
                Id.append(upperAlpha.charAt(value));
            } else {
                int value = getRandomNumber(random, 0, 10);
                Id.append(nums.charAt(value));
            }
        }
        return Id.toString();
    }
    public static int getRandomNumber(Random random, int min, int max) {
        return random.nextInt(max - min) + min;
    }
}

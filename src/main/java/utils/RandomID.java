package utils;

import java.util.Random;

public class RandomID {
    
    public static String rand(){
       return RandomID.rand(4);
    }
    
    public static String rand(int length){
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int charactersLength = characters.length();
        String randomString = "";
        
        for(int i=0; i < length;i++){
            randomString += characters.charAt(RandomID.randInt(0, charactersLength-1));
        }
        return randomString;
    }
    
    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}

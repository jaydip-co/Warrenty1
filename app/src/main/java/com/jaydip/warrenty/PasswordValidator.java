package com.jaydip.warrenty;

public class PasswordValidator {
    public static Conditions validate(String first){
        if(first.length() < 6){
            return   Conditions.MINLENGTH;
        }
        if(first.length() > 10){
            return Conditions.MAXLENGTH;
        }
        if(!isContainCapital(first))
            return Conditions.ALPHABET;
        if(!isContainLowerCase(first))
            return Conditions.ALPHABET;
        return Conditions.SUCCESS;
    }
    private static boolean isContainCapital(String s){
        for(int i=0;i<s.length();i++){
            char c = s.charAt(i);
            if(Character.isUpperCase(c)){
                return true;
            }
        }
        return false;
    }
    private static boolean isContainLowerCase(String s){
        for(int i=0;i<s.length();i++){
            char c = s.charAt(i);
            if(Character.isLowerCase(c)){
                return true;
            }
        }
        return false;
    }
}

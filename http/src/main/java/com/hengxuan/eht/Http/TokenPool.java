package com.hengxuan.eht.Http;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/11/27.
 */
public class TokenPool {
    private static ArrayList<Token> tokens = new ArrayList<Token>();
    private static TokenPool instance;
    private TokenPool(){

    }

    public static synchronized TokenPool getTokenPool(){
        if(instance == null){
            instance = new TokenPool();
        }
        return instance;
    }

    public synchronized String getToken(){
        String ret = null;
        for(Token token:tokens){
            if(!token.isUse){
                token.isUse = true;
                return token.value;
            }
        }
        return ret;
    }

    public synchronized void backToken(String val){
        if(val==null || val.equals(""))return;
        for(Token token:tokens){
            if(token.value.equals(val)){
                token.isUse = false;
                return;
            }
        }
        Token token = new Token();
        token.value = val;
        token.isUse = false;
        tokens.add(token);
    }
    public synchronized void addToken(String val){
        if(val==null || val.equals(""))return;
        for(Token token:tokens){
            if(token.value.equals(val)){
                token.isUse = true;
            }
        }
        Token token = new Token();
        token.value = val;
        token.isUse = true;
        tokens.add(token);
    }


    class Token{
        String value;
        boolean isUse;
    }
}

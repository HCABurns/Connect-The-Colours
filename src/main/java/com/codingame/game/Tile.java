package com.codingame.game;

import com.codingame.gameengine.module.entities.Sprite;

public class Tile {

    private int id;
    private char startValue;
    private Sprite sprite;


    public Tile(int id, char startValue, Sprite sprite) {
        this.id = id;
        this.startValue = startValue;
        this.sprite = sprite;
    }


    public int getId() {
        return id;
    }
}

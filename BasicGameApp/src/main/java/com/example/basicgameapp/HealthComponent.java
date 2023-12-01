package com.example.basicgameapp;

import com.almasb.fxgl.entity.component.Component;

public class HealthComponent extends Component {
    public int health = 100;

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
}
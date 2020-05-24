package com.aasma2020.pacman.communication;

import com.aasma2020.pacman.Food;
import com.aasma2020.pacman.Ghost;
import com.aasma2020.pacman.PowerUpFood;
import com.aasma2020.pacman.board.Position;

import java.awt.*;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapAreaInfo {

    private Point center;

    private double radius;

    private Position pacman;

    private SortedMap<Point, Ghost> ghosts;

    private SortedMap<Point, PowerUpFood> powerUpFood;

    private SortedMap<Point, Food> food;

    private Point positionOfClosest;

    private Class closest;

    public MapAreaInfo(Point center, double radius) {
        this.center = center;
        this.radius = radius;
        ghosts = new TreeMap<>((Point o1, Point o2) -> {
            return (int) (distance(o1) - distance(o2));
        });
        powerUpFood = new TreeMap<>((Point o1, Point o2) -> {
            return (int) (distance(o1) - distance(o2));
        });
        food = new TreeMap<>((Point o1, Point o2) -> {
            return (int) (distance(o1) - distance(o2));
        });
    }

    public Position getPacman() {
        return pacman;
    }

    public void setPacman(Point pacman) {
        if(distance(pacman) > radius) {
            return;
        }
        this.pacman = new Position(pacman);
    }

    public SortedMap<Point, Ghost> getGhosts() {
        return ghosts;
    }

    public void addGhost(Point position, Ghost ghost) {
        if(distance(position) > radius) {
            return;
        }
        ghosts.put(position, ghost);
        validateClosest();
    }

    public Map<Point, PowerUpFood> getPowerUpFood() {
        return powerUpFood;
    }

    public void setPowerUpFood(SortedMap<Point, PowerUpFood> powerUpFood) {
        this.powerUpFood = powerUpFood;
        validateClosest();
    }

    public void addPowerUpFood(Point position, PowerUpFood powerUp) {
        if(distance(position) > radius) {
            return;
        }
        powerUpFood.put(position, powerUp);
        validateClosest();
    }

    public Map<Point, Food> getFood() {
        return food;
    }

    public void setFood(SortedMap<Point, Food> food) {
        this.food = food;
        validateClosest();
    }

    public void addFood(Point position, Food fd) {
        if(distance(position) > radius) {
            return;
        }
        food.put(position, fd);
        validateClosest();
    }

    public Point getPointOfClosest() {
        return positionOfClosest;
    }

    public void setPointOfClosest(Point positionOfClosest) {
        this.positionOfClosest = positionOfClosest;
    }

    public Class getClosest() {
        return closest;
    }

    public void setClosest(Class closest) {
        this.closest = closest;
    }

    private void validateClosest() {

        Point closestGhost = null;
        Point closestPowerUpFood = null;
        Point closestFood = null;

        if(!ghosts.isEmpty()) {
            closestGhost = ghosts.firstKey();
        }
        if(!powerUpFood.isEmpty()) {
            closestPowerUpFood = powerUpFood.firstKey();
        }
        if(!food.isEmpty()) {
            closestFood = food.firstKey();
        }

        if(distance(closestGhost) <= distance(closestPowerUpFood) &&
                distance(closestGhost) <= distance(closestFood)) {
            positionOfClosest = closestGhost;
            closest = Ghost.class;
            return;
        }
        if(distance(closestPowerUpFood) <= distance(closestGhost) &&
                distance(closestPowerUpFood) <= distance(closestFood)) {
            positionOfClosest = closestPowerUpFood;
            closest = PowerUpFood.class;
            return;
        }
        if(distance(closestFood) <= distance(closestPowerUpFood) &&
                distance(closestFood) <= distance(closestGhost)) {
            positionOfClosest = closestFood;
            closest = Food.class;
            return;
        }

    }

    private double distance(Point point) {
        return center.distance(point != null ? point : new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

}

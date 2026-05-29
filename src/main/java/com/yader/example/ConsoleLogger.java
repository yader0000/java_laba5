package com.yader.example;

/**
 * Реализация {@link EventLogger}, записывающая события в консоль.
 */
public class ConsoleLogger implements EventLogger {

    @Override
    public void log() {
        System.out.print("C");
    }
}
package com.yader.example;

/**
 * Журнал событий.
 * Конкретное место записи определяется классом-реализацией.
 */
public interface EventLogger {

    /**
     * Записывает событие в журнал.
     */
    void log();
}
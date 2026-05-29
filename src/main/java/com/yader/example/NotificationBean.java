package com.yader.example;

import com.yader.AutoInjectable;

/**
 * Демонстрационный класс с зависимостями, которые должны быть
 * внедрены автоматически.
 * <p>
 * Поля {@code messageService} и {@code eventLogger} нигде в классе
 * не инициализируются вручную — их заполняет {@link com.yader.Injector}.
 */
public class NotificationBean {

    @AutoInjectable
    private MessageService messageService;

    @AutoInjectable
    private EventLogger eventLogger;

    /**
     * Использует внедрённые зависимости.
     * Если зависимости не были внедрены, вызов завершится
     * {@link NullPointerException}.
     */
    public void process() {
        messageService.send();
        eventLogger.log();
    }
}
package com.yader;

import com.yader.example.NotificationBean;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тесты для {@link Injector}.
 * <p>
 * Каждый тест собирает собственный набор привязок и передаёт его
 * в инжектор напрямую через конструктор {@code Injector(Properties)},
 * поэтому тесты не зависят друг от друга и не трогают
 * основной {@code config.properties}.
 */
class InjectorTest {

    /**
     * Собирает набор привязок для тестов.
     *
     * @param messageImpl полное имя реализации {@code MessageService}
     *                    (или {@code null}, чтобы не добавлять привязку)
     * @param loggerImpl  полное имя реализации {@code EventLogger}
     *                    (или {@code null}, чтобы не добавлять привязку)
     * @return заполненный объект {@link Properties}
     */
    private Properties bindings(String messageImpl, String loggerImpl) {
        Properties props = new Properties();
        if (messageImpl != null) {
            props.setProperty("com.yader.example.MessageService", messageImpl);
        }
        if (loggerImpl != null) {
            props.setProperty("com.yader.example.EventLogger", loggerImpl);
        }
        return props;
    }

    @Test
    void injectsConfiguredImplementations() {
        Injector injector = new Injector(bindings(
                "com.yader.example.EmailService",
                "com.yader.example.ConsoleLogger"));

        NotificationBean bean = injector.inject(new NotificationBean());

        // если бы поля не внедрились, process() бросил бы NullPointerException
        assertDoesNotThrow(bean::process);
    }

    @Test
    void injectorReturnsSameObject() {
        Injector injector = new Injector(bindings(
                "com.yader.example.EmailService",
                "com.yader.example.ConsoleLogger"));

        NotificationBean original = new NotificationBean();
        NotificationBean result = injector.inject(original);

        assertSame(original, result);
    }

    @Test
    void differentConfigInjectsDifferentImplementation() {
        assertDoesNotThrow(() -> new Injector(bindings(
                "com.yader.example.EmailService",
                "com.yader.example.ConsoleLogger"))
                .inject(new NotificationBean())
                .process());

        assertDoesNotThrow(() -> new Injector(bindings(
                "com.yader.example.SmsService",
                "com.yader.example.ConsoleLogger"))
                .inject(new NotificationBean())
                .process());
    }

    @Test
    void missingBindingThrowsException() {
        // нет привязки для MessageService
        Injector injector = new Injector(
                bindings(null, "com.yader.example.ConsoleLogger"));
        NotificationBean bean = new NotificationBean();

        assertThrows(IllegalStateException.class, () -> injector.inject(bean));
    }

    @Test
    void missingConfigResourceThrowsException() {
        assertThrows(IllegalStateException.class,
                () -> new Injector("nonexistent.properties"));
    }

    @Test
    void unknownImplementationClassThrowsException() {
        Injector injector = new Injector(bindings(
                "com.yader.example.DoesNotExist",
                "com.yader.example.ConsoleLogger"));
        NotificationBean bean = new NotificationBean();

        assertThrows(IllegalStateException.class, () -> injector.inject(bean));
    }
}
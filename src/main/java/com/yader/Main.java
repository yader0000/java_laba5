package com.yader;

import com.yader.example.NotificationBean;

/**
 * Точка входа. Демонстрирует работу {@link Injector}:
 * создаёт объект с незаполненными зависимостями, прогоняет его
 * через инжектор и вызывает метод, использующий внедрённые зависимости.
 * <p>
 * При настройках по умолчанию выводит {@code AC}. Если в
 * {@code config.properties} заменить {@code EmailService} на
 * {@code SmsService}, вывод изменится на {@code BC}.
 */
public class Main {

    public static void main(String[] args) {
        NotificationBean bean = new Injector().inject(new NotificationBean());
        bean.process();
        System.out.println();
    }
}
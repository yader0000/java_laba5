package com.yader.example;

/**
 * Реализация {@link MessageService}, имитирующая отправку по электронной почте.
 */
public class EmailService implements MessageService {

    @Override
    public void send() {
        System.out.print("A");
    }
}
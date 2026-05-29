package com.yader.example;

/**
 * Реализация {@link MessageService}, имитирующая отправку по SMS.
 */
public class SmsService implements MessageService {

    @Override
    public void send() {
        System.out.print("B");
    }
}
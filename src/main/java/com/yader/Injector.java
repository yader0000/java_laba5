package com.yader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

/**
 * Выполняет внедрение зависимостей в объекты.
 * <p>
 * Просматривает все поля переданного объекта и для каждого поля,
 * помеченного аннотацией {@link AutoInjectable}, подбирает класс-реализацию.
 * Соответствие "интерфейс — класс-реализация" берётся из файла настроек
 * формата {@code .properties}: ключ — полное имя интерфейса,
 * значение — полное имя класса, который его реализует.
 */
public class Injector {

    /** Имя файла настроек в ресурсах, используемого по умолчанию. */
    private static final String DEFAULT_CONFIG = "config.properties";

    /** Карта соответствий "имя интерфейса — имя класса-реализации". */
    private final Properties bindings;

    /**
     * Создаёт инжектор, читая настройки из ресурса {@value #DEFAULT_CONFIG}.
     */
    public Injector() {
        this(DEFAULT_CONFIG);
    }

    /**
     * Создаёт инжектор, читая настройки из указанного ресурса на classpath.
     *
     * @param resourceName имя {@code .properties}-файла в ресурсах проекта
     * @throws IllegalStateException если ресурс не найден
     * @throws UncheckedIOException  если ресурс не удалось прочитать
     */
    public Injector(String resourceName) {
        this.bindings = loadFromResource(resourceName);
    }

    /**
     * Создаёт инжектор с готовым набором привязок.
     * Этот конструктор удобен для тестов: можно передать привязки напрямую,
     * не создавая файл на диске.
     *
     * @param bindings набор соответствий "интерфейс — реализация"
     */
    public Injector(Properties bindings) {
        this.bindings = Objects.requireNonNull(bindings, "bindings");
    }

    /**
     * Внедряет зависимости в переданный объект.
     * <p>
     * Для каждого поля, помеченного {@link AutoInjectable}, определяется тип поля,
     * по имени типа в настройках ищется класс-реализация, создаётся его экземпляр
     * и записывается в поле.
     *
     * @param target объект, в поля которого нужно внедрить зависимости
     * @param <T>    тип объекта
     * @return тот же объект с заполненными полями
     * @throws IllegalStateException если для интерфейса нет привязки в настройках
     *                               или экземпляр реализации не удалось создать
     */
    public <T> T inject(T target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoInjectable.class)) {
                continue;
            }
            String interfaceName = field.getType().getName();
            String implementationName = bindings.getProperty(interfaceName);

            if (implementationName == null || implementationName.isBlank()) {
                throw new IllegalStateException(
                        "No binding configured for interface: " + interfaceName);
            }
            field.setAccessible(true);
            try {
                field.set(target, instantiate(implementationName.trim()));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Cannot access field: " + field.getName(), e);
            }
        }
        return target;
    }

    /**
     * Создаёт экземпляр класса по его полному имени,
     * используя конструктор без аргументов.
     *
     * @param className полное имя класса-реализации
     * @return новый экземпляр указанного класса
     * @throws IllegalStateException если класс не найден или его не удалось создать
     */
    private Object instantiate(String className) {
        try {
            return Class.forName(className)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate implementation: " + className, e);
        }
    }

    /**
     * Загружает настройки из ресурса на classpath.
     *
     * @param resourceName имя файла настроек в ресурсах
     * @return заполненный объект {@link Properties}
     * @throws IllegalStateException если ресурс не найден
     * @throws UncheckedIOException  если ресурс не удалось прочитать
     */
    private static Properties loadFromResource(String resourceName) {
        Properties props = new Properties();
        try (InputStream in = Injector.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Config resource not found on classpath: " + resourceName);
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Cannot read config resource: " + resourceName, e);
        }
        return props;
    }
}
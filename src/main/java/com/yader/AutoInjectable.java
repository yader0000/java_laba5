package com.yader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает поле, в которое {@link Injector} должен автоматически
 * внедрить реализацию при вызове {@code inject()}.
 * <p>
 * {@link RetentionPolicy#RUNTIME} обязателен — иначе аннотация не сохранится
 * в скомпилированном классе и рефлексия не сможет её увидеть.
 * {@link ElementType#FIELD} ограничивает применение: аннотацию можно
 * ставить только на поля.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoInjectable {
}
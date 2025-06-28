package dev.benchurchill.desky.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();               // base name (e.g. "office")

    String[] aliases() default {};

    String permission() default "";

    String parent() default ""; // for subcommands
}

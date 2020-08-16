package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.MinecraftVersion;
import lombok.NonNull;
import org.bukkit.command.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
@NonNull

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InferiorCommandHandler {

	Class<?> argType();

}

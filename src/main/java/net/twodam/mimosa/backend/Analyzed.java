package net.twodam.mimosa.backend;

import net.twodam.mimosa.types.MimosaType;

import java.util.function.Function;

public interface Analyzed extends Function<Environment, MimosaType> {
}

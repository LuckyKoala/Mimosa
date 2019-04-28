package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.types.MimosaType;

import java.util.function.Function;

public interface Analyzed extends Function<Environment, MimosaType> {
}

package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.exceptions.MimosaEvaluatorException;
import net.twodam.mimosa.exceptions.MimosaNoBindingException;
import net.twodam.mimosa.types.MimosaList;
import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.twodam.mimosa.utils.MimosaListUtil.*;

/**
 * Created by luckykoala on 19-4-8.
 */
public class Environment {
    static class Entry {
        MimosaSymbol key;
        MimosaType value;

        Entry(MimosaSymbol key, MimosaType value) {
            this.key = key;
            this.value = value;
        }

        static Entry wrapKey(MimosaSymbol key) {
            return new Entry(key, null);
        }

        @Override
        public boolean equals(Object obj) {
            if(super.equals(obj)) return true;
            if(!(obj instanceof Entry)) return false;

            return Objects.equals(this.key, ((Entry) obj).key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", key, value);
        }
    }

    List<Entry> entryList;
    private Environment upperEnv;

    Environment(List<Entry> entryList) {
        this.entryList = entryList;
        this.upperEnv = null;
    }

    Environment(Environment env) {
        this.entryList = new ArrayList<>();
        this.upperEnv = env;
    }

    public static Environment empty() {
        return new Environment(new ArrayList<>());
    }

    /**
     * This function won't modify env in the parameters
     * @param env Environment to be extended.
     * @param key key of new binding.
     * @param value value of new binding.
     * @return extended enviroment.
     */
    public static Environment extend(Environment env, MimosaSymbol key, MimosaType value) {
        Environment extendedEnv = new Environment(env);
        extendedEnv.entryList.add(new Entry(key, value));
        return extendedEnv;
    }

    public static Environment extend(Environment env, MimosaType keys, MimosaType values) {
        TypeUtil.checkType(MimosaList.class, keys);
        TypeUtil.checkType(MimosaList.class, values);

        MimosaNumber keyLength = length(keys);
        MimosaNumber valueLength = length(values);
        if(keyLength.equals(valueLength)) {
            Environment extendedEnv = new Environment(env);
            while(!(MimosaList.isNil(keys) || MimosaList.isNil(values))) {
                extendedEnv.entryList.add(new Entry((MimosaSymbol) car(keys), car(values)));
                keys = cdr(keys);
                values = cdr(values);
            }
            return extendedEnv;
        } else {
            throw MimosaEvaluatorException.paramsCountNotMatched(keyLength.toString(), MimosaNumber.valToNum(valueLength));
        }
    }

    public static MimosaType search(Environment env, MimosaSymbol key) {
        while(env != null) {
            final int index = env.entryList.lastIndexOf(Entry.wrapKey(key));
            if(index != -1) {
                //Found binding of key
                return env.entryList.get(index).value;
            } else {
                env = env.upperEnv;
            }
        }

        throw MimosaNoBindingException.noBindingOf(key);
    }

    @Override
    public String toString() {
        if(upperEnv == null) {
            return entryList.stream().map(Entry::toString).collect(Collectors.joining("\n"));
        } else {
            return upperEnv.toString() + "\n\n" +
                    entryList.stream().map(Entry::toString).collect(Collectors.joining("\n"));
        }
    }
}

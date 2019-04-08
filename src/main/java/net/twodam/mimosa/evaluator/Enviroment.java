package net.twodam.mimosa.evaluator;

import net.twodam.mimosa.exceptions.MimosaNoBindingException;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaSymbol;
import net.twodam.mimosa.types.MimosaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by luckykoala on 19-4-8.
 */
public class Enviroment {
    static class Entry {
        MimosaSymbol key;
        MimosaPair value;

        Entry(MimosaSymbol key, MimosaPair value) {
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
    }

    private List<Entry> entryList;

    Enviroment(List<Entry> entryList) {
        this.entryList = entryList;
    }

    Enviroment(Enviroment env) {
        this.entryList = new ArrayList<>(env.entryList);
    }

    public static Enviroment empty() {
        return new Enviroment(new ArrayList<>());
    }

    /**
     * This function won't modify env in the parameters
     * @param env Enviroment to be extended.
     * @param key key of new binding.
     * @param value value of new binding.
     * @return extended enviroment.
     */
    public static Enviroment extend(Enviroment env, MimosaSymbol key, MimosaPair value) {
        Enviroment extendedEnv = new Enviroment(env);
        extendedEnv.entryList.add(new Entry(key, value));
        return extendedEnv;
    }

    public static MimosaPair search(Enviroment env, MimosaSymbol key) {
        final int index = env.entryList.indexOf(Entry.wrapKey(key));
        if(index != -1) {
            //Found binding of key
            return env.entryList.get(index).value;
        } else {
            throw MimosaNoBindingException.noBindingOf(key);
        }
    }
}

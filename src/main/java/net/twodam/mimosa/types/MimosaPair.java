package net.twodam.mimosa.types;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaPair extends MimosaType {
    private MimosaType car;
    private MimosaType cdr;

    MimosaPair(MimosaType car, MimosaType cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static MimosaPair cons(MimosaType data, MimosaType origin) {
        return new MimosaPair(data, origin);
    }

    public MimosaType car() {
        return car;
    }

    public MimosaType cdr() {
        return cdr;
    }

    @Override
    public String toString() {
        return "(" + car + " " + cdr + ")";
    }

    @Override
    public int hashCode() {
        return car.hashCode() * 31 + cdr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) return true;
        if(!(obj instanceof MimosaPair)) return false;

        MimosaPair anotherPair = (MimosaPair) obj;
        return this.car.equals(anotherPair.car) && this.cdr.equals(anotherPair.cdr);
    }
}

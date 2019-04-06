package net.twodam.mimosa.types;

public class MimosaVal extends MimosaType {
    public Object val;
    public static final MimosaVal EMPTY = wrap(null);

    MimosaVal(Object val) {
        this.val = val;
    }

    public static MimosaVal wrap(Object obj) {
        if(obj instanceof String && ((String) obj).isEmpty()) return EMPTY;
        return new MimosaVal(obj);
    }

    public static boolean isEmpty(MimosaVal val) {
        return val == EMPTY;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) return true;
        if(!(obj instanceof MimosaVal)) return false;

        MimosaVal otherValObj = (MimosaVal) obj;
        return val.equals(otherValObj.val);
    }

    @Override
    public String toString() {
        return val.toString();
    }
}

package net.twodam.mimosa.exceptions;

import net.twodam.mimosa.types.MimosaNumber;
import net.twodam.mimosa.types.MimosaPair;
import net.twodam.mimosa.types.MimosaType;
import net.twodam.mimosa.utils.TypeUtil;

/**
 * Created by luckykoala on 19-4-5.
 */
public class MimosaIndexOutBoundException extends MimosaException {
    private MimosaIndexOutBoundException(MimosaPair list, MimosaNumber index) {
        super(String.format("list-ref: index reaches a non-pair\n" +
                "\n" +
                "  index: %d\n" +
                "\n" +
                "  in: %s", MimosaNumber.valToNum(index), list));
    }

    public static MimosaIndexOutBoundException of(MimosaType list, MimosaType index) {
        TypeUtil.checkType(MimosaPair.class, list);
        return new MimosaIndexOutBoundException((MimosaPair) list, (MimosaNumber) index);
    }
}

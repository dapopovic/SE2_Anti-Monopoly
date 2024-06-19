package at.aau.anti_mon.server.enums;

import java.util.Random;

/**
 */
public enum Name {

    RYANY,
    LARDE,
    DREWYN,
    ICHOL,
    WALTIN,
    RALPHYE,
    EPHES,
    CHELRY,
    GEORGE,
    PHILY,
    BENNE,
    ELYN,
    DENE,
    WENE,
    WYNE,
    BRYELLIA,
    ECIL,
    HERY,
    FANE,
    ERYS,
    LYNSOR,
    FHTAGNCHTHON,
    THARHOTH,
    CTHULHU,
    NYARLATHOTEP,
    YOGSOTHOTH,
    SHUBNIGGURATH,
    HASTUR,
    AZATHOTH,
    DAGON,
    HYDRA,
    CTHUGHA,
    ITHAQUA,
    YIG;

    private static final Random PRNG = new Random();

    public static Name randomName() {
        Name[] name = values();
        return name[PRNG.nextInt(name.length)];
    }


}

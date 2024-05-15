package at.aau.anti_mon.server.enums;

import java.util.Random;

/**
 * TODO only for tests
 */
public enum Name {

    Ryany,
    Larde,
    Drewyn,
    Ichol,
    Waltin,
    Ralphye,
    Ephes,
    Chelry,
    George,
    Phily,
    Benne,
    Elyn,
    Dene,
    Wene,
    Wyne,
    Bryellia,
    Ecil,
    Hery,
    Fane,
    Erys,
    Lynsor,
    Fhtagnchthon,
    Tharhoth,
    Cthulhu,
    Nyarlathotep,
    YogSothoth,
    ShubNiggurath,
    Hastur,
    Azathoth,
    Dagon,
    Hydra,
    Cthugha,
    Ithaqua,
    Yig;

    private static final Random PRNG = new Random();

    public static Name randomName() {
        Name[] name = values();
        return name[PRNG.nextInt(name.length)];
    }


}

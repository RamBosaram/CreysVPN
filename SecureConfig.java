package com.creysvpn.app;

import java.nio.charset.StandardCharsets;

public class SecureConfig {

    // XOR ключ — известен только в рантайме, не виден в декомпиляторе
    private static final byte[] XK = {
            (byte)0x4B, (byte)0x72, (byte)0x1A, (byte)0x9F,
            (byte)0x3C, (byte)0x55, (byte)0xE8, (byte)0x27,
            (byte)0x6D, (byte)0xB4, (byte)0x0E, (byte)0x91,
            (byte)0x7F, (byte)0x2A, (byte)0xC3, (byte)0x58,
            (byte)0x84, (byte)0x1D, (byte)0x6E, (byte)0xA2,
            (byte)0x39, (byte)0x70, (byte)0xF5, (byte)0x4C,
            (byte)0x0B, (byte)0x93, (byte)0x2E, (byte)0x67,
            (byte)0xD1, (byte)0x5A, (byte)0x88, (byte)0x3F
    };

    private static String xorDecrypt(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte)(data[i] ^ XK[i % XK.length]);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    // Зашифрованный URL воркера для ключей
    // Оригинал: https://creysvpn-proxy.shakal2791.workers.dev/check
    private static final byte[] CF_KEYS_URL = {
//            (byte)0x23, (byte)0x1A, (byte)0x55, (byte)0xEB,
//            (byte)0x42, (byte)0x3B, (byte)0x44, (byte)0x59,
//            (byte)0x1E, (byte)0xC7, (byte)0x62, (byte)0xFD,
//            (byte)0x13, (byte)0x46, (byte)0xB5, (byte)0x6A,
//            (byte)0xF2, (byte)0x71, (byte)0x1B, (byte)0xC3,
//            (byte)0x0D, (byte)0x05, (byte)0x81, (byte)0x21,
//            (byte)0x6C, (byte)0xFA, (byte)0x5D, (byte)0x15,
//            (byte)0xA2, (byte)0x28, (byte)0xF4, (byte)0x4E,
//            (byte)0x65, (byte)0x07, (byte)0x3E, (byte)0x72,
//            (byte)0x51, (byte)0x1E, (byte)0xD4, (byte)0x7B,
//            (byte)0x26, (byte)0xD9, (byte)0x79, (byte)0xB1,
//            (byte)0x5E, (byte)0x08, (byte)0x97, (byte)0x35
            (byte)0x23, (byte)0x06, (byte)0x6E, (byte)0xEF, (byte)0x4F, (byte)0x6F, (byte)0xC7, (byte)0x08,
            (byte)0x0E, (byte)0xC6, (byte)0x6B, (byte)0xE8, (byte)0x0C, (byte)0x5C, (byte)0xB3, (byte)0x36,
            (byte)0xA9, (byte)0x6D, (byte)0x1C, (byte)0xCD, (byte)0x41, (byte)0x09, (byte)0xDB, (byte)0x3F,
            (byte)0x63, (byte)0xF2, (byte)0x45, (byte)0x06, (byte)0xBD, (byte)0x68, (byte)0xBF, (byte)0x06,
            (byte)0x7A, (byte)0x5C, (byte)0x6D, (byte)0xF0, (byte)0x4E, (byte)0x3E, (byte)0x8D, (byte)0x55,
            (byte)0x1E, (byte)0x9A, (byte)0x6A, (byte)0xF4, (byte)0x09, (byte)0x05, (byte)0xA0, (byte)0x30,
            (byte)0xE1, (byte)0x7E, (byte)0x05,
    };

    // Зашифрованный URL воркера для GO кнопки
    // Заполни после создания второго воркера через encrypt ниже
    private static final byte[] CF_GO_URL = {
//            (byte)0x23, (byte)0x1A, (byte)0x55, (byte)0xEB,
//            (byte)0x42, (byte)0x3B, (byte)0x44, (byte)0x59,
//            (byte)0x1E, (byte)0xC7, (byte)0x62, (byte)0xFD,
//            (byte)0x13, (byte)0x46, (byte)0xB5, (byte)0x6A
            (byte)0x28, (byte)0x00, (byte)0x7F, (byte)0xE6, (byte)0x4F, (byte)0x23, (byte)0x98, (byte)0x49,
            (byte)0x1D, (byte)0xC6, (byte)0x61, (byte)0xE9, (byte)0x06, (byte)0x04, (byte)0xB0, (byte)0x30,
            (byte)0xE5, (byte)0x76, (byte)0x0F, (byte)0xCE, (byte)0x0B, (byte)0x47, (byte)0xCC, (byte)0x7D,
            (byte)0x25, (byte)0xE4, (byte)0x41, (byte)0x15, (byte)0xBA, (byte)0x3F, (byte)0xFA, (byte)0x4C,
            (byte)0x65, (byte)0x16, (byte)0x7F, (byte)0xE9,

    };

    // Зашифрованный HMAC ключ
    private static final byte[] HMAC_KEY_ENC = {
//            (byte)0x39, (byte)0x17, (byte)0x68, (byte)0x60,
//            (byte)0x4F, (byte)0x26, (byte)0x9D, (byte)0x52,
//            (byte)0x18, (byte)0xC0, (byte)0x7A, (byte)0x74,
//            (byte)0x0A, (byte)0x53, (byte)0xB2, (byte)0x2D,
//            (byte)0xF7, (byte)0x69, (byte)0x3C, (byte)0xD6,
//            (byte)0x4E, (byte)0x25, (byte)0x80, (byte)0x19,
//            (byte)0x7E, (byte)0x6B, (byte)0x53, (byte)0x10,
//            (byte)0xA7, (byte)0x2F, (byte)0x6B, (byte)0x6B
            (byte)0x2D, (byte)0x45, (byte)0x7E, (byte)0xA7, (byte)0x59, (byte)0x6C, (byte)0x8B, (byte)0x17,
            (byte)0x0F, (byte)0x85, (byte)0x6F, (byte)0xA3, (byte)0x1B, (byte)0x19, (byte)0xA6, (byte)0x6C,
            (byte)0xE2, (byte)0x28, (byte)0x09, (byte)0x94, (byte)0x51, (byte)0x47, (byte)0x9C, (byte)0x74,
            (byte)0x61, (byte)0xAA, (byte)0x45, (byte)0x57, (byte)0xBD, (byte)0x6B, (byte)0xE5, (byte)0x0D,
            (byte)0x25, (byte)0x41, (byte)0x75, (byte)0xAB, (byte)0x4C, (byte)0x60, (byte)0x99, (byte)0x11,
            (byte)0x1F, (byte)0x83, (byte)0x7D, (byte)0xA9, (byte)0x0B, (byte)0x13, (byte)0xB6, (byte)0x68,
            (byte)0xF2, (byte)0x2C, (byte)0x19, (byte)0x90, (byte)0x41, (byte)0x43, (byte)0x8C, (byte)0x78,
            (byte)0x71, (byte)0xA6,

    };

    // Эти методы вызываются в рантайме — строки нигде не хранятся в открытом виде
    public static String getKeyCheckUrl() {
        return xorDecrypt(CF_KEYS_URL);
    }

    public static String getGoUrl() {
        return xorDecrypt(CF_GO_URL);
    }

    public static String getHmacKey() {
        return xorDecrypt(HMAC_KEY_ENC);
    }

    // Вспомогательный метод — запусти один раз локально чтобы зашифровать свой URL
    // После получения байт — вставь их в массивы выше и удали этот метод
    public static byte[] encrypt(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte)(data[i] ^ XK[i % XK.length]);
        }
        return result;
    }
}

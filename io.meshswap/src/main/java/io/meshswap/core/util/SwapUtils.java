package io.meshswap.core.util;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;

public class SwapUtils {
    public static byte[] hash160(byte[] input) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(input, 0, input.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

}

package io.meshswap.core.util;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.VarInt;

import java.util.List;

public class FeeUtils {
    public static final long SATOSHI_PER_BITCOIN = 100000000L;
    public static final long MAX_SATOSHI_PER_TX = SATOSHI_PER_BITCOIN * 21000000L;

    // FeeForSerializeSize calculates the required fee for a transaction of some
    // arbitrary size given a mempool's relay fee policy.
    public static Coin feeForSerializeSize(Coin relayFeePerKb, int txSerializeSize) {
        Coin fee = relayFeePerKb.multiply(txSerializeSize).divide(1000);

        if (fee.longValue() == 0 && relayFeePerKb.value > 0) {
            fee = relayFeePerKb;
        }

        if (fee.longValue() < 0 || fee.longValue() > MAX_SATOSHI_PER_TX) {
            fee = Coin.valueOf(MAX_SATOSHI_PER_TX);
        }

        return fee;
    }

}

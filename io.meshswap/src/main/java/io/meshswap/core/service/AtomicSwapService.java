package io.meshswap.core.service;

import io.meshswap.core.dto.InitiateResult;
import io.meshswap.core.dto.RedeemResult;

import java.math.BigDecimal;

public interface AtomicSwapService {
    public InitiateResult cmdInitiate(String initiatorAddress, String participantAddressStr, BigDecimal amount, boolean signTx);
    public RedeemResult cmdRedeem(String contractHex, String contractTxHex, String secret, String participantAddressStr);
}

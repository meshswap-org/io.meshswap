package io.meshswap.core.controller;

import io.meshswap.core.dto.InitiateResult;
import io.meshswap.core.dto.RedeemResult;
import io.meshswap.core.dto.RefundResult;
import io.meshswap.core.service.NativeSwapService;
import io.meshswap.core.service.RemoteSwapService;
import io.meshswap.core.util.RunCommandViaSsh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SwapControllerRest {
    @Autowired
    NativeSwapService swapService;

    @GetMapping("/initiate")
    public InitiateResult initiate(@RequestParam String initiatorAddress, @RequestParam String participantAddress, @RequestParam BigDecimal amount) {
        return swapService.cmdInitiate(initiatorAddress,participantAddress,amount, true);
    }

    @GetMapping("/redeem")
    public RedeemResult redeem(@RequestParam String contractHex, @RequestParam String contractTx, @RequestParam String secret, @RequestParam String participantAddress) {
        return swapService.cmdRedeem(contractHex,contractTx,secret, participantAddress);
    }

    @GetMapping("/refund")
    public RefundResult refund(@RequestParam String contractHex, @RequestParam String contractTx) {
        return swapService.cmdRefund(contractHex,contractTx);
    }

}

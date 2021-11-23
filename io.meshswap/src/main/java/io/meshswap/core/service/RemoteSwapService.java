package io.meshswap.core.service;

import io.meshswap.core.dto.InitiateResult;
import io.meshswap.core.dto.RedeemResult;
import io.meshswap.core.util.RunCommandViaSsh;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.apache.commons.lang3;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class RemoteSwapService implements AtomicSwapService {
    @Autowired
    RunCommandViaSsh runCommandViaSsh;

    @Override
    public InitiateResult cmdInitiate(String initiatorAddress, String participantAddressStr, BigDecimal amount, boolean signTx) {
        InitiateResult result = new InitiateResult();
        String runcmd = String.format("/home/ec2-user/bin/btcatomicswap -testnet -rpcuser=test -rpcpass=test -s localhost:18201 initiate %s %s",participantAddressStr,amount.movePointLeft(8).toString());
        log.info("CMD: {}",runcmd);
        List<String> execResult = runCommandViaSsh.runCommand(runcmd);
        int lineIndex = 0;

        for (String line : execResult) {
            log.info(line);
            //String[] parts =  sp  splitString()line.split(" ");
            if (line.startsWith("Secret:")) {
                String[] parts = line.split(" ");
                result.secret = parts[1].trim();
            } else if (line.startsWith("Secret hash:")) {
                String[] parts = line.split(" ");
                result.secretHash = parts[1].trim();
            } else if (line.startsWith("Contract")) {
                result.contractTx = execResult.get(lineIndex+1);
            } else if (line.startsWith("Contract transaction")) {
                result.redeemTx = execResult.get(lineIndex+1);
            } else if (line.startsWith("Refund transaction")) {
                result.refundTx = execResult.get(lineIndex+1);
            }
            lineIndex++;
        }
        return result;
    }

    @Override
    public RedeemResult cmdRedeem(String contractHex, String contractTxHex, String secret, String participantAddressStr) {
        return null;
    }
}

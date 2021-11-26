package io.meshswap.core.controller;

import io.meshswap.core.dto.InitiateResult;
import io.meshswap.core.service.NativeSwapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@Slf4j
public class SwapController {
    @Autowired
    NativeSwapService swapService;

    @RequestMapping("")
    public String home(Model model) {
        return "forward:swap/init/create";
    }

    @RequestMapping("/swap/init/create")
    public String createSwap(Model model,
                             @RequestParam(name = "initiator", required = false) String initiatorAddress,
                             @RequestParam(name = "participant", required = false) String participantAddress,
                             @RequestParam(required = false) BigDecimal amount) {
        InitiateResult result = null;

        if (StringUtils.hasText(initiatorAddress) && StringUtils.hasText(participantAddress) && amount != null) {
            result = swapService.cmdInitiate(initiatorAddress, participantAddress, amount, false);
            model.addAttribute("initiatorAddress",initiatorAddress);
            model.addAttribute("participantAddress",participantAddress);
            model.addAttribute("amount",amount);
            model.addAttribute("initTx", result);
        }
        return "swapinit";
    }
}

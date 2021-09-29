package io.meshswap.core.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Info {
    @AllArgsConstructor
    @Data
    public static class InfoData {
        private String name;
        private String version;
    }

    @GetMapping("/api/v1/info")
    public InfoData info() {
        return new InfoData("MeshSwap", "0.0.1");
    }
}

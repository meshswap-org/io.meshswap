package io.meshswap.core.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class AddressServiceTest {
    @InjectMocks
    NativeSwapService addressService;


    @Test
    public void shouldGenerateAddress() {
        addressService = new NativeSwapService();
        assertEquals("3A3Ug1TybVyezkKMSzYDDWXrYVJVKQQqLr",addressService.createP2SHAddress("mpzCWUoGJdagjtXrHuF9U28ZJFaQ2LenF7","n3CyxhPqpLbhnPGLXLeeAL7HBeqojyEcZc"));
    }

    @Test
    public void shouldGenerateScript() {
        addressService = new NativeSwapService();
    //    assertEquals("111",addressService.generateLockScript("mqgXoWq6V9YMy5qK2fmowtp6TBPwqPdJWi","mqvwkAu7qpZQDuU1BS1oSgazSK1oA2u9BP"));
    }
}
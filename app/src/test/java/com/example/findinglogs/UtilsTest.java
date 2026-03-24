package com.example.findinglogs;

import com.example.findinglogs.model.util.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void celsiusConversion_freezingPoint_returnsZero() {
        // 273.15K = 0°C
        String result = Utils.getCelsiusTemperatureFromKevin(273.15f);
        assertTrue(result.contains("0"));
        assertTrue(result.contains("C"));
    }

    @Test
    public void celsiusConversion_boilingPoint_returns100() {
        // 373.15K = 100°C
        String result = Utils.getCelsiusTemperatureFromKevin(373.15f);
        assertTrue(result.contains("100"));
    }

    @Test
    public void celsiusConversion_typicalBrazilianTemp_returnsExpected() {
        // 300K = 26.85°C
        String result = Utils.getCelsiusTemperatureFromKevin(300f);
        assertTrue(result.contains("26"));
    }

    @Test
    public void celsiusConversion_returnsFormattedString() {
        String result = Utils.getCelsiusTemperatureFromKevin(300f);
        assertTrue(result.endsWith("C"));
    }
}

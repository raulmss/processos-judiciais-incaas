package com.incaas.tjrn.util;

import java.time.*;

public class Utils {

    public static Instant[] intervaloDoDia(LocalDate data) {
        ZonedDateTime inicio = data.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime fim = data.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return new Instant[]{inicio.toInstant(), fim.toInstant()};
    }
}

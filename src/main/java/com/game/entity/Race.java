package com.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Race {
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "human", nullable = false)
    HUMAN,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "dwarf", nullable = false)
    DWARF,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "elf", nullable = false)
    ELF,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "giant", nullable = false)
    GIANT,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "orc", nullable = false)
    ORC,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "troll", nullable = false)
    TROLL,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "hobbit", nullable = false)
    HOBBIT
}
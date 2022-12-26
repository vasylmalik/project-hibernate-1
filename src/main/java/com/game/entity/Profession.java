package com.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Profession {
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "warrior", nullable = false)
    WARRIOR,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "rogue", nullable = false)
    ROGUE,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "sorcerer", nullable = false)
    SORCERER,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "cleric", nullable = false)
    CLERIC,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "paladin", nullable = false)
    PALADIN,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "nazgul", nullable = false)
    NAZGUL,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "warlock", nullable = false)
    WARLOCK,
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "druid", nullable = false)
    DRUID
}
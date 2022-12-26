package com.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

//@Table(name = "profession", schema = "rpg")
public enum Profession {
    WARRIOR,
    ROGUE,
    SORCERER,
    CLERIC,
    PALADIN,
    NAZGUL,
    WARLOCK,
    DRUID
}
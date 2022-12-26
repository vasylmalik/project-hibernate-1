package com.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

//@Table(name = "race", schema = "rpg")
public enum Race {
    HUMAN,
    DWARF,
    ELF,
    GIANT,
    ORC,
    TROLL,
    HOBBIT
}
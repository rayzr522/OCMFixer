package me.rayzr522.ocmfixer;

public enum PlayerAttribute {
    ATTACK_SPEED("generic.attackSpeed", "minecraft:generic.attack_speed", 4.0),
    ATTACK_DAMAGE("generic.attackDamage", "minecraft:generic.attack_damage", 1.0),
    MOVEMENT_SPEED("generic.movementSpeed", "minecraft:generic.movement_speed", 0.100000001490116),
    MAX_HEALTH("generic.maxHealth", "minecraft:generic.max_health", 20.0),
    ARMOR("generic.armor", "minecraft:generic.armor", 0.0),
    ARMOR_TOUGHNESS("generic.armorToughness", "minecraft:generic.armor_toughness", 0.0),
    KNOCKBACK_RESISTANCE("generic.knockbackResistance", "minecraft:generic.knockback_resistance", 0.0),
    LUCK("generic.luck", "minecraft:generic.luck", 0.0);

    private final String legacyName;
    private final String namespacedName;
    private final double defaultValue;

    PlayerAttribute(String legacyName, String namespacedName, double defaultValue) {
        this.legacyName = legacyName;
        this.namespacedName = namespacedName;
        this.defaultValue = defaultValue;
    }

    public String getLegacyName() {
        return legacyName;
    }

    public String getNamespacedName() {
        return namespacedName;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public boolean appliesTo(String attributeName) {
        return attributeName.equals(legacyName) || attributeName.equals(namespacedName);
    }
}

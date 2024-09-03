module ktorm.support.dameng {
    requires ktorm.core;
    exports org.ktorm.support.dameng;
    provides org.ktorm.database.SqlDialect with org.ktorm.support.dameng.DamengDialect;
}

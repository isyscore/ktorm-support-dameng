package org.ktorm.support.dameng

import org.ktorm.database.Database
import org.ktorm.database.SqlDialect
import org.ktorm.expression.SqlFormatter

/**
 * [SqlDialect] implementation for Dameng database.
 */
open class DamengDialect : SqlDialect {

    override fun createSqlFormatter(database: Database, beautifySql: Boolean, indentSize: Int): SqlFormatter = DamengFormatter(database, beautifySql, indentSize)

}

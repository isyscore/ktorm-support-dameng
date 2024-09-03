package org.ktorm.support.dameng

import org.ktorm.database.Database
import org.ktorm.expression.*
import org.ktorm.schema.IntSqlType

/**
 * [SqlFormatter] implementation for Dameng, formatting SQL expressions as strings with their execution arguments.
 */
open class DamengFormatter(database: Database, beautifySql: Boolean, indentSize: Int) : SqlFormatter(database, beautifySql, indentSize) {

    override fun shouldQuote(identifier: String): Boolean = identifier.startsWith('_') || super.shouldQuote(identifier)

    override fun visitQuery(expr: QueryExpression): QueryExpression {
        if (expr.offset == null && expr.limit == null) {
            return super.visitQuery(expr)
        }

        val offset = expr.offset ?: 0
        val minRowNum = offset + 1
        val maxRowNum = expr.limit?.let { offset + it } ?: Int.MAX_VALUE

        val tempTableName = "_t"

        writeKeyword("select * ")
        newLine(Indentation.SAME)
        writeKeyword("from (")
        newLine(Indentation.INNER)
        writeKeyword("select ")
        write("${tempTableName.quoted}.*, ")
        writeKeyword("rownum ")
        write("${"_rn".quoted} ")
        newLine(Indentation.SAME)
        writeKeyword("from ")

        visitQuerySource(
            when (expr) {
                is SelectExpression -> expr.copy(tableAlias = tempTableName, offset = null, limit = null)
                is UnionExpression -> expr.copy(tableAlias = tempTableName, offset = null, limit = null)
            }
        )

        newLine(Indentation.SAME)
        writeKeyword("where rownum <= ?")
        newLine(Indentation.OUTER)
        write(") ")
        newLine(Indentation.SAME)
        writeKeyword("where ")
        write("${"_rn".quoted} >= ? ")

        _parameters += ArgumentExpression(maxRowNum, IntSqlType)
        _parameters += ArgumentExpression(minRowNum, IntSqlType)

        return expr
    }

    override fun writePagination(expr: QueryExpression) {
        throw AssertionError("Never happen.")
    }

    override fun visitUnion(expr: UnionExpression): UnionExpression {
        if (expr.orderBy.isEmpty()) {
            return super.visitUnion(expr)
        }
        writeKeyword("select * ")
        newLine(Indentation.SAME)
        writeKeyword("from ")
        visitQuerySource(expr.copy(orderBy = emptyList(), tableAlias = null))
        newLine(Indentation.SAME)
        writeKeyword("order by ")
        visitExpressionList(expr.orderBy)
        return expr
    }
}

package mx.buap.fcc.clasificador.model

import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

/**
 * @author Carlos Montoya
 * @since 13/03/2019
 */
open class DataSet(
		val id: Int = idGenerator.incrementAndGet(),
		val attributes: List<Attribute>) : Iterable<Row>
{
	companion object { private val idGenerator = AtomicInteger(0) }

	private val rows: MutableList<Row> = mutableListOf()

	val rowSize: Int get() = rows.size

	val columnSize: Int = attributes.size

	/**
	 * Calcula el minimo para cada atributo de este DataSet. Si el atributo es de tipo
	 * nominal, entonces el resultado asignado para esa columna es 0
	 */
	private val minimums: Array<BigDecimal>
	get() {
		if (minCache != null) {
			minCache = Array(columnSize) { ZERO }
			for (i in 0 until columnSize)
				minCache!![i] =
						if (isNominal(i)) ZERO
						else getColumnStream(i)
								.min { o1, o2 -> o1.compareTo(o2) }
								.orElse(ZERO)
		}
		return minCache!!
	}
	private var minCache: Array<BigDecimal>? = null

	private val log = LogManager.getLogger()

	/**
	 * Agrega un Row a este DataSet, si el numero de columnas de ese Row
	 * es igual al numero de columnas de este DataSet.
	 *
	 * @param row El Row a agregar a este DataSet
	 */
	fun add(row: Row) : Boolean {
		if (row.size() != columnSize) return false

		rows.add(row) // TODO verificar los atributos nominales
		row.dataSet = this
		return true
	}

	operator fun get(i: Int): Row = rows[i]

	/**
	 * Retorna un Stream con los valores de la columna especificada.
	 *
	 * @param column numero de la columna a calcular
	 * @return un Stream con los valores de una columna especifica.
	 */
	private fun getColumnStream(column: Int): Stream<BigDecimal> =
			rows.stream().map { row -> row[column] }

	/**
	 * Retorna true si la columna especificada es atributo de tipo numerico.
	 *
	 * @param c El numero de columna
	 * @return Si el tipo de atributo es numerico.
	 */
	fun isNumerico(c: Int): Boolean = AttributeType.NUMERICO == attributes[c].type

	/**
	 * Retorna true si la columna especificada es atributo de tipo nominal.
	 *
	 * @param c El numero de columna
	 * @return Si el tipo de atributo es nominal.
	 */
	fun isNominal(c: Int): Boolean = AttributeType.NOMINAL == attributes[c].type

	override fun iterator(): Iterator<Row> = rows.iterator()

	/**
	 * Representa este DataSet en un String
	 * @return la representacion textual de este DataSet
	 */
	override fun toString(): String {
		val sb = StringBuilder()
		rows.forEach { r -> sb.append(r).append("\n") }
		return "DataSet{\n" +
				"rowSize=" + rows.size + "\n" +
				"atributes=" + attributes + "\n" +
				"rows={\n" + sb.toString() + "}" +
				'}'.toString()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as DataSet
		if (id != other.id) return false

		return true
	}

	override fun hashCode(): Int {
		return id
	}
}

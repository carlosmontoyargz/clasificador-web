package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.tools.MathTools
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Carlos Montoya
 * @since 13/03/2019
 */
open class DataSet(
		val id: Int = idGenerator.incrementAndGet(),
		val attributes: List<Attribute>) : Iterable<Row>
{
	companion object {
		private val log = LogManager.getLogger()
		private val idGenerator = AtomicInteger(0)
	}

	private val rows: MutableList<Row> = mutableListOf()

	val rowsSize: Int get() = rows.size

	val attributesSize: Int = attributes.size

	/**
	 * Obtiene el i-esimo renglon de este DataSet. Es un operador pues de esta
	 * forma es posible invocar la funcion como notacion de arrays.
	 *
	 * @param i
	 * @return el i-esimo renglon de este DataSet
	 */
	operator fun get(i: Int): Row = rows[i]

	/**
	 * Agrega un Row a este DataSet, si el numero de columnas de ese Row
	 * es igual al numero de columnas de este DataSet.
	 *
	 * @param row El Row a agregar a este DataSet
	 */
	fun add(row: Row) : Boolean {
		if (row.size() != attributesSize) return false

		// TODO verificar los atributos nominales y los cache de min y max
		rows.add(row)
		row.dataSet = this
		return true
	}

	/**
	 * Normaliza este DataSet mediante el metodo min-max.
	 *
	 * @param newMin el nuevo minimo para todas las columnas
	 * @param newMax el nuevo maximo para todas las columnas
	 */
	fun minMax(newMin: BigDecimal, newMax: BigDecimal) {
		rows.forEach { row -> row.minmax(newMin, newMax) }
	}

	/**
	 * Normaliza este DataSet mediante el metodo Z-Score.
	 *
	 */
	fun zScore() { rows.forEach { row -> row.zScore() } }

	/**
	 * Calcula el minimo para cada atributo de este DataSet. Si el atributo es de tipo
	 * nominal, entonces el resultado asignado para esa columna es 0
	 */
	val maximum: Array<BigDecimal>
	get() {
		if (maxCache == null) {
			maxCache = Array(attributesSize) { ZERO }
			for (i in 0 until attributesSize)
				if (isNumerical(i))
					maxCache!![i] = getColumnStream(i)
							.max { o1, o2 -> o1.compareTo(o2) }
							.orElse(ZERO)
			log.info("Maximum computed: ${maxCache!!.contentToString()}")
		}
		return maxCache!!
	}
	private var maxCache: Array<BigDecimal>? = null

	private val log = LogManager.getLogger()

	/**
	 * Calcula el minimo para cada atributo de este DataSet. Si el atributo es de tipo
	 * nominal, entonces el resultado asignado para esa columna es 0
	 */
	val minimum: Array<BigDecimal>
	get() {
		if (minCache == null) {
			minCache = Array(attributesSize) { ZERO }
			for (i in 0 until attributesSize)
				if (isNumerical(i))
					minCache!![i] = getColumnStream(i)
							.min { o1, o2 -> o1.compareTo(o2) }
							.orElse(ZERO)
			log.info("Minimum computed: ${minCache!!.contentToString()}")
		}
		return minCache!!
	}
	private var minCache: Array<BigDecimal>? = null

	/**
	 * Calcula la desviacion estandar para este DataSet
	 */
	val standardDeviation: Array<BigDecimal>
	get() {
		if (stdDvnCache == null) {
			stdDvnCache = Array(attributesSize) { ZERO }
			for (i in 0 until attributesSize)
				if (isNumerical(i))
					stdDvnCache!![i] = MathTools
							.sqrt(getColumnStream(i)
									.map { v -> v
											.subtract(average[i])
											.pow(2)
									}
									.reduce(ZERO) { sum, augend -> sum.add(augend) }
									.divide(BigDecimal(rows.size), Row.precision, RoundingMode.HALF_UP),
									Row.precision)
			log.info("Standard Deviation computed: ${stdDvnCache!!.contentToString()}")
		}
		return stdDvnCache!!
	}
	private var stdDvnCache: Array<BigDecimal>? = null

	/**
	 * Calcula el promedio para cada atributo de este DataSet
	 */
	val average: Array<BigDecimal>
	get() {
		if (avgCache == null) {
			avgCache = Array(attributesSize) { ZERO }
					.mapIndexed { i, _ ->
						if (isNumerical(i)) averageAtAtribute(i)
						else modeAtAttribute(i)
					}
					.toTypedArray()
			log.info("Average computed: ${avgCache!!.contentToString()}")
		}
		return avgCache!!
	}
	private var avgCache: Array<BigDecimal>? = null

	/**
	 * Calcula el promedio del i-esimo atributo de este DataSet.
	 *
	 * @return el promedio del i-esimo atributo de este DataSet.
	 */
	private fun averageAtAtribute(i: Int): BigDecimal =
			if (rows.size == 0) ZERO
			else getColumnStream(i)
					.reduce(ZERO) { sum, augend -> sum.add(augend) }
					.divide(BigDecimal(rows.size), Row.precision, RoundingMode.HALF_UP)

	/**
	 * Calcula la moda del i-esimo atributo de este DataSet.
	 *
	 * @return la moda del i-esimo atributo de este DataSet.
	 */
	private fun modeAtAttribute(n: Int): BigDecimal {
		val columnValues = getColumnStream(n).toArray<BigDecimal> { arrayOfNulls(rowsSize)}
		var mode = ZERO ; var maxCount = 0
		var i = 0; var j: Int
		while (i < columnValues.size) {
			var count = 0 ; j = 0
			while (j < columnValues.size) {
				if (columnValues[j] == columnValues[i]) ++count
				++j
			}
			if (count > maxCount) {
				maxCount = count
				mode = columnValues[i]
			}
			++i
		}
		return mode
	}

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
	fun isNumerical(c: Int): Boolean = AttributeType.NUMERICAL == attributes[c].type

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
		return "DataSet{\n" +
				"rowSize=" + rows.size + "\n" +
				"atributes=" + attributes + "\n" +
				"rows={\n" +
				StringBuilder()
						.apply {
							rows.forEach { r -> this.append(r).append("\n") }
						} +
				"}\n" +
				'}'
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as DataSet
		if (id != other.id) return false

		return true
	}

	override fun hashCode(): Int = id
}

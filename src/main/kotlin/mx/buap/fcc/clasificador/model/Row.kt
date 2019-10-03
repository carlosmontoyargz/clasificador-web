package mx.buap.fcc.clasificador.model

import java.math.BigDecimal
import java.math.BigDecimal.*

/**
 *
 * @author Carlos Montoya
 */
class Row constructor(
		val indice: Int = 0,
		val values: Array<BigDecimal>,
		var dataSet: DataSet? = null)
{
	fun size() = values.size

	operator fun get(i: Int) = values[i]

	operator fun set(i: Int, att: BigDecimal) { values[i] = att }

	/**
	 * Normaliza este DataRow mediante el metodo min-max, a partir de los parametros especificados.
	 *
	 */
	fun minmax(minRow: Array<BigDecimal>, maxRow: Array<BigDecimal>,
			   newMin: BigDecimal, newMax: BigDecimal)
	{
		val diffNewMinNewMax = newMax - newMin
		for (i in values.indices) {
			val diffMinMax = maxRow[i] - minRow[i]
			if (dataSet!!.isNumerico(i))
				values[i] = (((values[i] - minRow[i]) / diffMinMax) * diffNewMinNewMax) + newMin
		}
	}

	/**
	 * Normaliza este Row mediante el metodo z-score, a partir de los parametros especificados.
	 *
	 */
	fun zScore(avg: Array<BigDecimal>, stddev: Array<BigDecimal>)
	{
		for (i in values.indices)
			if (dataSet!!.isNumerico(i) && stddev[i] != ZERO)
				values[i] = (values[i] - avg[i]) / stddev[i]
	}

	/**
	 * Normaliza este Row mediante el metodo decimal-scaling, a partir de los parametros especificados.
	 *
	 */
	fun decimalScaling(j: IntArray)
	{
		for (i in values.indices)
			if (dataSet!!.isNumerico(i))
				values[i] = values[i].movePointLeft(j[i]).stripTrailingZeros()
	}

	override fun toString(): String {
		return "Row(indice=$indice,attributes=${values.contentToString()})"
	}
}

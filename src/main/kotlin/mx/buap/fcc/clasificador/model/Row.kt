package mx.buap.fcc.clasificador.model

import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode

/**
 *
 * @author Carlos Montoya
 */
class Row constructor(
		val indice: Int = 0,
		val values: Array<BigDecimal>,
		var dataSet: DataSet? = null)
{
	companion object { const val precision = 13 }

	operator fun get(i: Int) = values[i]

	operator fun set(i: Int, att: BigDecimal) { values[i] = att }

	fun size() = values.size

	/**
	 * Normaliza este DataRow mediante el metodo min-max, a partir de los parametros especificados.
	 *
	 */
	fun minmax(newMin: BigDecimal, newMax: BigDecimal)
	{
		val diffNewMinNewMax = newMax - newMin
		for (i in values.indices) {
			val diffMinMax = maxRow[i] - minRow[i]
			if (dataSet!!.isNumerical(i))
				values[i] = values[i]
						.subtract(dataSet!!.minimum[i])
						.divide(diffMinMax, precision, RoundingMode.HALF_UP)
						.multiply(diffNewMinNewMax)
						.add(newMin)
						.stripTrailingZeros()
		}
	}

	/**
	 * Normaliza este Row mediante el metodo z-score, a partir de los parametros especificados.
	 *
	 */
	fun zScore() {
		val stdDvtn = dataSet!!.standardDeviation
		val avrg = dataSet!!.average
		for (i in values.indices) {
			if (dataSet!!.isNumerical(i) && stdDvtn[i] != ZERO )
				values[i] = values[i]
						.subtract(avrg[i])
						.divide(stdDvtn[i], precision, RoundingMode.HALF_UP)
						.stripTrailingZeros()
		}
	}

	/**
	 * Normaliza este Row mediante el metodo decimal-scaling, a partir de los parametros especificados.
	 *
	 */
	fun decimalScaling(j: IntArray)
	{
		for (i in values.indices)
			if (dataSet!!.isNumerical(i))
				values[i] = values[i].movePointLeft(j[i]).stripTrailingZeros()
	}

	override fun toString(): String {
		return "Row(indice=$indice,attributes=${values.contentToString()})"
	}
}

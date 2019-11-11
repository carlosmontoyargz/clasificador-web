package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.tools.MathTools
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode

/**
 *
 * @author Carlos Montoya
 */
class Instance
	constructor(val indice: Int = 0,
				val clazz: Int = -1,
				val data: Array<BigDecimal>)
{
	companion object { const val precision = 13 }

	private var dataSet: DataSet? = null

	operator fun get(i: Int) = data[i]

	operator fun set(i: Int, att: BigDecimal) { data[i] = att }

	fun size() = data.size

	fun setDataSet(ds: DataSet) { this.dataSet = ds }

	fun distance(other: Instance): BigDecimal {
		var sqrDistSum = ZERO
		for (i in data.indices)
			sqrDistSum += data[i].subtract(other[i]).pow(2)
		return MathTools.sqrt(sqrDistSum, precision)
	}

	/**
	 * Retorna la distancia entre el atributo especificado y el segundo parametro
	 * de la funcion. Si el atributo es de tipo nominal la distancia es igual a
	 * 1 si el valor del atributo es igual al especificado, o 0 en caso contrario.
	 */
	private fun distance(attribute: Int, other: BigDecimal?): BigDecimal =
			when {
				other == null -> ONE
				dataSet?.isNominal(attribute) == true ->
					if (data[attribute] == other) ZERO
					else ONE
				else -> data[attribute].subtract(other).abs()
			}

	/**
	 * Normaliza este DataRow mediante el metodo min-max, a partir de los parametros especificados.
	 *
	 */
	fun minmax(newMin: BigDecimal, newMax: BigDecimal) {
		val diffNewMinNewMax = newMax - newMin
		for (i in data.indices) {
			val diffMinMax = dataSet!!.maximum[i] - dataSet!!.minimum[i]
			if (diffMinMax == ZERO)
				data[i] = (newMin + newMax) / BigDecimal(2)
			else if (dataSet!!.isNumerical(i))
				data[i] = data[i]
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
		for (i in data.indices) {
			if (dataSet!!.isNumerical(i) && stdDvtn[i] != ZERO )
				data[i] = data[i]
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
		for (i in data.indices)
			if (dataSet!!.isNumerical(i))
				data[i] = data[i].movePointLeft(j[i]).stripTrailingZeros()
	}

	override fun toString() = "Instance(indice=$indice, clazz=$clazz, values=${data.contentToString()})"
}

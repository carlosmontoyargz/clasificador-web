package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.tools.MathTools
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList

/**
 * Clase que modela un conjunto de instancias asi como sus tipos de atributos.
 *
 * @author Carlos Montoya
 * @since 13/03/2019
 */
open class DataSet
	constructor(var id: String = UUID.randomUUID().toString(),
				val attributes: List<Attribute>,
				val classSize: Int)
	: Iterable<Instance>
{
	companion object { private val log = LogManager.getLogger() }

	/**
	 * Construye un DataSet con el tamano especificado con todos los atributos
	 * como numericos.
	 */
	constructor(attSize: Int, classSize: Int):
			this(attributes = List(attSize) { Attribute(AttributeType.NUMERICAL) },
				 classSize = classSize)

	/**
	 * El numero de instancias de este DataSet
	 */
	val instanceSize: Int get() = mtInstances.size

	/**
	 * El numero de atributos de este DataSet
	 */
	val attributeSize: Int = attributes.size

	/**
	 * La lista mutable privada de instancias de este DataSet
	 */
	private val mtInstances: MutableList<Instance> = mutableListOf()

	/**
	 * La lista inmutable publica de instancias de este DataSet
	 */
	val instances get() = mtInstances.toList()

	/**
	 * La lista de clases del dataset
	 */
	val classes = ArrayList<String>().apply {
		for (i in 0 until classSize) add(i.toString())
	}

	/**
	 * El iterador de la clase
	 */
	override fun iterator(): Iterator<Instance> = instances.iterator()

	/**
	 * Obtiene el i-esimo renglon de este DataSet. Es un operador pues de esta
	 * forma es posible invocar la funcion como notacion de arrays (dataSet[i]).
	 *
	 * @param i
	 * @return el i-esimo renglon de este DataSet
	 */
	operator fun get(i: Int): Instance = mtInstances[i]

	/**
	 * Retorna un Stream con los valores del atributo especificado.
	 *
	 * @param a posicion del atributo
	 * @return un Stream con los valores de un atributo especifico.
	 */
	private fun getAttributeStream(a: Int): Stream<BigDecimal> =
			mtInstances.stream().map { row -> row[a] }

	/**
	 * Retorna true si la columna especificada es atributo de tipo numerico.
	 *
	 * @param a El numero de columna
	 * @return Si el tipo de atributo es numerico.
	 */
	fun isNumerical(a: Int): Boolean = AttributeType.NUMERICAL == attributes[a].type

	/**
	 * Retorna true si la columna especificada es atributo de tipo nominal.
	 *
	 * @param a El numero de columna
	 * @return Si el tipo de atributo es nominal.
	 */
	fun isNominal(a: Int): Boolean = AttributeType.NOMINAL == attributes[a].type

	/**
	 * Agrega un Row a este DataSet, si el numero de columnas de ese Row
	 * es igual al numero de columnas de este DataSet.
	 *
	 * @param instance El Row a agregar a este DataSet
	 */
	fun add(instance: Instance) : Boolean {
		if (instance.size() != attributeSize) return false
		//if (!validClass(row.clazz)) return false

		// TODO verificar los atributos nominales y los cache de min y max
		mtInstances.add(instance)
		instance.setDataSet(this)
		return true
	}

	fun validClass(clazz: Int) = clazz in 0 until classSize

	/**
	 * Clasifica una instancia en base al algoritmo kNN. Primero encuentra
	 * los vecinos mas cercanos y retorna la clase mas votada entre ellos.
	 *
	 * @param k el numero de vecinos a encontrar.
	 * @param instance la instancia a clasificar.
	 */
	fun kNNClassification(k: Int, instance: Instance): Int? {
		val nearestNeighbors = findNearestNeighbors(instance, k)
		val votes: MutableMap<Int, Int> = mutableMapOf()
		nearestNeighbors.forEach { neighbor ->
			val clazz = neighbor.instance.clazz
			val c = votes[clazz]
			votes[clazz] = if (c == null) 1 else c + 1
		}
		return votes.maxBy { it.value }?.key
	}

	/**
	 * Encuentra los k vecinos mas cercanos, junto con sus distancias a la
	 * instancia especificada
	 */
	private fun findNearestNeighbors(instance: Instance, k: Int): List<Neighbor>
	{
		val neighbors: ArrayList<Neighbor> = arrayListOf()
		instances.forEach { i ->
			val distance = instance.distance(i)
			if (neighbors.isEmpty())
				neighbors.add(Neighbor(i, distance))
			else if (distance < neighbors.last().distance) {
				var index = 0
				while (distance > neighbors[index].distance) index++
				neighbors.add(index, Neighbor(i, distance))
				if (neighbors.size > k) neighbors.removeAt(k)
			}
		}
		log.trace("Vecinos cercanos encontrados: {}", neighbors)
		return neighbors
	}

	/**
	 * Elimina las instancias en las que la clase predicha por el clasificador
	 * k-NN es distinta de la clase real de la instancia.
	 *
	 * @param k el numero de vecinos cercanos.
	 */
	fun wilsonEditig(k: Int) {
		log.debug("Comienza proceso de suavizado de fronteras")
		var totalIt = 0
		var done = false
		while (!done) {
			totalIt++
			done = true
			val itrt = mtInstances.iterator()
			while (itrt.hasNext()) {
				val instance = itrt.next()
				if (instance.clazz != kNNClassification(k, instance)) {
					itrt.remove(); done = false
					log.debug("Instancia removida: {}", instance)
				}
			}
		}
		log.debug("Termina el proceso de suavizado de fronteras despues de {} iteraciones", totalIt)
	}

	/**
	 * Normaliza este DataSet mediante el metodo min-max.
	 *
	 * @param newMin el nuevo minimo para todas las columnas
	 * @param newMax el nuevo maximo para todas las columnas
	 */
	fun minMax(newMin: BigDecimal, newMax: BigDecimal) {
		mtInstances.forEach { row -> row.minmax(newMin, newMax) }
	}

	/**
	 * Normaliza este DataSet mediante el metodo Z-Score.
	 *
	 */
	fun zScore() { mtInstances.forEach { row -> row.zScore() } }

	/**
	 * Normaliza este DataSet mediante el metodo decimal-scaling.
	 *
	 */
	fun decimalScaling() {
		val maxOrderMagnitude = getMaxOrderMagnitude()
		mtInstances.forEach { row -> row.decimalScaling(maxOrderMagnitude) }
	}

	/**
	 * Obtiene el maximo orden de magnitud para cada atributo de este DataSet, y retorna
	 * el resultado en un arreglo de enteros. Si el atributo es de tipo nominal, entonces
	 * el resultado asignado para esa columna es 0
	 *
	 * @return un arreglo de enteros con los maximos ordenes de magnitud
	 */
	private fun getMaxOrderMagnitude(): IntArray {
		val j = IntArray(attributeSize)
		for (i in 0 until attributeSize) {
			if (isNumerical(i)) {
				var tenPower = 0
				var n = absoluteMax[i]
				while (n > ONE) {
					tenPower++
					n = n.movePointLeft(1)
				}
				j[i] = tenPower
			}
			else j[i] = 0
		}
		log.debug("Positions to move: {}", j.contentToString())
		return j
	}

	/**
	 * Calcula el minimo para cada atributo de este DataSet. Si el atributo es de tipo
	 * nominal, entonces el resultado asignado para esa columna es 0
	 */
	val maximum: Array<BigDecimal>
	get() {
		if (maxCache == null) {
			maxCache = Array(attributeSize) { ZERO }
			for (i in 0 until attributeSize)
				if (isNumerical(i))
					maxCache!![i] = getAttributeStream(i)
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
			minCache = Array(attributeSize) { ZERO }
			for (i in 0 until attributeSize)
				if (isNumerical(i))
					minCache!![i] = getAttributeStream(i)
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
			stdDvnCache = Array(attributeSize) { ZERO }
			for (i in 0 until attributeSize)
				if (isNumerical(i))
					stdDvnCache!![i] = MathTools
							.sqrt(getAttributeStream(i)
									.map { it.subtract(average[i]).pow(2) }
									.reduce(ZERO) { sum, augend -> sum.add(augend) }
									.divide(BigDecimal(mtInstances.size), Instance.precision, RoundingMode.HALF_UP),
									Instance.precision)
			log.info("Desviacion estandar calculada: {}", stdDvnCache!!.contentToString())
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
			avgCache = Array(attributeSize) { ZERO }
					.mapIndexed { i, _ ->
						if (isNumerical(i)) averageAtAtribute(i)
						else modeAtAttribute(i)
					}
					.toTypedArray()
			log.debug("Promedio calculado: {}", avgCache!!.contentToString())
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
			if (mtInstances.size == 0) ZERO
			else getAttributeStream(i)
					.reduce(ZERO) { sum, augend -> sum.add(augend) }
					.divide(BigDecimal(mtInstances.size), Instance.precision, RoundingMode.HALF_UP)

	/**
	 * Calcula la moda del i-esimo atributo de este DataSet.
	 *
	 * @return la moda del i-esimo atributo de este DataSet.
	 */
	private fun modeAtAttribute(n: Int): BigDecimal {
		val columnValues = getAttributeStream(n).toArray<BigDecimal> { arrayOfNulls(instanceSize)}
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
	 * Calcula el maximo absoluto para cada atributo de este DataSet. Si el atributo
	 * es de tipo nominal, entonces el resultado asignado para esa columna es 0
	 */
	val absoluteMax: Array<BigDecimal>
	get() {
		if (absMaxCache == null) {
			absMaxCache = Array(attributeSize) { ZERO }
			for (i in 0 until attributeSize)
				if (isNumerical(i))
					absMaxCache!![i] = getAttributeStream(i)
							.map { it.abs() }
							.max { o1, o2 -> o1.compareTo(o2) }
							.orElse(ZERO)
			log.info("Absolute maximum computed: ${absMaxCache!!.contentToString()}")
		}
		return absMaxCache!!
	}
	private var absMaxCache: Array<BigDecimal>? = null

	/**
	 * Obtiene un DataSet nuevo, el cual es una particion aleatoria de este DataSet.
	 * Los elementos del conjunto retornado son eliminados de este DataSet.
	 *
	 * @param porcentaje de las instancias que se incluiran en la particion.
	 */
	fun particionar(porcentaje: Double = 0.2): DataSet {
		val n = (instanceSize * porcentaje).toInt()
		val partition = DataSet(this.attributeSize, this.classSize)
		while (partition.instanceSize < n) {
			val rnd = mtInstances.random()
			if (!partition.contains(rnd)) {
				partition.add(rnd); mtInstances.remove(rnd)
			}
		}
		log.info("Termina particion del DataSet, nuevo tamano: {}", instanceSize)
		return partition
	}

	fun clonar(): DataSet {
		val ds = DataSet(attributeSize, classSize)
		instances.forEach { ds.add(it) }
		return ds
	}

	/**
	 * Representa este DataSet en un String
	 * @return la representacion textual de este DataSet
	 */
	override fun toString(): String =
			"DataSet{\n" +
			"	rowSize=" + mtInstances.size + "\n" +
			"	attributeSize=" + attributeSize + "\n" +
			"	classSize=" + classSize + "\n" +
			"	atributes=" + attributes + "\n" +
			"	instances={\n" +
					StringBuilder()
							.apply { instances.forEach {
								r -> this.append("\t\t").append(r).append("\n") } }
							.toString() +
			"	}\n" +
			"}"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as DataSet
		if (id != other.id) return false

		return true
	}

	override fun hashCode(): Int = id.hashCode()
}

package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.AttributeType
import mx.buap.fcc.clasificador.model.Attribute
import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.model.Row
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.math.BigDecimal

@Service
class DataSetFileService
{
	companion object { private const val separator = "," }

	/**
	 * Crea un DataSet a partir de un archivo CSV apuntado por el filaname.
	 *
	 * @param filename El filename del archivo csv a cargar
	 * @return El DataSet creado
	 * @throws IOException Si ocurre un error durante la lectura del archivo CSV
	 */
	@Throws(IOException::class)
	fun loadFromFile(filename: String): DataSet
	{
		return File(filename).useLines<DataSet> { sequence ->
			val iterator = sequence.iterator()

			val rowSize =
					if (iterator.hasNext()) iterator.next().split(separator)[0].toInt()
					else throw IOException("No se especifico el numero de renglones")
			val attSize =
					if (iterator.hasNext()) iterator.next().split(separator)[0].toInt()
					else throw IOException("No se especifico el numero de columnas")

			val attSplit = iterator.next().split(separator)
			if (attSplit.size != attSize)
				throw IOException("No se especifico correctamente el numero de atributos")

			val attributes = MutableList(attSize) { Attribute(AttributeType.NUMERICO) }
			for (i in attributes.indices) {
				val n = attSplit[i].toInt()
				if (n > 0) attributes[i] = Attribute(AttributeType.NOMINAL, n)
			}

			return DataSet(attributes = attributes)
					.apply {
						for (i in 1..rowSize) {
							if (!iterator.hasNext())
								throw IOException("No se especifico correctamente el numero de renglones")
							this.add(Row(
									indice = i,
									values = iterator.next()
											.split(separator).stream()
											.map { BigDecimal(it) }
											.toArray { length -> arrayOfNulls<BigDecimal>(length)}))
						}
					}
		}
	}
}

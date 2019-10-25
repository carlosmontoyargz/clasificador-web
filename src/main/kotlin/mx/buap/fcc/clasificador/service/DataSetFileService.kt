package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.model.Row
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.math.BigDecimal

@Service
class DataSetFileService
{
	companion object {
		const val delimiter = ",";
		const val basePath = "csv-samples" ;
		private val log = LogManager.getLogger()
	}

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
		return File("$basePath/$filename").useLines<DataSet> { sequence ->
			val rowItr = sequence.iterator()

			val rowSize =
					if (rowItr.hasNext()) rowItr.next().split(delimiter)[0].toInt()
					else throw IOException("No se especifico el numero de renglones")
			val attSize =
					if (rowItr.hasNext()) rowItr.next().split(delimiter)[0].toInt()
					else throw IOException("No se especifico el numero de columnas")
			val classSize =
					if (rowItr.hasNext()) rowItr.next().split(delimiter)[0].toInt()
					else throw IOException("No se especifico el numero de clases")
			log.debug("{} {} {}", rowSize, attSize, classSize)

			/*val attributes: List<String> = iterator.next().split(delimiter)
			if (attributes.size != attSize)
				throw IOException("No se especifico correctamente el numero de atributos")*/
			// attributes = attributes.map { str -> Attribute.fromInt(str.toInt()) }

			return DataSet(attSize, classSize)
					.apply {
						for (i in 1..rowSize) {
							if (!rowItr.hasNext())
								throw IOException("No se especifico correctamente el numero de renglones")

							val columnItr = rowItr.next().split(delimiter).iterator()
							add(Row(indice = i,
									values = Array(attSize) { BigDecimal(columnItr.next()) },
									clazz = columnItr.next().toInt()))
						}
					}
		}
	}
}

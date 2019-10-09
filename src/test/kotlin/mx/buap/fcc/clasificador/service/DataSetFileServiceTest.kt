package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.Attribute
import mx.buap.fcc.clasificador.model.AttributeType
import mx.buap.fcc.clasificador.model.AttributeType.*
import org.apache.logging.log4j.LogManager
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
class DataSetFileServiceTest
{
	@Autowired
	val dataSetFileService: DataSetFileService? = null

	private val log = LogManager.getLogger()

	@Test
	fun loadNumericDataSetFromFile() {
		val dataSet = dataSetFileService!!.loadFromFile("csv-samples/Heart694.csv")

		assertEquals(694, dataSet.rowsSize)
		assertEquals(14, dataSet.attributesSize)
		dataSet.attributes.forEach { c -> assertEquals(NUMERICAL, c.type)}

		log.info(dataSet)
	}

	@Test
	fun loadMixedDataSetFromFile() {
		val dataSet = dataSetFileService!!.loadFromFile("csv-samples/MixedData.csv")

		assertEquals(194, dataSet.rowsSize)
		assertEquals(29, dataSet.attributesSize)
		assertEquals(Attribute(NOMINAL, 6), dataSet.attributes[0])
		assertEquals(Attribute(NOMINAL, 4), dataSet.attributes[1])
		assertEquals(Attribute(NUMERICAL, 0), dataSet.attributes[2])
		assertEquals(Attribute(NUMERICAL, 0), dataSet.attributes[3])
		assertEquals(Attribute(NOMINAL, 10), dataSet.attributes[4])

		log.info(dataSet)
	}
}

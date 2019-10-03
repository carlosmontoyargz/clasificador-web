package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.AttributeType
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

		assertEquals(694, dataSet.rowSize)
		assertEquals(14, dataSet.columnSize)
		dataSet.attributes.forEach { c -> assertEquals(AttributeType.NUMERICO, c.type)}

		log.info(dataSet)
	}

	@Test
	fun loadMixedDataSetFromFile() {
		val dataSet = dataSetFileService!!.loadFromFile("csv-samples/MixedData.csv")

		assertEquals(194, dataSet.rowSize)
		assertEquals(29, dataSet.columnSize)
//		dataSet.columns.forEach { c -> assertEquals(AttributeType.NUMERICO, c.attributeType)}

		log.info(dataSet)
	}
}

package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.service.DataSetFileService
import org.apache.logging.log4j.LogManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal

@SpringBootTest
@RunWith(SpringRunner::class)
class DataSetTest
{
	@Autowired
	private val dataSetFileService: DataSetFileService? = null

	private val log = LogManager.getLogger()

	@Test
	fun minMax() {
		val dataSet = dataSetFileService!!.loadFromFile("csv-samples/Heart694.csv")
		log.info(dataSet)

		dataSet.minMax(BigDecimal.ZERO, BigDecimal.ONE)
		log.info(dataSet)
	}

	@Test
	fun zScore() {
		val dataSet = dataSetFileService!!.loadFromFile("csv-samples/Heart694.csv")
		log.info(dataSet)

		dataSet.zScore()
		log.info(dataSet)
	}
}

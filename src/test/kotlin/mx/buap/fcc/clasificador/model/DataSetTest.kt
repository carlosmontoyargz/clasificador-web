package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.service.DataSetFileService
import org.apache.logging.log4j.LogManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal.*

@SpringBootTest
@RunWith(SpringRunner::class)
class DataSetTest
{
	@Autowired
	private lateinit var dataSetFileService: DataSetFileService
	private lateinit var dataSetTest: DataSet

	private val log = LogManager.getLogger()

	/*@Before
	fun setDataSet() {
		dataSetTest = dataSetFileService.loadFromFile("Heart694.csv")
		log.info(dataSetTest)
	}

	@Test
	fun minMax() {
		dataSetTest.minMax(ZERO, ONE)
		log.info(dataSetTest)
	}

	@Test
	fun zScore() {
		dataSetTest.zScore()
		log.info(dataSetTest)
	}

	@Test
	fun decimalScaling() {
		dataSetTest.decimalScaling()
		log.info(dataSetTest)
	}*/
}

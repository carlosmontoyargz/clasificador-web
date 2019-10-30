package mx.buap.fcc.clasificador.model

import mx.buap.fcc.clasificador.service.DataSetFileService
import org.apache.logging.log4j.LogManager
import org.junit.Before
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
	private lateinit var dataSetFileService: DataSetFileService
	private lateinit var ds: DataSet

	private val log = LogManager.getLogger()

	@Before
	fun setDataSet() {
		ds = dataSetFileService.loadFromFile("seg-data.txt")
		log.info(ds)
	}

	@Test
	fun kNN() {
		val clazz = ds
				.kNNClassification(7,
						Instance(data = arrayOf(112.0, 180.0, 0.0, 0.0, 0.0, 1.3889, 1.294, 4.5556, 1.5587, 41.3704, 42.6667, 54.3333, 43.1111, -11.1111, 29.8889, -13.7778, 57.3333, 0.28765, -1.93)
								.map { BigDecimal(it) }.toTypedArray()))
		log.info(clazz)
	}

	/*
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

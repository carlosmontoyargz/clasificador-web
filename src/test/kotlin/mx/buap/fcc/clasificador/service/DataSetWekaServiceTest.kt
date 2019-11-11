package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.DataSet
import org.apache.logging.log4j.LogManager
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.jupiter.api.BeforeAll
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.classifiers.bayes.NaiveBayes
import weka.classifiers.trees.J48
import weka.core.Instances
import java.util.*

@SpringBootTest
@RunWith(SpringRunner::class)
class DataSetWekaServiceTest
{
	@Autowired lateinit var fileService: DataSetFileService
	@Autowired lateinit var wekaService: DataSetWekaService

	private var instances: Instances? = null

	private val log = LogManager.getLogger()

	@Before
	fun getWekaInstancesFromDataSet() {
		if (instances != null) return
		val dataSet = fileService.loadFromFile("seg-data.txt")
		instances = wekaService.getWekaInstancesFromDataSet(dataSet)
		log.info(instances)
	}

	@Test
	fun arbolDeDecision() {
		val tree = J48()
		tree.options = arrayOf("-U")
		tree.buildClassifier(instances)
		log.info(tree)

		val evaluation = Evaluation(instances)
		val numFolds = 5;
		evaluation.crossValidateModel(tree, instances, numFolds, Random(1))
		log.info(evaluation.toSummaryString())
	}

	@Test
	fun naiveBayes() {
		val naiveBayesClassifier = NaiveBayes()
		naiveBayesClassifier.buildClassifier(instances)
		log.info(naiveBayesClassifier)

		val naiveBayesEvaluation = Evaluation(instances)
		naiveBayesEvaluation.crossValidateModel(naiveBayesClassifier, instances, 5, Random(1))
		log.info(naiveBayesEvaluation.toSummaryString())
	}
}

package mx.buap.fcc.clasificador.service

import mx.buap.fcc.clasificador.model.DataSet
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.classifiers.bayes.NaiveBayes
import weka.classifiers.trees.J48
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instances
import kotlin.collections.ArrayList

@Service
class DataSetWekaService
{
	private val log = LogManager.getLogger()

	fun evaluateBayes(trainingSet: DataSet, testSet: DataSet): String {
		val wekaTrSet: Instances = getWekaInstancesFromDataSet(trainingSet)
		val naiveBayes = NaiveBayes().apply { buildClassifier(wekaTrSet) }
		return evaluate(naiveBayes, wekaTrSet, getWekaInstancesFromDataSet(testSet))
	}

	fun evaluateTree(trainingSet: DataSet, testSet: DataSet): String {
		val wekaTrSet: Instances = getWekaInstancesFromDataSet(trainingSet)
		val tree = J48().apply {
			options = arrayOf("-U");
			buildClassifier(wekaTrSet)
		}
		return evaluate(tree, wekaTrSet, getWekaInstancesFromDataSet(testSet))
	}

	private fun evaluate(classifier: Classifier, trainingSet: Instances, testSet: Instances): String
	{
		val evaluation = Evaluation(trainingSet)
		evaluation.evaluateModel(classifier, testSet)
		return "$classifier \n ${evaluation.toSummaryString()}"
	}

	fun getWekaInstancesFromDataSet(ds: DataSet): Instances {
		val classAtribute = Attribute("CLASS", ds.classes)
		val instances = Instances(
				ds.id,
				ArrayList<Attribute>(ds.attributeSize + 1)
						.apply {
							ds.attributes.indices.forEach { i ->
								add(Attribute(i.toString()))
							}
							add(classAtribute)
						},
				ds.instanceSize).apply { setClass(classAtribute) }

		ds.forEach { inst -> instances.add(
				DenseInstance(ds.attributeSize + 1)
						.apply {
							setDataset(instances)
							inst.data.forEachIndexed {
								i, value -> setValue(i, value.toDouble())
							}
							setClassValue(inst.clazz.toString())
						})
		}
		log.debug(instances)
		return instances
	}
}

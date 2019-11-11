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
import java.util.*
import kotlin.collections.ArrayList

@Service
class DataSetWekaService
{
	private val log = LogManager.getLogger()

	fun evaluateBayes(ds: DataSet, numFolds: Int): String {
		val instances: Instances = getWekaInstancesFromDataSet(ds)
		val naiveBayesClassifier = NaiveBayes()
		naiveBayesClassifier.buildClassifier(instances)
		return evaluate(naiveBayesClassifier, instances, numFolds)
	}

	fun evaluateTree(ds: DataSet, numFolds: Int): String {
		val instances: Instances = getWekaInstancesFromDataSet(ds)
		val tree = J48()
		tree.options = arrayOf("-U")
		tree.buildClassifier(instances)
		return evaluate(tree, instances, numFolds)
	}

	private fun evaluate(classifier: Classifier, instances: Instances, numFolds: Int): String
	{
		val evaluation = Evaluation(instances)
		evaluation.crossValidateModel(classifier, instances, numFolds, Random(1))
		val evalStr = "$classifier \n ${evaluation.toSummaryString()}"
		log.info(evalStr)
		return evalStr
	}

	fun getWekaInstancesFromDataSet(ds: DataSet): Instances {
		val classAtribute = Attribute(
				"CLASS",
				ArrayList<String>(ds.classSize).apply { addAll(ds.classes) })
		val instances = Instances(
				ds.id,
				ArrayList<Attribute>(ds.attributeSize + 1)
						.apply {
							ds.attributes.indices.forEach { i ->
								add(Attribute(i.toString())) }
							add(classAtribute)
						},
				ds.instanceSize)
				.apply { setClass(classAtribute) }
		ds.forEach { inst ->
			instances.add(
					DenseInstance(ds.attributeSize + 1)
							.apply {
								setDataset(instances)
								inst.data.forEachIndexed {
									i, value -> setValue(i, value.toDouble())
								}
		//						setValue(ds.attributeSize, inst.clazz.toDouble())
								setClassValue(inst.clazz.toString())
							})
		}
		log.debug(instances)
		return instances
	}
}

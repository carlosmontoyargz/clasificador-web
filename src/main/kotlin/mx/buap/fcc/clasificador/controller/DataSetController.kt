package mx.buap.fcc.clasificador.controller

import mx.buap.fcc.clasificador.dto.ClassificationResult
import mx.buap.fcc.clasificador.dto.ClassDTO
import mx.buap.fcc.clasificador.dto.ClassesDTO
import mx.buap.fcc.clasificador.dto.DataSetDTO
import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.model.NormalizationMethod
import mx.buap.fcc.clasificador.model.NormalizationMethod.*
import mx.buap.fcc.clasificador.service.DataSetFileService
import mx.buap.fcc.clasificador.service.DataSetWekaService
import org.apache.logging.log4j.LogManager
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.stream.Collectors

/**
 * Controlador web para realizar operaciones sobre datasets.
 */
@RestController
@RequestMapping("/dataset")
class DataSetController
	@Autowired constructor(
			val dataSetFileService: DataSetFileService,
			val dsWekaService: DataSetWekaService,
			val modelMapper: ModelMapper)
{
	private val log = LogManager.getLogger()

	/**
	 * Obtiene un dataset por su nombre.
	 */
	@GetMapping("/{id}")
	fun getById(@PathVariable id: String): ResponseEntity<DataSetDTO> =
			ResponseEntity.ok(
					modelMapper.map(
							dataSetFileService.loadFromFile(id),
							DataSetDTO::class.java))

	/**
	 * Realiza el proceso de suavizado de fronteras, normalizacion y analisis de entrenamiento
	 * de clasificadores.
	 *
	 * @param id es el nombre del dataset
	 * @param normalizacion es el metodo de normalizacion aplicado al dataset
	 *
	 * @return el resultado del analisis de clasificacion.
	 */
	@GetMapping("/{id}/performAnalysis")
	fun performAnalysis(@PathVariable id: String,
						@RequestParam normalizacion: NormalizationMethod?,
						@RequestParam(required = false, defaultValue = "7") k: Int)
			: ResponseEntity<ClassificationResult>
	{
		// Obtiene el dataset por id y lo normaliza mediante el algoritmo especificado
		val dataSet = dataSetFileService
				.loadFromFile(id)
				.apply {
					when(normalizacion) {
						MIN_MAX -> minMax(BigDecimal.ZERO, BigDecimal.ONE)
						Z_SCORE -> zScore()
						DECIMAL_SCALING -> decimalScaling()
					}
				}

		// El DataSet para evaluacion
		val testSet = dataSet.particionar()
		log.info("Conjunto de evaluacion\n {}", testSet)

		// El DataSet editado con fronteras suavizadas
		val editedDataSet = dataSet.clonar().apply { wilsonEditig(7) }

		return ResponseEntity
				.ok(ClassificationResult().apply {
					original = dataSetToClassesDTO(dataSet)
					suavizado = dataSetToClassesDTO(editedDataSet)
					bayesEvaluation = dsWekaService.evaluateBayes(dataSet, testSet)
					suavizadoBayesEvaluation = dsWekaService.evaluateBayes(editedDataSet, testSet)
					treeEvaluation = dsWekaService.evaluateTree(dataSet, testSet)
					suavizadoTreeEvaluation = dsWekaService.evaluateTree(editedDataSet, testSet)
				})
	}

	/**
	 * Convierte un dataset a un objeto de representacion de clases.
	 */
	private fun dataSetToClassesDTO(ds: DataSet): ClassesDTO {
		return ClassesDTO().apply {
			attributeSize = ds.attributeSize
			classes = Array(ds.classSize) { i ->
				ClassDTO().apply {
					name = i.toString()
					data = ds.instances.stream()
							.filter { it.clazz == i }
							.map { it.data }
							.collect(Collectors.toList())
				}
			}
		}
	}
}

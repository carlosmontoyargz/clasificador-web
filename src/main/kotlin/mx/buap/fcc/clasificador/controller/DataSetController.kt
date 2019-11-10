package mx.buap.fcc.clasificador.controller

import mx.buap.fcc.clasificador.dto.ClassificationResult
import mx.buap.fcc.clasificador.dto.ClassDTO
import mx.buap.fcc.clasificador.dto.ClassesDTO
import mx.buap.fcc.clasificador.dto.DataSetDTO
import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.model.NormalizationMethod
import mx.buap.fcc.clasificador.model.NormalizationMethod.*
import mx.buap.fcc.clasificador.service.DataSetFileService
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
			val modelMapper: ModelMapper)
{
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
						@RequestParam normalizacion: NormalizationMethod?)
			: ResponseEntity<ClassificationResult>
	{
		// Obtiene el dataset por id y lo normaliza mediante el algoritmo especificado
		val ds = dataSetFileService.loadFromFile(id)
				.apply {
					when(normalizacion) {
						MIN_MAX -> minMax(BigDecimal.ZERO, BigDecimal.ONE)
						Z_SCORE -> zScore()
						DECIMAL_SCALING -> decimalScaling()
					}
				}
		// Retorna el resultado de la clasificacion y suavizado
		return ResponseEntity.ok(ClassificationResult().apply {
			original = dataSetToClassesDTO(ds)
			suavizado = null
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

	@GetMapping("/{id}/classes")
	fun getClasses(@PathVariable id: String): ResponseEntity<Array<ClassDTO>> {
		val dataSet = dataSetFileService
				.loadFromFile(id)
				.apply { minMax(BigDecimal.ZERO, BigDecimal.TEN) }
		return ResponseEntity
				.ok(Array(dataSet.classSize) { i ->
					ClassDTO().apply {
						name = i.toString()
						data = dataSet.instances.stream()
								.filter { it.clazz == i }
								.map { it.data }
								.collect(Collectors.toList())
					}
				})
	}
}

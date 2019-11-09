package mx.buap.fcc.clasificador.controller

import mx.buap.fcc.clasificador.dto.ClassDTO
import mx.buap.fcc.clasificador.dto.DataSetDTO
import mx.buap.fcc.clasificador.service.DataSetFileService
import org.apache.logging.log4j.LogManager
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.stream.Collectors

@RestController
@RequestMapping("/dataset")
class DataSetController
	@Autowired constructor(
			val dataSetFileService: DataSetFileService,
			val modelMapper: ModelMapper)
{
	private val log = LogManager.getLogger()

	@GetMapping("/{id}")
	fun getById(@PathVariable id: String): ResponseEntity<DataSetDTO> =
			ResponseEntity.ok(
					modelMapper.map(
							dataSetFileService.loadFromFile(id),
							DataSetDTO::class.java))

	@GetMapping("/{id}/classes")
	fun getClasses(@PathVariable id: String): ResponseEntity<Array<ClassDTO>> {
		val dataSet = dataSetFileService
				.loadFromFile(id)
				.apply { minMax(BigDecimal.ZERO, BigDecimal.TEN) }
		return ResponseEntity
				.ok(Array(dataSet.classSize) { i ->
					ClassDTO().apply {
						name = i.toString()
						attributeSize = dataSet.attributeSize
						data = dataSet.instances.stream()
								.filter { it.clazz == i }
								.map { it.data }
								.collect(Collectors.toList())
					}
				})
	}
}

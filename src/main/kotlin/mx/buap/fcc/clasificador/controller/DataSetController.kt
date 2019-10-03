package mx.buap.fcc.clasificador.controller

import mx.buap.fcc.clasificador.dto.DataSetDTO
import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.service.DataSetFileService
import org.apache.logging.log4j.LogManager
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dataset")
class DataSetController
	@Autowired constructor(
			val dataSetFileService: DataSetFileService,
			val modelMapper: ModelMapper)
{
	private val log = LogManager.getLogger()

	@GetMapping("/{id}")
	fun getById(@PathVariable id: String) : ResponseEntity<DataSetDTO> =
			ResponseEntity.ok(
					modelMapper.map(
							dataSetFileService.loadFromFile("csv-samples/$id"),
							DataSetDTO::class.java))
}

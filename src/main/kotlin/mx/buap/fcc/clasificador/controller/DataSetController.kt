package mx.buap.fcc.clasificador.controller

import mx.buap.fcc.clasificador.model.DataSet
import mx.buap.fcc.clasificador.service.DataSetFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dataset")
class DataSetController
	@Autowired constructor(val dataSetFileService: DataSetFileService)
{
	@GetMapping("/{id}")
	fun getById(@PathVariable id: String) : ResponseEntity<DataSet> =
			ResponseEntity.ok(dataSetFileService.loadFromFile("csv-samples/$id"))
}

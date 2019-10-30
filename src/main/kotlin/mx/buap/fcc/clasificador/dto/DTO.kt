package mx.buap.fcc.clasificador.dto

import mx.buap.fcc.clasificador.model.AttributeType
import java.math.BigDecimal

open class DataSetDTO {
	var instanceSize: Int = 0
	var attributeSize: Int = 0
	var classSize: Int = 0
	var attributes: List<AttributeDTO> = emptyList()
	var instances: List<InstanceDTO> = emptyList()
}

open class AttributeDTO {
	var type: AttributeType? = null
	var size: Int = 0
}

open class InstanceDTO {
	var indice: Int = 0
	var clazz: Int = 0
	var data: Array<BigDecimal> = emptyArray()
}

open class ClassDTO {
	var name: String = ""
	var data: List<Array<BigDecimal>> = emptyList()
}

package mx.buap.fcc.clasificador.dto

import mx.buap.fcc.clasificador.model.AttributeType
import java.math.BigDecimal

open class DataSetDTO(
		var rowsSize: Int = 0,
		var attributesSize: Int = 0,
		var classSize: Int = 0,
		var attributes: List<AttributeDTO> = emptyList(),
		var rowList: List<RowDTO> = emptyList())

open class AttributeDTO(
		var type: AttributeType? = null,
		var size: Int = 0)

open class RowDTO(
		var indice: Int = 0,
		var clazz: Int = 0,
		var values: Array<BigDecimal> = emptyArray())

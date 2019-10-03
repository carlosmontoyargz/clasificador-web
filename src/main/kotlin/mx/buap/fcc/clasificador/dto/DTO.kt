package mx.buap.fcc.clasificador.dto

import mx.buap.fcc.clasificador.model.AttributeType
import java.math.BigDecimal

data class DataSetDTO(
		var rowSize: Int = 0,
		var columnSize: Int = 0,
		var attributes: List<AttributeDTO> = emptyList(),
		var rowList: List<RowDTO> = emptyList())

data class AttributeDTO(
		var type: AttributeType? = null,
		var size: Int = 0)

data class RowDTO(
		var indice: Int = 0,
		var values: Array<BigDecimal> = emptyArray())

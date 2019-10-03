package mx.buap.fcc.clasificador.dto

data class DataSetDTO
(
		var rowSize: Int = 0,
		var columnSize: Int = 0,
		var attributes: List<AttributeDTO> = emptyList(),
		var rowList: List<RowDTO> = emptyList()
)

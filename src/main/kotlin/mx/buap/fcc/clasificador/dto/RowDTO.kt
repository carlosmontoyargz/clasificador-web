package mx.buap.fcc.clasificador.dto

import java.math.BigDecimal

data class RowDTO(var indice: Int = 0, var values: Array<BigDecimal> = emptyArray())

package mx.buap.fcc.clasificador.config

import mx.buap.fcc.clasificador.dto.RowDTO
import mx.buap.fcc.clasificador.model.Row
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig
{
	@Bean
	fun modelMapper(): ModelMapper {
		val modelMapper = ModelMapper()
		return modelMapper
	}
}

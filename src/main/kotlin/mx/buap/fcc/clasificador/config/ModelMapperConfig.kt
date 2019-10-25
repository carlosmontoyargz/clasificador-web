package mx.buap.fcc.clasificador.config

import mx.buap.fcc.clasificador.dto.DataSetDTO
import mx.buap.fcc.clasificador.model.DataSet
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig
{
	@Bean
	fun modelMapper() = ModelMapper()
}

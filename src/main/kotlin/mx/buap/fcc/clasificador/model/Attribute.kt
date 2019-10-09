package mx.buap.fcc.clasificador.model

data class Attribute(val type: AttributeType, val size: Int = 0)
{
	companion object
	{
		fun fromInt(n: Int) =
				if (n > 0) Attribute(AttributeType.NOMINAL, n)
				else Attribute(AttributeType.NUMERICAL, 0)
	}
}

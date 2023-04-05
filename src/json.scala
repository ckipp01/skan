object json:
  def deserialize(data: String): Vector[DataItem] =
    upickle.default.read[Vector[DataItem]](data)

  def serialize(data: Array[DataItem]): String = upickle.default.write(data)

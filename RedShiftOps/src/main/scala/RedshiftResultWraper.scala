import java.sql.ResultSet


trait RedshiftResult {
  def getString(columnName : String) : String
  def getLong(columnName : String) : Long
  def getBoolean(columnName : String) : Boolean
  def next() : Boolean
}

case class RedshiftResultWrapper(resultSet: ResultSet) extends RedshiftResult{

  def getString(columnName : String) : String  = {
    resultSet.getString(columnName)
  }

  def getLong(columnName : String) : Long  = {
    resultSet.getLong(columnName)
  }

  def getBoolean(columnName : String) : Boolean  = {
    val v = resultSet.getBoolean(columnName)
    if (v == null) false else v
  }

  def next() : Boolean = {
    resultSet.next()
  }

}

object RedshiftResult {

  implicit def resultSetToResultWrapper(resultSet: ResultSet) : RedshiftResult = {
    RedshiftResultWrapper(resultSet)
  }
}

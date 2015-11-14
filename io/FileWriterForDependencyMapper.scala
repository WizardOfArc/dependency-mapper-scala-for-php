package dependencymapper.io

import java.io._

object FileWriterForDependencyMapper {
  def writeDependencyFile(name: String, dependencies: String) = {
    val outputFilename = name + "Dependencies.gv"
    val pw = new PrintWriter(new File(outputFilename))
    pw.write(dependencies)
    pw.close()
  }
}

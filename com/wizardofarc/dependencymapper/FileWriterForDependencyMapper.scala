package com.wizardofarc.dependencymapper

import java.io._

object FileWriterForDependencyMapper {
  def writeDependencyFile(name: String, dependencies: String, targetDirectoryName: String) = {
    val targetDir = new File(targetDirectoryName)
    if (!targetDir.exists){
      targetDir.mkdir
    }
    val outputFilename = targetDirectoryName + "/" + name + "Dependencies.gv"
    val pw = new PrintWriter(new File(outputFilename))
    pw.write(dependencies)
    pw.close()
  }
}

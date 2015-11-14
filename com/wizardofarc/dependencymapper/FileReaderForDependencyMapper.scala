package com.wizardofarc.dependencymapper 

  import java.nio.file.{ Files, Paths }
  import scala.io.Source

  object FileReaderForDependencyMapper {
    def getLineList(filename:String):List[String] = {
      if (Files.exists(Paths.get(filename))){
        Source.fromFile(filename).getLines().toList
      } else {
        Nil
      } 
    }
  }


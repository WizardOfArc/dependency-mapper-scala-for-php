package dependencymapper.io

import scala.util.matching.Regex 
import java.io.File

object FileFinderForDependencyMapper {
  def convertNameToFileName(name: String):String = {
    val nameParts = name.split('.')
      nameParts.length match {
      case 1 => nameParts(0) + ".php"
      case _ => name
    }
  }

  def makeRegexForFileSearch(filename: String): Regex = {
    val prefix = """(^|.*\W)"""
    val suffix = """$"""
    val regexString = prefix + filename + suffix
    regexString.r
  }

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
   val these = f.listFiles
   if ( these != null){
     val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
     good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_,r))
   } else {
     Array()
   }
  }

  def getFilePathsForTargetClassOrFile(startingDirName: String, targetName: String): Array[String] = {
    val filename = convertNameToFileName(targetName)
    val targetRegex = makeRegexForFileSearch(filename)
    val startingFile = new File(startingDirName)
    recursiveListFiles(startingFile, targetRegex).map(x => x.toString)  
  }

  def getLastArrayEntry[T](inputArray: Array[T]): T = {
    val last = inputArray.length - 1
    inputArray(last)
  } 

  def getShortNameFromFilePath(path: String): String = {
    val pathArray = path.split('/')
    getLastArrayEntry(pathArray)
  }

  def recursiveFilterlessListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveFilterlessListFiles)
  }

  def pathMapFromPathArray(pathArray: Array[File]): Map[String, String] = {
    @annotation.tailrec
    def mapFromArrayRecursive(arr: Array[File], aggregator: Map[String, String]): Map[String, String] = {
      if (arr == null || arr.length == 0) aggregator
      else {
        val filePath = arr(0).toString
        val tail = arr.slice(1, arr.length)
        val shortName = getShortNameFromFilePath(filePath)
        mapFromArrayRecursive(tail, aggregator + (shortName -> filePath))
      }
    }
    mapFromArrayRecursive(pathArray, Map[String, String]())
  }

} 

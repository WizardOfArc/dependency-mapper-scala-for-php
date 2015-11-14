package com.wizardofarc.dependencymapper

import scala.util.matching.Regex
import scala.collection.mutable

class DependencyFinder (name:String){
  val className = name
  
  // patterns
  val requirePattern = """require_once\([^"']*["']([^"']+)["']\)""".r
  val ancestorPattern = """(extends|implements) (\w+)""".r
  val staticPattern = """(\b(\w+))::""".r 
  val instancePattern = """\bnew (\w+)\(""".r
  // pattern groups
  val requireGroupIndex = 1
  val ancestorGroupIndex = 2
  val staticGroupIndex = 1
  val instanceGroupIndex = 1

  // filters
  val requireFilter = (_:String) => false
  val ancestorFilter = (_:String) => false
  val staticFilter = (s:String) => s == "static" || s == "self" || s == "this" || s == "parent" || s == name
  val instanceFilter = (s:String) => s == name || s == "self" || s == "static"

  var requireList = mutable.MutableList[String]()
  var ancestorList = mutable.MutableList[String]()
  var staticList = mutable.MutableList[String]()
  var instanceList = mutable.MutableList[String]()

  def getDependenciesFromLineList(lines:List[String]):Map[String,List[String]] = {
     for (line <- lines) {
       requireList ++= getFilteredMatches(line,requirePattern,requireGroupIndex,requireFilter)
       ancestorList ++= getFilteredMatches(line,ancestorPattern,ancestorGroupIndex,ancestorFilter)
       staticList ++= getFilteredMatches(line,staticPattern,staticGroupIndex,staticFilter)
       instanceList ++= getFilteredMatches(line,instancePattern,instanceGroupIndex,instanceFilter)
     }
     Map( 
       "requires" -> dedupeList(requireList.toList).map(convertRequirePathToName),
       "ancestors" -> dedupeList(ancestorList.toList),
       "statics" -> dedupeList(staticList.toList),
       "instances" -> dedupeList(instanceList.toList)
     )
   } 

  def convertRequirePathToName(path: String): String = {
    val pathBits = path.split('/')
    val fileName = pathBits(pathBits.length - 1)
    val fileNameParts = fileName.split('.')
    fileNameParts(1) match {
      case "php" => fileNameParts(0)
      case _ => fileName 
    }
  }

  def getFilteredMatches(input:String, pattern:scala.util.matching.Regex, groupIndex: Int, filter:String => Boolean): List[String] = {
    val allMatches = for (m <- pattern findAllMatchIn input) yield m group groupIndex
    val filteredMatches = allMatches.filterNot(filter)
    filteredMatches.toList
  }

  def dedupeList(strings: List[String]): List[String] = strings match {
    case Nil => strings
    case head::tail => head :: dedupeList(tail filterNot (_==head))
  }

}

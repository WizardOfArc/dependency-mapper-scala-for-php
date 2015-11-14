package com.wizardofarc.dependencymapper

import scala.util.matching.Regex 
import java.io.File

object FileFinderForDependencyMapper {
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

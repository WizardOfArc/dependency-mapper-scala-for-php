import java.io.File
import scala.collection.mutable
import com.wizardofarc.dependencymapper.{
  DependencyGrapher => DG,
  DependencyFinder => DF,
  FileReaderForDependencyMapper => FR,
  FileWriterForDependencyMapper => FW,
  FileFinderForDependencyMapper => FF
}

object DependencyMapper extends App {
  if (args.length != 2){
    println("USAGE:\n\tscala DependencyMapper <ClassName> <repo directory>\n")
    System.exit(1)
  }
  val massivePathsArray = FF.recursiveFilterlessListFiles(new File(args(1))) 
  val FileToPathMap = FF.pathMapFromPathArray(massivePathsArray)
  val initialTarget = args(0)
  val bodyPrefix = "digraph DependenciesMap {\nlabel=\"Dependency Map for " + initialTarget + "\"\n"
  val bodyDelim = "\n"
  val bodySuffix = "\n}"

  var visited = mutable.Map[String, String]()
  var queue = mutable.Queue[String]()
  
  def getRepoFromPath(filePath: String): String = {
    if ( !filePath.contains("/vendor/") ) {
      "email"
    } else if (filePath.contains("/vendor/trulia/web/")){
      "web"
    } else if (filePath.contains("/vendor/trulia/web-common/")){
      "common"
    } else if (filePath.contains("/vendor/trulia/db-handle/")){
      "db_handle"
    } else {
      "external"
    }
  }

  def isInTrackingRepo(path: String): Boolean = {
    val repo = getRepoFromPath(path)
    repo match {
     case "email" => true
     case "common" => true
     case "web" => true
     case "db_handle" => true
     case _ => false
    }
  }

  def traverseDependencyNode(targetName: String): Unit = {
    val fileName = FF.convertNameToFileName(targetName)
    if (FileToPathMap.contains(fileName) && isInTrackingRepo(FileToPathMap(fileName))){
      val path = FileToPathMap(fileName)
      val lines = FR.getLineList(path)
      val df = new DF(targetName)
      val dependencies = df.getDependenciesFromLineList(lines)
      visited(targetName) = DG.graphDependencies(
        targetName,
        getRepoFromPath(path),
        dependencies("requires"),
        dependencies("ancestors"),
        dependencies("statics"),
        dependencies("instances")
      )
      for (depType <- dependencies.keys){
        for (dependency <- dependencies(depType)){
          if(!queue.contains(dependency) && !visited.contains(dependency)){
            queue.enqueue(dependency)
          }
        }
      }
    } else {
      visited(targetName) = DG.graphDependencies(
        targetName,
        "",
        Nil, Nil, Nil, Nil
      )
    }
  } 

  @annotation.tailrec
  def recursiveBFS(currentNode:String): Unit = {
    if (!queue.isEmpty){
      val newNode = queue.dequeue
      if(!visited.contains(newNode)){
       traverseDependencyNode(newNode)
       recursiveBFS(newNode)
      }
    } 
  }

  queue.enqueue(initialTarget)
  while(!queue.isEmpty){
    traverseDependencyNode(queue.dequeue)
  }
  
  val body = visited.values.toList.mkString(bodyPrefix, bodyDelim, bodySuffix) // this will be the main result
  FW.writeDependencyFile(initialTarget,body)
 
}


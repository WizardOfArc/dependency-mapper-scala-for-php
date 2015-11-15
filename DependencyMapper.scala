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
  if (args.length != 3){
    println("USAGE:\n\tscala DependencyMapper <ClassName> <directory to search in> <directory to write result file in>\n")
    System.exit(1)
  }
  val inputDir = args(1)
  val targetDirectoryName = args(2)
  val seedDirectory = new File(inputDir)
  if (!seedDirectory.exists){
    println(s"Starting directory, $inputDir, does not exist")
    if (inputDir.slice(0,1) == "~")
      println("Use relative or absolute paths...  I don't like ~'s")
    System.exit(1)
  }
  val massivePathsArray = FF.recursiveFilterlessListFiles(seedDirectory) 
  val FileToPathMap = FF.pathMapFromPathArray(massivePathsArray)
  val initialTarget = args(0)
  val bodyPrefix = "digraph DependenciesMap {\nlabel=\"Dependency Map for " + initialTarget + "\"\n"
  val bodyDelim = "\n"
  val bodySuffix = "\n}"

  var visited = mutable.Map[String, String]()
  var queue = mutable.Queue[String]()
  
  def traverseDependencyNode(targetName: String): Unit = {
    val fileName = FF.convertNameToFileName(targetName)
    if (FileToPathMap.contains(fileName)){
      val path = FileToPathMap(fileName)
      val lines = FR.getLineList(path)
      val df = new DF(targetName)
      val dependencies = df.getDependenciesFromLineList(lines)
      visited(targetName) = DG.graphDependencies(
        targetName,
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
        Nil, Nil, Nil, Nil
      )
    }
  } 

  queue.enqueue(initialTarget)
  while(!queue.isEmpty){
    traverseDependencyNode(queue.dequeue)
  }
  
  val body = visited.values.toList.mkString(bodyPrefix, bodyDelim, bodySuffix) // this will be the main result
  FW.writeDependencyFile(initialTarget,body, targetDirectoryName)
  println("look in " + targetDirectoryName + " for the file called \"" + initialTarget + "Dependencies.gv\"") 
}


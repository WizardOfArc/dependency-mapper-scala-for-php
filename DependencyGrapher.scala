package dependencymapper

object DependencyGrapher {

  def graphDependencies(
    rawName: String,
    repo: String,
    requires: List[String],
    ancestors: List[String],
    statics: List[String],
    instances: List[String]
   ): String = {
     val name = makeNameSafeForGraphiz(rawName)
     if (requires.isEmpty && ancestors.isEmpty && instances.isEmpty && statics.isEmpty) {
       getLeafCode(name, repo)
     } else {
       getNodeCode(name, repo) + 
       getDependencyListCode(name, requires, "orange") +
       getDependencyListCode(name, ancestors, "blue") +
       getDependencyListCode(name, statics, "red") +
       getDependencyListCode(name, instances, "green")
     }
  }

  def getLeafCode(name: String, repo: String):String = {
    val colorSetting = repo match {
      case "email" => "brown, fontcolor=white]"
      case "web" => "black, fontcolor=white]"
      case "common" => "purple, fontcolor=white]"
      case "db_handle" => "blue, fontcolor=white]"
      case _ => "orange]"
    }
    name + " [style=filled, color=" + colorSetting + "\n"
  }

  def getNodeCode(name: String, repo: String): String = {
    val colorSetting = repo match {
      case "email" => "]"
      case "web" => ", color=red, fontcolor=white]"
      case "db_handle" => ", color=yellow]"
      case "common" => ", color=green]"
      case _ => ", color=orange]"
    }
    name + " [style=filled" + colorSetting + "\n"
  }

  def getDependencyListCode(name: String, dependencies:List[String], color:String):String = {
    if (dependencies.isEmpty){
      ""
    } else {
      val openString = name + " -> {"
      val closeString = "        }[color=" + color + "]"
      val dependenciesString = dependencies map makeNameSafeForGraphiz mkString "\n        "
      openString + "\n        " + dependenciesString + "\n" + closeString + "\n"
    }
  }

  def makeNameSafeForGraphiz(name: String): String = {
    name.split('.').mkString("DOT")
  }
}

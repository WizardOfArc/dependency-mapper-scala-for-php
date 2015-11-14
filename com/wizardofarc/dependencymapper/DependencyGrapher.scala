package com.wizardofarc.dependencymapper


object DependencyGrapher {

  def graphDependencies(
    rawName: String,
    requires: List[String],
    ancestors: List[String],
    statics: List[String],
    instances: List[String]
   ): String = {
     val name = makeNameSafeForGraphiz(rawName)
     if (requires.isEmpty && ancestors.isEmpty && instances.isEmpty && statics.isEmpty) {
       getLeafCode(name)
     } else {
       getNodeCode(name) + 
       getDependencyListCode(name, requires, "orange") +
       getDependencyListCode(name, ancestors, "blue") +
       getDependencyListCode(name, statics, "red") +
       getDependencyListCode(name, instances, "green")
     }
  }

  def getLeafCode(name: String):String = {
    name + " [style=filled, color=black, fontcolor=white]\n"
  }

  def getNodeCode(name: String): String = {
    name + " [style=filled, color=red, fontcolor=white]\n"
  }

  def getDependencyListCode(name: String, dependencies:List[String], color:String):String = {
    if (dependencies.isEmpty){
      ""
    } else {
      val openString = name + " -> {"
      val closeString = "        }[color=" + color + "]"
      val dependenciesString = dependencies mkString "\n        "
      openString + "\n        " + dependenciesString + "\n" + closeString + "\n"
    }
  }

  def makeNameSafeForGraphiz(name: String): String = {
    name.split('.').mkString("DOT")
  }
}

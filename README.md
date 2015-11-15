# dependency-mapper-scala-for-php
A Scala application to map out dependencies in a PHP codebase and output a DOT language file to be used by Graphviz
----
*USAGE:*<br/>
<code>scala DependencyMapper _InitialClass_ "_SearchDirectory_" "_DestinationDirectory_"</code><br/>
generates a file in dot lang called **_DestinationDirectory_/<em>InitialClass</em>Dependencies.gv**


_a bit of a warning about larger codebases:_
<p>Although this application can handle a large codebase and generate a .gv file tens of thousands of lines long, Graphviz itself cannot handle a file that large.</p>

<p>You can still use the generated file to find dependencies with your favorite text editor...  and then use this app for smaller subsets of the code base to actually generate graphs</p> 

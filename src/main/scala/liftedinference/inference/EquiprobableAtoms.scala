/*
 * Copyright 2015 Guy Van den Broeck and Wannes Meert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liftedinference.inference

import scala.collection._

import liftedinference._
import liftedinference.compiler._
import liftedinference.examples.models._
import liftedinference.inference._
import scala.util.Random._
import util.KLD._

class EquiprobableAtoms(val weightedCNF: WeightedCNF, val verbose: Boolean = false) {

  // Should also support hard clauses!
  //    // require that the CNF is compiled from an MLN with only soft clauses.
  //    require(weightedCNF.cnf.clauses.forall { _.predicates.exists { _.name.name.startsWith("f_") } })

  val domainSizes = weightedCNF.domainSizes
  val originalPredicateWeights = weightedCNF.predicateWeights

  val (factors, fgPredicates) = {
    val (factors, fgPredicates) = weightedCNF.vocabularyPredicates.partition { _.toString.startsWith("f_") }
    if (verbose) {
      println("Factors")
      println(factors.mkString("\n"))
      println
      println("FG Predicates")
      println(fgPredicates.mkString("\n"))
      println
    }
    (factors, fgPredicates)
  }

  // First co-shatter the CNF. 
  // This will have to happen eventually anyway, when introducing the compensating factors
  lazy val coshatteredCnf = {
    //coshatter, but also remove tautologies introduced by coShattering
    val coshatteredCnf = weightedCNF.cnf.coShatter.removeTautologies
    if (verbose) {
      println("Original CNF")
      println(weightedCNF.cnf)
      println
      println("CoShattered CNF")
      println(coshatteredCnf)
      println
    }
    coshatteredCnf
  }

  lazy val coshatteredFgCAtoms = {
    val coshatteredFgCAtoms = coshatteredCnf.distinctPositiveUnitClauses.filter { catom =>
      fgPredicates.contains(catom.atom.predicate)
    }
    if (verbose) {
      println("Coshattered FG CAtoms")
      println(coshatteredFgCAtoms.mkString("\n"))
      println
    }
    coshatteredFgCAtoms
  }

  //    lazy val equiprobableClassesWithInstances = coshatteredFgCAtoms.map{ case catom =>
  //      catom.groundLiterals
  //    }

}

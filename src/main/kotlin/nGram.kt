import java.io.File
import kotlin.math.log10

/**
 * Created by: Adriano Silva (20/11/2020)
 * Class that contains Bigrams, Trigrams or Quadgrams from the preexisting files
 */
class nGram (nGramFile: String, sep: String = " ") {

    // Key-value map of N-Gram and its count
    var nGramMap = mutableMapOf<String, Double>()

    // Length of this instances N-Gram (basically if it's Bi, Tri or Quad)
    var nGramLength: Int

    // total counts of this N-Gram for statistic calculations
    var totalCounts: Double

    // Since we use log probabilities, this variable will contain the floor-value (lowest possible)
    // for N-Grams which are extremely unlikely and are not present in the preexisting files
    var floorProbability: Double

    init {
        // Create an entry in the map for each line in the file
        File(nGramFile).forEachLine{
            nGramMap[it.split(sep)[0]] = it.split(sep)[1].toDouble()
        }

        // define N-Gram instance values
        nGramLength = nGramMap.keys.first().length
        totalCounts = nGramMap.values.sum()
        floorProbability = log10(0.01/totalCounts)

        // calculate Log probabilities with each count value (to stop possible underflow!)
        nGramMap.keys.forEach {
            nGramMap[it] = log10((nGramMap[it]?.div(totalCounts)!!))
        }
    }

    /**
     * Function that calculates a Fitness score using the Log probabilities
     * log(a*b) = log(a)+log(b) is used as reference to calculate the final score
     */
    fun fitnessScore(text: String): Double {
        var score = 0.0
        for (i in 0..text.length - nGramLength)
            // substring correct sizes for each N-Gram
            if (nGramMap.containsKey(text.substring(i, i + nGramLength))) {
                score += nGramMap[text.substring(i, i + nGramLength)]!!
            } else {
                score += floorProbability
            }
        return score
    }
}
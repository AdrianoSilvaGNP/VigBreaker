import java.io.File
import kotlin.math.log10

class nGram (nGramFile: String, sep: String = " ") {

    var nGramMap = mutableMapOf<String, Double>()
    var nGramLength: Int
    var totalCounts: Double
    var floorProbability: Double

    init {
        File(nGramFile).forEachLine{
            nGramMap[it.split(sep)[0]] = it.split(sep)[1].toDouble()
        }
        nGramLength = nGramMap.keys.first().length
        totalCounts = nGramMap.values.sum()

        // calculate Log probabilities (to stop possible underflows)
        nGramMap.keys.forEach {
            nGramMap[it] = log10((nGramMap[it]?.div(totalCounts)!!))
        }
        floorProbability = log10(0.01/totalCounts)
    }

    fun fitnessScore(text: String): Double {
        val score: Double
        if (nGramMap.containsKey(text)) {
            score = nGramMap[text]!!
        } else {
            score = floorProbability
        }
        return score
    }
}
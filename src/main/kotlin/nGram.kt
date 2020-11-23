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
        var score = 0.0
        for (i in 0..text.length - nGramLength + 1)
        if (nGramMap.containsKey(text.substring(i, i + nGramLength))) {
            score = nGramMap[text.substring(i, i + nGramLength)]!!
        } else {
            score = floorProbability
        }
        return score
    }
}
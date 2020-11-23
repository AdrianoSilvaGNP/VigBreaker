import com.github.shiguruikai.combinatoricskt.permutations
import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition

fun main(args: Array<String>) {

    val triGram = nGram("english_trigrams.txt")
    val quadGram = nGram("english_quadgrams.txt")
    val cypherText = "kiqpbkxspshwehospzqhoinlgapp"
    val recordLenght = 100

    for (keyLength in 3..20) {
        // each record holds score, key and cleartext
        var record = mutableListOf<Triple<Double, String, String>>()

        for (i in "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toMutableList().permutations(3)) {
            val key = i.joinToString(separator = "")
            val text = vigenere(cypherText, key, false)
            var fitness = 0.0
            for (j in 0..text.length step keyLength) {
                if (text.length - j < 3)
                    fitness += 0
                else
                    fitness += triGram.fitnessScore(text.substring(j, j + 3))
            }
            record.add(Triple(fitness, key, text))
            record.sortByDescending { triple -> triple.first }
            if (record.size > recordLenght) {
                record = record.subList(0, recordLenght)
            }
        }

        var nextRecord = mutableListOf<Triple<Double, String, String>>()
        for (i in 0..(keyLength - 3)) {
            for (k in 0..99) {
                for (c in "ABCDEFGHIJKLMNOPQRSTUVWXYZ") {
                    val key = record[k].second + c
                    //val fullKey = key + "A" * (keyLength - key.length)
                    val text = vigenere(cypherText, key, false)
                    var fitness = 0.0
                    for (j in 0..text.length step keyLength) {
                        if (text.length - j < 4)
                            fitness += quadGram.fitnessScore(text.substring(j, j + key.length))
                    }
                    nextRecord.add(Triple(fitness, key, text))
                    nextRecord.sortByDescending { triple -> triple.first }
                    if (nextRecord.size > recordLenght) {
                        nextRecord = nextRecord.subList(0, recordLenght)
                    }
                }
            }
            record = nextRecord
            nextRecord = mutableListOf()
        }



        var bestKey = record[0].second
        val decyText = vigenere(cypherText, bestKey, false)
        var bestScore = quadGram.fitnessScore(decyText)
        for (i in 0..99) {
            val text = vigenere(cypherText, record[i].second, false)
            val score = quadGram.fitnessScore(text)
            if (score > bestScore) {
                bestKey = record[i].second
                bestScore = score
            }
        }
        /*var bestScore = 0.0
        for (j in 0..decyText.length step keyLength) {
            if (decyText.length - j < 4)
                bestScore += 0
            else
                bestScore += quadGram.fitnessScore(decyText.substring(j, j + 4))
        }*/


        println("$bestScore, key: $keyLength $bestKey, $decyText")

    }


}

fun vigenere(text: String, key: String, encrypt: Boolean = true): String {
    val t = if (encrypt) text.toUpperCase() else text.toUpperCase()
    val sb = StringBuilder()
    var ki = 0
    for (c in t) {
        if (c !in 'A'..'Z') continue
        val ci = if (encrypt)
            (c.toInt() + key[ki].toInt() - 130) % 26
        else
            (c.toInt() - key[ki].toInt() +  26) % 26
        sb.append((ci + 65).toChar())
        ki = (ki + 1) % key.length
    }
    return sb.toString()
}
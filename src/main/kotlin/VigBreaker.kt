import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition

/**
 * Created by: Adriano Silva (20/11/2020)
 * VigBreaker - Vigenere cipher breaker using N-Gram frequency analysis + bruteforce
 */
fun main() {

    // Load N-Gram files
    val monoGram = nGram("english_monograms.txt") // using lower N-Grams seems to improve performance but should mathematically degrade precision (I could be wrong needs to be investigated)
    val biGram = nGram("english_bigrams.txt")
    val triGram = nGram("english_trigrams.txt")
    val quadGram = nGram("english_quadgrams.txt")

    // cyphertext and chosen N-Gram
    //val cypherText = "LPROZ OOGRJ ZGFLV TUKMC WFDQM PZXIJ LVRWQ XEOSZ ZHTEK UYSCR PTFCZ UHXIJ LPPTD CPRBY OSMGY TLEVD UAQMF IFMZV LVYTO QDLHX LBPLL KYCQY ODRKS ACTEU XZEVO UAQMF OSDSU BKMBJ QEORF WFQCK HKSOD INGJZ SHGVV LMSZD WWHFJ AVQGF NUWMW AOIXT CSRYC YPPTP LFUCR AVQHR RVRES QCKHM LGARF YHXZP CSNWS NCRQV GHRLR ZEDHF JVUPC XZJQC ATISQ SCGXN BALWY MAQCY OSD"
    //val cypherText = "Vyc hxfxvp tgqqllixfgu yc bbvvpctz gipdk ct dghvcpwgvnfckgdg opu upl iprzax hq tmbizgkc nhit icfnsuk"
    //val cypherText = "Hmzlbh fj gvrycglwaj eckpgfrxzg vmma fp mgvqk r nmxmrw fj kvilvngjrq Grzagv pmglgzh iejwr bq xyz tkxgiiw qn p ripoceg Xyjcml glzw eqeoii ag rdwp ow arqiiwvick eev wzspvhmtx ssi xjztl gvfhhumvn qz vrwzwvms hpc shghqgoa zs ovvem qi"
    val cypherText = "VVRQI EREOY LDPTT MWNFL ECKAV MZPWE EHRZK UHXHI KCISC BGBZH LHEPK DSERK AEESJ KOLIF ZJKHB SXSZK SALUA ZPGVX EOKIX OZEIQ VHBHF HWFJI MITSP XHCZS JTYWH VTRSW KVMSG QTKSY WYMOF XQPSH IGSOH GMVXC ITPKW YZXAH JVRSK ZWGXT RMTXW AGFDV IQGTK SVXEM OMFWN OFOR"
    //val cypherText = "bqnnmsbumizadyqighrr"
    val chosenNGram = quadGram

    // length of kept records (decreasing improves performance but lowers precision/success rate)
    val recordLenght = 1000

    for (keyLength in 52..52) {
        // create record list (each record holds score, key and cleartext)
        var record = mutableListOf<Triple<Double, String, String>>()

        // Loop to record best keys via N-Gram scores.
        // Use permutations for the minimum key length. This is done for performance.
        // Using permutations on every key length is a CPU killer!
        for (i in "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toMutableList().permutationsWithRepetition(3)) {
            val key = i.joinToString(separator = "") + "A".repeat(keyLength - i.size) // Always keep original key length for better results!
            val text = vigenere(cypherText, key, false)

            val fitness = chosenNGram.fitnessScore(text)

            record.add(Triple(fitness, i.joinToString(separator = ""), text))
            record.sortByDescending { triple -> triple.first } // sort by lowest score -> closer to 0 is better
            if (record.size > recordLenght) {
                record = record.subList(0, recordLenght) // keep defined record length
            }
        }

        // Using only the best N records from the permutation phase to create a new record if keyLength is > 3
        var nextRecord = mutableListOf<Triple<Double, String, String>>()
        for (i in 0..(keyLength - 4)) {
            //println("current keysize cycle: ${i + 3}")
            for (k in 0 until recordLenght) {
                for (c in "ABCDEFGHIJKLMNOPQRSTUVWXYZ") {
                    val key = record[k].second + c
                    val fullKey = key + "A".repeat(keyLength - key.length) // Always keep original key length for better results!
                    val text = vigenere(cypherText, fullKey, false)

                    val fitness = chosenNGram.fitnessScore(text)

                    nextRecord.add(Triple(fitness, key, text))
                    nextRecord.sortByDescending { triple -> triple.first }
                    if (nextRecord.size > recordLenght) {
                        nextRecord = nextRecord.subList(0, recordLenght)
                    }
                }
            }
            record = nextRecord // use results for next keyLength
            nextRecord = mutableListOf()
        }

        // show most probable result
        val bestKey = record[0].second
        val decyText = record[0].third//vigenere(cypherText, bestKey, false)
        val bestScore = record[0].first //chosenNGram.fitnessScore(decyText)

        println("$bestScore, key: $keyLength $bestKey, $decyText")
    }
}

/**
 * Vigenere cipher implementation
 */
fun vigenere(text: String, key: String, encrypt: Boolean = true): String {
    val t = text.toUpperCase()
    val sb = StringBuilder()
    var ki = 0
    for (c in t) {
        if (c !in 'A'..'Z') continue
        val ci = if (encrypt)
            (c.toInt() + key[ki].toInt() - 130) % 26
        else
            (c.toInt() - key[ki].toInt() + 26) % 26
        sb.append((ci + 65).toChar())
        ki = (ki + 1) % key.length
    }
    return sb.toString()
}